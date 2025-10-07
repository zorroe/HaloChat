package com.ruoyi.web.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum FriendIsBlackCode {

    NOT_BLACK("0", "非黑名单"),
    IS_BLACK("1", "黑名单");

    private final String code;

    private final String label;

}
