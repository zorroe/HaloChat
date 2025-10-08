package com.ruoyi.web.domain.model;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class ChatMessageBody {

    private String fromUserId;

    @NotBlank(message = "接收者用户ID不能为空")
    private String toUserId;

    @NotBlank(message = "消息内容不能为空")
    private String content;

    @NotBlank(message = "消息类型不能为空")
    private String msgType;

}
