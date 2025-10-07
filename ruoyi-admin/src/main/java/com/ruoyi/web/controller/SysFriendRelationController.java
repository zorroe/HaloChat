package com.ruoyi.web.controller;

import com.ruoyi.web.core.page.TableDataInfo;
import com.ruoyi.web.domain.AjaxResult;
import com.ruoyi.web.domain.model.LoginUser;
import com.ruoyi.web.service.ISysFriendRelationService;
import com.ruoyi.web.service.TokenService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 好友关系
 * @author zorroe
 * @since 2025-09-30
 */
@RestController
@RequestMapping("/friend/relation")
public class SysFriendRelationController {

    @Resource
    private ISysFriendRelationService friendRelationService;

    @Resource
    private TokenService tokenService;

    /**
     * 查询好友列表（分页）
     *
     * @param pageNum   页码
     * @param pageSize  页大小
     * @param request   HTTP请求对象
     * @return 好友列表分页数据
     */
    @GetMapping("/list")
    public TableDataInfo getFriendList(@RequestParam(defaultValue = "1") Integer pageNum,
                                       @RequestParam(defaultValue = "10") Integer pageSize,
                                       HttpServletRequest request) {
        String currentUserId = getCurrentUserId(request);
        return friendRelationService.getFriendList(currentUserId, pageNum, pageSize);
    }

    /**
     * 修改好友备注
     *
     * @param friendUserId  好友用户ID
     * @param remark        新备注
     * @param request       HTTP请求对象
     * @return AjaxResult
     */
    @PutMapping("/remark")
    public AjaxResult updateFriendRemark(@RequestParam String friendUserId,
                                         @RequestParam String remark,
                                         HttpServletRequest request) {
        String currentUserId = getCurrentUserId(request);
        return friendRelationService.updateFriendRemark(currentUserId, friendUserId, remark);
    }

    /**
     * 拉黑/取消拉黑好友
     *
     * @param friendUserId  好友用户ID
     * @param isBlack       是否拉黑（1-拉黑，0-取消拉黑）
     * @param request       HTTP请求对象
     * @return AjaxResult
     */
    @PutMapping("/black")
    public AjaxResult updateFriendBlackStatus(@RequestParam String friendUserId,
                                              @RequestParam String isBlack,
                                              HttpServletRequest request) {
        String currentUserId = getCurrentUserId(request);
        return friendRelationService.updateFriendBlackStatus(currentUserId, friendUserId, isBlack);
    }

    /**
     * 删除好友
     *
     * @param friendUserId  好友用户ID
     * @param request       HTTP请求对象
     * @return AjaxResult
     */
    @DeleteMapping("/remove")
    public AjaxResult deleteFriend(@RequestParam String friendUserId,
                                   HttpServletRequest request) {
        String currentUserId = getCurrentUserId(request);
        return friendRelationService.deleteFriend(currentUserId, friendUserId);
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
