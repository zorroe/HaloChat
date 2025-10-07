package com.ruoyi.web.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum FriendApplyStatus {


    PENDING("0", "待处理"),
    AGREE("1", "同意"),
    REFUSE("2", "拒绝");


    private final String code;

    private final String label;
}
