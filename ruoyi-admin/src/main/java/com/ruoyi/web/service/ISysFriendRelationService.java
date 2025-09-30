package com.ruoyi.web.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.web.core.page.TableDataInfo;
import com.ruoyi.web.domain.AjaxResult;
import com.ruoyi.web.domain.entity.SysFriendRelation;

/**
 * <p>
 * 好友关系表 服务类
 * </p>
 *
 * @author zorroe
 * @since 2025-09-30
 */
public interface ISysFriendRelationService extends IService<SysFriendRelation> {
    /**
     * 查询好友列表（分页）
     *
     * @param userId 用户ID
     * @param pageNum 页码
     * @param pageSize 页大小
     * @return 好友列表分页数据
     */
    TableDataInfo getFriendList(String userId, int pageNum, int pageSize);

    /**
     * 修改好友备注
     *
     * @param userId 用户ID
     * @param friendUserId 好友用户ID
     * @param remark 新备注
     * @return AjaxResult
     */
    AjaxResult updateFriendRemark(String userId, String friendUserId, String remark);

    /**
     * 拉黑/取消拉黑好友
     *
     * @param userId 用户ID
     * @param friendUserId 好友用户ID
     * @param isBlack 是否拉黑（1-拉黑，0-取消拉黑）
     * @return AjaxResult
     */
    AjaxResult updateFriendBlackStatus(String userId, String friendUserId, Byte isBlack);

    /**
     * 检查用户是否为好友
     *
     * @param userId 用户ID
     * @param friendUserId 好友用户ID
     * @return boolean
     */
    boolean isFriend(String userId, String friendUserId);

    /**
     * 删除好友关系
     *
     * @param userId 用户ID
     * @param friendUserId 好友用户ID
     * @return AjaxResult
     */
    AjaxResult deleteFriend(String userId, String friendUserId);
}
