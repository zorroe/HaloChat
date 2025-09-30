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
 * <p>
 * 好友关系表
 * </p>
 *
 * @author zorroe
 * @since 2025-09-30
 */
@Getter
@Setter
@ToString
@TableName("sys_friend_relation")
public class SysFriendRelation implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 关系记录ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private String id;

    /**
     * 当前用户ID（关联user.id）
     */
    private String userId;

    /**
     * 好友用户ID（关联user.id）
     */
    private String friendUserId;

    /**
     * 当前用户对好友的备注（如“大学同学”）
     */
    private String remark;

    /**
     * 是否拉黑：0-正常，1-已拉黑
     */
    private Byte isBlack;

    /**
     * 成为好友的时间
     */
    @JsonFormat(pattern = DatePattern.NORM_DATETIME_PATTERN)
    private LocalDateTime createTime;

    @JsonFormat(pattern = DatePattern.NORM_DATETIME_PATTERN)
    private LocalDateTime updateTime;
}
