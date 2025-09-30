package com.ruoyi.web.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.web.core.page.TableDataInfo;
import com.ruoyi.web.domain.AjaxResult;
import com.ruoyi.web.domain.entity.SysFriendRelation;
import com.ruoyi.web.domain.entity.SysUser;
import com.ruoyi.web.enums.FriendErrorCode;
import com.ruoyi.web.mapper.SysFriendRelationMapper;
import com.ruoyi.web.mapper.SysUserMapper;
import com.ruoyi.web.service.ISysFriendRelationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 好友关系表 服务实现类
 * </p>
 *
 * @author zorroe
 * @since 2025-09-30
 */
@Service
public class SysFriendRelationServiceImpl extends ServiceImpl<SysFriendRelationMapper, SysFriendRelation> implements ISysFriendRelationService {

    @Resource
    private SysUserMapper userMapper;

    /**
     * 查询好友列表（分页）
     *
     * @param userId 用户ID
     * @param pageNum 页码
     * @param pageSize 页大小
     * @return 好友列表分页数据
     */
    @Override
    public TableDataInfo getFriendList(String userId, int pageNum, int pageSize) {
        // 创建分页对象
        Page<SysFriendRelation> page = new Page<>(pageNum, pageSize);

        // 构造查询条件
        LambdaQueryWrapper<SysFriendRelation> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysFriendRelation::getUserId, userId)
                .eq(SysFriendRelation::getIsBlack, (byte) 0) // 只查询非拉黑的好友
                .orderByDesc(SysFriendRelation::getUpdateTime); // 按更新时间倒序

        // 执行分页查询
        Page<SysFriendRelation> friendPage = this.page(page, queryWrapper);

        // 查询好友的详细信息并封装
        List<SysFriendRelation> friendRelations = friendPage.getRecords();
        List<SysUser> friendUsers = new ArrayList<>();
        for (SysFriendRelation relation : friendRelations) {
            SysUser friendUser = userMapper.selectById(relation.getFriendUserId());
            if (friendUser != null) {
                // 隐藏敏感信息，如密码
                friendUser.setPassword(null);
                friendUser.setRemark(relation.getRemark());
                friendUsers.add(friendUser);
            }
        }

        TableDataInfo tableDataInfo = new TableDataInfo();
        tableDataInfo.setRows(friendUsers);
        tableDataInfo.setTotal(friendPage.getTotal());
        tableDataInfo.setCode(200);
        tableDataInfo.setMsg(FriendErrorCode.FRIEND_LIST_QUERY_SUCCESS.getMessage());

        return tableDataInfo;
    }

    /**
     * 修改好友备注
     *
     * @param userId 用户ID
     * @param friendUserId 好友用户ID
     * @param remark 新备注
     * @return AjaxResult
     */
    @Override
    @Transactional
    public AjaxResult updateFriendRemark(String userId, String friendUserId, String remark) {
        // 检查好友关系是否存在
        if (!isFriend(userId, friendUserId)) {
            return AjaxResult.error(FriendErrorCode.FRIEND_RELATION_NOT_FOUND.getCode(),
                    FriendErrorCode.FRIEND_RELATION_NOT_FOUND.getMessage());
        }

        // 更新用户对好友的备注
        LambdaUpdateWrapper<SysFriendRelation> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(SysFriendRelation::getUserId, userId)
                .eq(SysFriendRelation::getFriendUserId, friendUserId)
                .set(SysFriendRelation::getRemark, remark);

        boolean result = this.update(updateWrapper);
        if (result) {
            return AjaxResult.success("备注修改成功");
        } else {
            return AjaxResult.error("备注修改失败");
        }
    }

    /**
     * 拉黑/取消拉黑好友
     *
     * @param userId 用户ID
     * @param friendUserId 好友用户ID
     * @param isBlack 是否拉黑（1-拉黑，0-取消拉黑）
     * @return AjaxResult
     */
    @Override
    @Transactional
    public AjaxResult updateFriendBlackStatus(String userId, String friendUserId, Byte isBlack) {
        // 验证参数
        if (isBlack != 0 && isBlack != 1) {
            return AjaxResult.error("拉黑状态参数错误，0-取消拉黑，1-拉黑");
        }

        // 更新拉黑状态
        LambdaUpdateWrapper<SysFriendRelation> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(SysFriendRelation::getUserId, userId)
                .eq(SysFriendRelation::getFriendUserId, friendUserId)
                .set(SysFriendRelation::getIsBlack, isBlack);

        boolean result = this.update(updateWrapper);
        if (result) {
            String message = isBlack == 1 ? "已拉黑该好友" : "已取消拉黑该好友";
            return AjaxResult.success(message);
        } else {
            return AjaxResult.error("更新拉黑状态失败");
        }
    }

    /**
     * 检查用户是否为好友
     *
     * @param userId 用户ID
     * @param friendUserId 好友用户ID
     * @return boolean
     */
    @Override
    public boolean isFriend(String userId, String friendUserId) {
        LambdaQueryWrapper<SysFriendRelation> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysFriendRelation::getUserId, userId)
                .eq(SysFriendRelation::getFriendUserId, friendUserId)
                .eq(SysFriendRelation::getIsBlack, (byte) 0); // 只检查非拉黑状态的好友
        return this.count(queryWrapper) > 0;
    }

    /**
     * 删除好友关系
     *
     * @param userId 用户ID
     * @param friendUserId 好友用户ID
     * @return AjaxResult
     */
    @Override
    @Transactional
    public AjaxResult deleteFriend(String userId, String friendUserId) {
        // 删除好友关系（双向）- 软删除或逻辑删除，这里使用物理删除
        LambdaQueryWrapper<SysFriendRelation> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.and(wrapper -> wrapper.eq(SysFriendRelation::getUserId, userId)
                .eq(SysFriendRelation::getFriendUserId, friendUserId))
                .or(wrapper -> wrapper.eq(SysFriendRelation::getUserId, friendUserId)
                        .eq(SysFriendRelation::getFriendUserId, userId));

        boolean result = this.remove(queryWrapper);
        if (result) {
            return AjaxResult.success("删除好友成功");
        } else {
            return AjaxResult.error("删除好友失败");
        }
    }
}
