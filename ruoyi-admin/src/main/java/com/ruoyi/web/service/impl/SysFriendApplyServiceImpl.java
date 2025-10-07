package com.ruoyi.web.service.impl;

import cn.hutool.core.date.LocalDateTimeUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.web.domain.AjaxResult;
import com.ruoyi.web.domain.entity.SysFriendApply;
import com.ruoyi.web.domain.entity.SysFriendRelation;
import com.ruoyi.web.domain.entity.SysUser;
import com.ruoyi.web.enums.FriendApplyStatus;
import com.ruoyi.web.enums.FriendErrorCode;
import com.ruoyi.web.enums.FriendIsBlackCode;
import com.ruoyi.web.mapper.SysFriendApplyMapper;
import com.ruoyi.web.mapper.SysFriendRelationMapper;
import com.ruoyi.web.mapper.SysUserMapper;
import com.ruoyi.web.service.ISysFriendApplyService;
import com.ruoyi.web.service.ISysFriendRelationService;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 好友申请表 服务实现类
 * </p>
 *
 * @author zorroe
 * @since 2025-09-30
 */
@Service
public class SysFriendApplyServiceImpl extends ServiceImpl<SysFriendApplyMapper, SysFriendApply> implements ISysFriendApplyService {

    @Resource
    private SysUserMapper userMapper;

    @Resource
    private SysFriendRelationMapper friendRelationMapper;

    @Resource
    private ISysFriendRelationService friendRelationService;

    @Resource
    private RedissonClient redissonClient;

