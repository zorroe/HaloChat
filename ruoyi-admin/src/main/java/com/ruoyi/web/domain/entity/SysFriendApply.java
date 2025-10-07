package com.ruoyi.web.domain.entity;

import cn.hutool.core.date.DatePattern;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 好友申请表
 * </p>
 *
 * @author zorroe
 * @since 2025-09-30
 */
@Data
@TableName("sys_friend_apply")
public class SysFriendApply implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 申请记录ID
     */
    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    private String id;

    /**
     * 申请人ID（关联user.id）
     */
    private String fromUserId;

    /**
     * 被申请人ID（关联user.id）
     */
    private String toUserId;

    /**
     * 申请留言（如“我是XXX”）
     */
    private String applyMsg;

    /**
     * 申请状态：0-待处理，1-已同意，2-已拒绝
     */
    private String status;

    /**
     * 处理时间（同意/拒绝时更新）
     */
    @JsonFormat(pattern = DatePattern.NORM_DATETIME_PATTERN)
    private LocalDateTime handleTime;

    /**
     * 申请时间
     */
    @JsonFormat(pattern = DatePattern.NORM_DATETIME_PATTERN)
    private LocalDateTime createTime;
}
