package com.ruoyi.web.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.web.domain.entity.SysChatMessage;
import com.ruoyi.web.enums.ChatMessageStatus;
import com.ruoyi.web.mapper.SysChatMessageMapper;
import com.ruoyi.web.service.ISysChatMessageService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SysChatMessageServiceImpl extends ServiceImpl<SysChatMessageMapper, SysChatMessage> implements ISysChatMessageService {
    @Override
    public void markAsDelivered(String messageId) {
        SysChatMessage msg = new SysChatMessage();
        msg.setId(messageId);
        msg.setStatus(ChatMessageStatus.DELIVERED.getValue());
        this.updateById(msg);
    }

    @Override
    public void markAsRead(String messageId) {
        SysChatMessage msg = new SysChatMessage();
        msg.setId(messageId);
        msg.setStatus(ChatMessageStatus.READ.getValue());
        this.updateById(msg);
    }

    @Override
    public List<SysChatMessage> listOfflineMessages(String toUserId) {
        LambdaQueryWrapper<SysChatMessage> qw = new LambdaQueryWrapper<>();
        qw.eq(SysChatMessage::getToUserId, toUserId)
          .eq(SysChatMessage::getStatus, ChatMessageStatus.NOT_DELIVERED.getValue())
          .orderByAsc(SysChatMessage::getCreateTime);
        return this.list(qw);
    }
}