    /**
     * 发送好友申请
     *
     * @param fromUserId 申请人ID
     * @param toUserName 被申请人用户名
     * @param applyMsg 申请留言
     * @return AjaxResult
     */
    @Override
    @Transactional
    public AjaxResult sendFriendApply(String fromUserId, String toUserName, String applyMsg) {
        // 使用Redisson分布式锁防止重复申请
        String lockKey = "friend_apply_lock:" + fromUserId + ":" + toUserName;
        RLock lock = redissonClient.getLock(lockKey);

        SysUser fromUser = userMapper.selectById(fromUserId);
        SysUser toUser = userMapper.selectOne(new LambdaQueryWrapper<SysUser>().eq(SysUser::getUserName, toUserName));

        if (Objects.isNull(fromUser) || Objects.isNull(toUser)) {
            return AjaxResult.error(FriendErrorCode.USER_NOT_FOUND.getCode(),
                    FriendErrorCode.USER_NOT_FOUND.getMessage());
        }

        if (fromUser.getUserId().equals(toUser.getUserId())) {
            return AjaxResult.error(FriendErrorCode.FRIEND_REQUEST_CANNOT_SEND_TO_SELF.getCode(),
                    FriendErrorCode.FRIEND_REQUEST_CANNOT_SEND_TO_SELF.getMessage());
        }

        try {
            // 尝试获取分布式锁，设置锁超时时间为5秒，获取锁等待时间为2秒
            if (lock.tryLock(2, 5, TimeUnit.SECONDS)) {
                // 检查申请人和被申请人是否为同一个人


                // 检查是否已经是好友
                if (friendRelationService.isFriend(fromUser.getUserId(), toUser.getUserId())) {
                    return AjaxResult.error(FriendErrorCode.FRIEND_RELATION_ALREADY_EXISTS.getCode(),
                            FriendErrorCode.FRIEND_RELATION_ALREADY_EXISTS.getMessage());
                }

                // 检查是否已存在好友申请
                if (existsFriendApply(fromUser.getUserId(), toUser.getUserId())) {
                    return AjaxResult.error(FriendErrorCode.FRIEND_REQUEST_ALREADY_EXISTS.getCode(),
                            FriendErrorCode.FRIEND_REQUEST_ALREADY_EXISTS.getMessage());
                }

                // 检查是否被拉黑（申请人被被申请人拉黑）
                LambdaQueryWrapper<SysFriendRelation> blockWrapper = new LambdaQueryWrapper<>();
                blockWrapper.eq(SysFriendRelation::getUserId, toUser.getUserId())
                        .eq(SysFriendRelation::getFriendUserId, fromUserId)
                        .eq(SysFriendRelation::getIsBlack, FriendIsBlackCode.IS_BLACK.getCode());
                if (friendRelationMapper.selectCount(blockWrapper) > 0) {
                    return AjaxResult.error(FriendErrorCode.FRIEND_REQUEST_SELF_BLOCKED.getCode(),
                            FriendErrorCode.FRIEND_REQUEST_SELF_BLOCKED.getMessage());
                }

                // 创建好友申请记录
                SysFriendApply friendApply = new SysFriendApply();
                friendApply.setFromUserId(fromUser.getUserId());
                friendApply.setToUserId(toUser.getUserId());
                friendApply.setApplyMsg(applyMsg);
                friendApply.setStatus(FriendApplyStatus.PENDING.getCode()); // 待处理
                friendApply.setCreateTime(LocalDateTimeUtil.now());

                boolean result = this.save(friendApply);
                if (result) {
                    return AjaxResult.success(FriendErrorCode.FRIEND_REQUEST_SUCCESS.getMessage());
                } else {
                    return AjaxResult.error("发送好友申请失败");
                }
            } else {
                // 获取锁失败
                return AjaxResult.error("正在处理中，请稍后再试");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return AjaxResult.error("操作被中断");
        } finally {
            // 释放分布式锁
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    /**
     * 处理好友申请
     *
     * @param applyId 申请ID
     * @param status 处理状态（1-同意，2-拒绝）
     * @param currentUserId 当前用户ID（处理人）
     * @return AjaxResult
     */
    @Override
    @Transactional
    public AjaxResult handleFriendApply(String applyId, String status, String currentUserId) {
        // 验证参数
        if (!status.equals(FriendApplyStatus.AGREE.getCode()) && !status.equals(FriendApplyStatus.REFUSE.getCode())) {
            return AjaxResult.error("处理状态不正确，1-同意，2-拒绝");
        }

        // 查询好友申请记录
        SysFriendApply friendApply = this.getById(applyId);
        if (friendApply == null) {
            return AjaxResult.error(FriendErrorCode.FRIEND_REQUEST_NOT_FOUND.getCode(),
                    FriendErrorCode.FRIEND_REQUEST_NOT_FOUND.getMessage());
        }

        // 检查申请是否已经处理过
        if (!friendApply.getStatus().equals(FriendApplyStatus.PENDING.getCode())) {
            return AjaxResult.error(FriendErrorCode.FRIEND_REQUEST_ALREADY_PROCESSED.getCode(),
                    FriendErrorCode.FRIEND_REQUEST_ALREADY_PROCESSED.getMessage());
        }

        // 检查当前用户是否有权限处理此申请（必须是被申请人）
        if (!friendApply.getToUserId().equals(currentUserId)) {
            return AjaxResult.error("没有权限处理此好友申请");
        }

        // 使用Redisson分布式锁防止并发处理同一申请
        String lockKey = "friend_apply_handle_lock:" + applyId;
        RLock lock = redissonClient.getLock(lockKey);
        
        try {
            // 尝试获取分布式锁
            if (lock.tryLock(2, 5, TimeUnit.SECONDS)) {
                // 更新申请状态
                LambdaUpdateWrapper<SysFriendApply> updateWrapper = new LambdaUpdateWrapper<>();
                updateWrapper.eq(SysFriendApply::getId, applyId)
                        .eq(SysFriendApply::getStatus, FriendApplyStatus.PENDING.getCode()) // 确保之前状态为待处理
                        .set(SysFriendApply::getStatus, status)
                        .set(SysFriendApply::getHandleTime, LocalDateTimeUtil.now());
                boolean updateResult = this.update(updateWrapper);

                if (!updateResult) {
                    return AjaxResult.error("处理好友申请失败");
                }

                // 如果是同意申请，则创建双向好友关系
                if (status.equals(FriendApplyStatus.AGREE.getCode())) {
                    // 检查是否已经是好友
                    if (!friendRelationService.isFriend(friendApply.getFromUserId(), friendApply.getToUserId())) {
                        // 创建好友关系（A->B）
                        SysUser toUser = userMapper.selectById(friendApply.getToUserId());
                        SysFriendRelation relation1 = new SysFriendRelation();
                        relation1.setUserId(friendApply.getFromUserId());
                        relation1.setFriendUserId(friendApply.getToUserId());
                        relation1.setRemark(toUser.getNickName()); // 初始备注为空
                        relation1.setIsBlack(FriendIsBlackCode.NOT_BLACK.getCode()); // 正常状态
                        relation1.setCreateTime(LocalDateTimeUtil.now());
                        relation1.setUpdateTime(LocalDateTimeUtil.now());
                        friendRelationMapper.insert(relation1);

                        // 创建好友关系（B->A）
                        SysUser fromUser = userMapper.selectById(friendApply.getFromUserId());
                        SysFriendRelation relation2 = new SysFriendRelation();
                        relation2.setUserId(friendApply.getToUserId());
                        relation2.setFriendUserId(friendApply.getFromUserId());
                        relation2.setRemark(fromUser.getNickName()); // 初始备注为空
                        relation2.setIsBlack(FriendIsBlackCode.NOT_BLACK.getCode()); // 正常状态
                        relation2.setCreateTime(LocalDateTimeUtil.now());
                        relation2.setUpdateTime(LocalDateTimeUtil.now());
                        friendRelationMapper.insert(relation2);
                    }
                }

                String message = status.equals(FriendApplyStatus.AGREE.getCode()) ? "已同意好友申请" : "已拒绝好友申请";
                return AjaxResult.success(message);
            } else {
                // 获取锁失败
                return AjaxResult.error("正在处理中，请稍后再试");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return AjaxResult.error("操作被中断");
        } finally {
            // 释放分布式锁
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    /**
     * 查询好友申请列表
     *
     * @param userId 用户ID
     * @param status 申请状态
     * @return 好友申请列表
     */
    @Override
    public List<SysFriendApply> getFriendApplies(String userId, Byte status) {
        LambdaQueryWrapper<SysFriendApply> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysFriendApply::getToUserId, userId);
        if (status != null) {
            queryWrapper.eq(SysFriendApply::getStatus, status);
        }
        queryWrapper.orderByDesc(SysFriendApply::getCreateTime);
        return this.list(queryWrapper);
    }

    /**
     * 检查是否已存在好友申请
     *
     * @param fromUserId 申请人ID
     * @param toUserId 被申请人ID
     * @return 是否已存在申请
     */
    @Override
    public boolean existsFriendApply(String fromUserId, String toUserId) {
        LambdaQueryWrapper<SysFriendApply> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysFriendApply::getFromUserId, fromUserId)
                .eq(SysFriendApply::getToUserId, toUserId)
                .in(SysFriendApply::getStatus, FriendApplyStatus.PENDING.getCode()); // 只检查未处理的申请
        return this.count(queryWrapper) > 0;
    }
}
