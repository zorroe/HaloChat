package com.ruoyi.web.controller;

import cn.hutool.core.text.CharSequenceUtil;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.model.RegisterBody;
import com.ruoyi.web.service.SysRegisterService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * RegisterController
 * 
 * @author zorroe
 */
@RestController
public class SysRegisterController extends BaseController
{
    @Resource
    private SysRegisterService registerService;

    /**
     * 注册
     */
    @PostMapping("/register")
    public AjaxResult register(@RequestBody RegisterBody user)
    {
        String msg = registerService.register(user);
        return CharSequenceUtil.isEmpty(msg) ? success() : error(msg);
    }
}
