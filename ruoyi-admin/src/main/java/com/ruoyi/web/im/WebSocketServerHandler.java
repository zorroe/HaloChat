package com.ruoyi.web.im;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.web.domain.entity.SysChatMessage;
import com.ruoyi.web.domain.model.LoginUser;
import com.ruoyi.web.enums.ChatMessageStatus;
import com.ruoyi.web.enums.ChatMessageType;
import com.ruoyi.web.service.ISysChatMessageService;
import com.ruoyi.web.service.TokenService;
import com.ruoyi.web.utils.SnowflakeIdWorker;
import com.ruoyi.web.utils.spring.SpringUtils;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

@Slf4j
public class WebSocketServerHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    private static final String ONLINE_KEY_PREFIX = "im:user:online:";
    private static final String OFFLINE_QUEUE_PREFIX = "im:offline:";
    private static final String UNREAD_PREFIX = "im:unread:"; // Hash: key UNREAD_PREFIX+to, field=from, val=count

    private final ISysChatMessageService messageService = SpringUtils.getBean(ISysChatMessageService.class);
    private final SnowflakeIdWorker idWorker = SpringUtils.getBean(SnowflakeIdWorker.class);
    private final RedisTemplate<Object, Object> redisTemplate = SpringUtils.getBean("redisTemplate");
    private final TokenService tokenService = SpringUtils.getBean(TokenService.class);

    private String currentUserId;

    /**
     * 当Channel活跃时（连接建立成功）触发
     * 此时用户还未登录，需等待登录验证后再关联userId和Channel
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("客户端连接成功：" + ctx.channel().remoteAddress());
        super.channelActive(ctx);
    }


    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof WebSocketServerProtocolHandler.HandshakeComplete) {
            // 获取userId参数
            // Netty未直接提供，此处通过channel的pipeline上下文传递较复杂。简化：从握手事件中获取请求URI
            WebSocketServerProtocolHandler.HandshakeComplete e = (WebSocketServerProtocolHandler.HandshakeComplete) evt;
            String authorization = e.requestHeaders().get("Authorization");
            LoginUser loginUser = null;
            if (authorization != null) {
                String token = authorization.replace("Bearer ", "");
                loginUser = tokenService.getLoginUser(token);
            }
            if (Objects.nonNull(loginUser)) {
                currentUserId = loginUser.getUserId();
                ChannelRegistry.bind(currentUserId, ctx.channel());
                // 标记在线
                redisTemplate.opsForValue().set(ONLINE_KEY_PREFIX + currentUserId, "1");
                // 尝试推送离线消息
                drainOfflineAndPush(currentUserId, ctx.channel());
            } else {
                ctx.close();
            }
        } else if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state() == IdleState.READER_IDLE) { // 读超时（客户端未发消息）
                log.error("心跳超时，强制离线：userId = {}", currentUserId);
                ctx.channel().close(); // 关闭连接，会触发channelInactive
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("连接异常：{}", cause.getMessage());
        ctx.channel().close(); // 关闭连接，触发离线处理
    }


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) {
        String text = msg.text();
        JSONObject json = JSON.parseObject(text);
        String toUserId = json.getString("toUserId");
        String content = json.getString("content");
        String msgType = json.getString("msgType");
        log.info("收到消息：{}", json);

        if (ChatMessageType.HEARTBEAT.getValue().equals(msgType)) {
            log.info("收到心跳：{}", json);
            Channel channel = ctx.channel();
            HashMap<String, String> map = new HashMap<>();
            map.put("timestamp", DateUtil.format(new Date(), DatePattern.NORM_DATETIME_PATTERN));
            map.put("msgType", "heartbeat");
            channel.writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(map)));
            return;
        }

        if (currentUserId == null || toUserId == null || Objects.equals(currentUserId, toUserId)) {
            return;
        }

        // 构造并持久化消息
        String messageId = idWorker.nextStringId();
        SysChatMessage entity = new SysChatMessage();
        entity.setId(messageId);
        entity.setFromUserId(currentUserId);
        entity.setToUserId(toUserId);
        entity.setContent(content);
        entity.setMsgType(msgType == null ? ChatMessageType.TEXT.getValue() : msgType);
        entity.setStatus(ChatMessageStatus.NOT_DELIVERED.getValue());
        entity.setCreateTime(LocalDateTime.now());
        messageService.save(entity);

        // 在线则推送，不在线入队
        boolean delivered = pushIfOnline(toUserId, entity);
        if (delivered) {
            messageService.markAsDelivered(messageId);
        } else {
            // 入离线队列
            redisTemplate.opsForList().rightPush(OFFLINE_QUEUE_PREFIX + toUserId, messageId);
            // 增加未读计数
            redisTemplate.opsForHash().increment(UNREAD_PREFIX + toUserId, currentUserId, 1);
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        if (currentUserId != null) {
            log.info("用户离线：userId=" + currentUserId);
            ChannelRegistry.unbind(currentUserId);
            redisTemplate.delete(ONLINE_KEY_PREFIX + currentUserId);
        }
    }

    private boolean pushIfOnline(String toUserId, SysChatMessage entity) {
        Channel ch = ChannelRegistry.get(toUserId);
        if (ch != null && ch.isActive()) {
            ch.writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(entity)));
            return true;
        }
        return false;
    }

    private void drainOfflineAndPush(String userId, Channel channel) {
        String key = OFFLINE_QUEUE_PREFIX + userId;
        while (true) {
            Object val = redisTemplate.opsForList().leftPop(key);
            if (val == null) break;
            String messageId = String.valueOf(val);
            SysChatMessage m = messageService.getById(messageId);
            if (m != null) {
                channel.writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(m)));
                messageService.markAsDelivered(messageId);
                // 清理未读计数（按会话维度需要fromUserId，此处不便一一减少，延后至客户端回执已读时）
            }
        }
        // 上线后可选：返回当前所有会话未读数
    }
}
