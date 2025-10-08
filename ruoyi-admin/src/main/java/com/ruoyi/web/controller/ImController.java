package com.ruoyi.web.controller;

import com.alibaba.fastjson2.JSON;
import com.ruoyi.web.domain.AjaxResult;
import com.ruoyi.web.domain.entity.SysChatMessage;
import com.ruoyi.web.domain.model.ChatMessageBody;
import com.ruoyi.web.domain.model.LoginUser;
import com.ruoyi.web.enums.ChatMessageStatus;
import com.ruoyi.web.im.ChannelRegistry;
import com.ruoyi.web.service.ISysChatMessageService;
import com.ruoyi.web.service.ISysFriendRelationService;
import com.ruoyi.web.service.TokenService;
import com.ruoyi.web.utils.SnowflakeIdWorker;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.List;
import java.util.HashMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/im")
public class ImController {

    private static final String OFFLINE_QUEUE_PREFIX = "im:offline:";
    private static final String UNREAD_PREFIX = "im:unread:";

    @Resource
    private TokenService tokenService;
    @Resource
    private ISysFriendRelationService friendRelationService;
    @Resource
    private ISysChatMessageService messageService;
    @Resource
    private SnowflakeIdWorker idWorker;
    @Resource
    private RedisTemplate<Object, Object> redisTemplate;

    @PostMapping("/send")
    public AjaxResult send(@RequestBody ChatMessageBody messageBody, HttpServletRequest request) {
        LoginUser loginUser = tokenService.getLoginUser(request);
        messageBody.setFromUserId(loginUser.getUserId());

        // 可选：校验是否好友
        if (!friendRelationService.isFriend(messageBody.getFromUserId(), messageBody.getToUserId())) {
            return AjaxResult.error("双方不是好友，无法发送消息");
        }
        String messageId = idWorker.nextStringId();
        SysChatMessage entity = new SysChatMessage();
        entity.setId(messageId);
        entity.setFromUserId(messageBody.getFromUserId());
        entity.setToUserId(messageBody.getToUserId());
        entity.setContent(messageBody.getContent());
        entity.setMsgType(messageBody.getMsgType());
        entity.setStatus(ChatMessageStatus.NOT_DELIVERED.getValue());
        entity.setCreateTime(LocalDateTime.now());
        messageService.save(entity);

        // 优先尝试实时推送
        Channel ch = ChannelRegistry.get(messageBody.getToUserId());
        if (ch != null && ch.isActive()) {
            ch.writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(entity)));
            messageService.markAsDelivered(messageId);
            return AjaxResult.success("已实时送达");
        }
        // 离线入队并累加未读
        redisTemplate.opsForList().rightPush(OFFLINE_QUEUE_PREFIX + messageBody.getToUserId(), messageId);
        redisTemplate.opsForHash().increment(UNREAD_PREFIX + messageBody.getToUserId(), messageBody.getFromUserId(), 1);
        return AjaxResult.success("对方不在线，已进入离线队列");
    }

    /**
     * 用户上线后拉取未读消息与各会话未读数
     */
    @GetMapping("/unread")
    public AjaxResult getUnread(HttpServletRequest request) {
        LoginUser loginUser = tokenService.getLoginUser(request);
        String userId = loginUser.getUserId();
        // 按会话维度的未读计数（Hash: key=im:unread:{toUid}, field=fromUid, val=count）
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(UNREAD_PREFIX + userId);
        Map<String, Long> unreadBySender = new HashMap<>();
        for (Map.Entry<Object, Object> e : entries.entrySet()) {
            String k = String.valueOf(e.getKey());
            long v;
            try {
                v = e.getValue() == null ? 0L : Long.parseLong(String.valueOf(e.getValue()));
            } catch (NumberFormatException ex) {
                v = 0L;
            }
            unreadBySender.put(k, v);
        }
        // 从数据库查询未投递的消息（status=0）
        List<SysChatMessage> undeliveredMessages = messageService.listOfflineMessages(userId);

        Map<String, Object> data = new HashMap<>();
        data.put("unreadBySender", unreadBySender);
        data.put("undeliveredMessages", undeliveredMessages);
        return AjaxResult.success(data);
    }
}
