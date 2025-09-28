package com.ruoyi.web.controller;

import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.web.service.ISysUserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
public class SysUserController {

    @Resource
    private ISysUserService userService;


    @GetMapping("/list")
    public AjaxResult list() {
        return AjaxResult.success(userService.list());
    }
}
