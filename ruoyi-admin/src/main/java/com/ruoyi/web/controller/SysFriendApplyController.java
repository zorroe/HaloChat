package com.ruoyi.web.controller;

import com.ruoyi.web.domain.AjaxResult;
import com.ruoyi.web.domain.entity.SysFriendApply;
import com.ruoyi.web.domain.model.LoginUser;
import com.ruoyi.web.service.ISysFriendApplyService;
import com.ruoyi.web.service.TokenService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 好友申请
 * @author zorroe
 * @since 2025-09-30
 */
@RestController
@RequestMapping("/friend/apply")
public class SysFriendApplyController {

    @Resource
    private ISysFriendApplyService friendApplyService;

    @Resource
    private TokenService tokenService;

    /**
     * 发送好友申请
     *
     * @param toUserName  被申请人用户名
     * @param applyMsg  申请留言
     * @param request   HTTP请求对象
     * @return AjaxResult
     */
    @PostMapping("/send")
    public AjaxResult sendFriendApply(@RequestParam String toUserName,
                                      @RequestParam(required = false) String applyMsg,
                                      HttpServletRequest request) {
        String currentUserId = getCurrentUserId(request);
        return friendApplyService.sendFriendApply(currentUserId, toUserName, applyMsg);
    }

    /**
     * 处理好友申请（同意或拒绝）
     *
     * @param applyId   申请ID
     * @param status    处理状态（1-同意，2-拒绝）
     * @param request   HTTP请求对象
     * @return AjaxResult
     */
    @PutMapping("/handle")
    public AjaxResult handleFriendApply(@RequestParam String applyId,
                                        @RequestParam Byte status,
                                        HttpServletRequest request) {
        String currentUserId = getCurrentUserId(request);
        return friendApplyService.handleFriendApply(applyId, status, currentUserId);
    }

    /**
     * 查询好友申请列表
     *
     * @param status    申请状态（可选）
     * @param request   HTTP请求对象
     * @return 好友申请列表
     */
    @GetMapping("/list")
    public AjaxResult getFriendApplies(@RequestParam(required = false) Byte status,
                                       HttpServletRequest request) {
        String currentUserId = getCurrentUserId(request);
        List<SysFriendApply> applies = friendApplyService.getFriendApplies(currentUserId, status);
        return AjaxResult.success(applies);
    }

    /**
     * 获取当前用户ID
     * 
     * @param request HTTP请求对象
     * @return 当前用户ID
     */
    private String getCurrentUserId(HttpServletRequest request) {
        LoginUser loginUser = tokenService.getLoginUser(request);
        if (loginUser != null) {
            return loginUser.getUserId();
        }
        return null;
    }
}
