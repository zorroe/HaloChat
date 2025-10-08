package com.ruoyi.web.domain.entity;

import cn.hutool.core.date.DatePattern;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 聊天消息实体
 */
@Getter
@Setter
@ToString
@TableName("sys_chat_message")
public class SysChatMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 消息ID（雪花ID）
     */
    @TableId(value = "id", type = IdType.INPUT)
    private String id;

    /** 发送者用户ID */
    private String fromUserId;

    /** 接收者用户ID */
    private String toUserId;

    /** 消息内容 */
    private String content;

    /** 消息类型：text/image/file 等 */
    private String msgType;

    /** 投递状态：0-未投递 1-已投递 2-已读 */
    private String status;

    /** 创建时间 */
    @JsonFormat(pattern = DatePattern.NORM_DATETIME_PATTERN)
    private LocalDateTime createTime;

    /** 投递时间（推送到客户端时间） */
    @JsonFormat(pattern = DatePattern.NORM_DATETIME_PATTERN)
    private LocalDateTime deliverTime;

    /** 阅读时间 */
    @JsonFormat(pattern = DatePattern.NORM_DATETIME_PATTERN)
    private LocalDateTime readTime;
}
