package com.ruoyi.web.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ChatMessageType {
    TEXT("text"),
    IMAGE("image"),
    FILE("file"),
    HEARTBEAT("heartbeat");

    private final String value;
}
