package com.ruoyi.web.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ChatMessageStatus {

    NOT_DELIVERED("0"),
    DELIVERED("1"),
    READ("2");

    private final String value;
}
