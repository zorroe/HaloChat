package com.ruoyi.web.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.web.domain.AjaxResult;
import com.ruoyi.web.domain.entity.SysFriendApply;
import com.ruoyi.web.domain.entity.SysUser;

import java.util.List;

/**
 * <p>
 * 好友申请表 服务类
 * </p>
 *
 * @author zorroe
 * @since 2025-09-30
 */
public interface ISysFriendApplyService extends IService<SysFriendApply> {
    /**
     * 发送好友申请
     *
     * @param fromUserId 申请人ID
     * @param toUserId 被申请人ID
     * @param applyMsg 申请留言
     * @return AjaxResult
     */
    AjaxResult sendFriendApply(String fromUserId, String toUserId, String applyMsg);

    /**
     * 处理好友申请
     *
     * @param applyId 申请ID
     * @param status 处理状态（1-同意，2-拒绝）
     * @param currentUserId 当前用户ID（处理人）
     * @return AjaxResult
     */
    AjaxResult handleFriendApply(String applyId, Byte status, String currentUserId);

    /**
     * 查询好友申请列表
     *
     * @param userId 用户ID
     * @param status 申请状态
     * @return 好友申请列表
     */
    List<SysFriendApply> getFriendApplies(String userId, Byte status);

    /**
     * 检查是否已存在好友申请
     *
     * @param fromUserId 申请人ID
     * @param toUserId 被申请人ID
     * @return 是否已存在申请
     */
    boolean existsFriendApply(String fromUserId, String toUserId);
}
