package com.ruoyi.web.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.web.domain.entity.SysChatMessage;

import java.util.List;

public interface ISysChatMessageService extends IService<SysChatMessage> {
    void markAsDelivered(String messageId);
    void markAsRead(String messageId);
    List<SysChatMessage> listOfflineMessages(String toUserId);
}
