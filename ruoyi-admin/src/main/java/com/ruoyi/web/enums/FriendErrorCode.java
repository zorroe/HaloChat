package com.ruoyi.web.enums;

/**
 * 好友管理错误码
 * 
 * @author zorroe
 */
public enum FriendErrorCode {
    FRIEND_REQUEST_SUCCESS(200, "好友请求成功"),
    FRIEND_REQUEST_ALREADY_EXISTS(40001, "好友申请已存在"),
    FRIEND_REQUEST_NOT_FOUND(40002, "好友申请不存在"),
    FRIEND_REQUEST_ALREADY_PROCESSED(40003, "好友申请已处理"),
    FRIEND_REQUEST_CANNOT_SEND_TO_SELF(40004, "不能向自己发送好友申请"),
    FRIEND_RELATION_NOT_FOUND(40005, "好友关系不存在"),
    USER_NOT_FOUND(40006, "用户不存在"),
    FRIEND_REQUEST_SELF_BLOCKED(40007, "对方已将您拉黑"),
    FRIEND_RELATION_ALREADY_EXISTS(40008, "好友关系已存在"),
    FRIEND_LIST_QUERY_SUCCESS(200, "好友列表查询成功");

    private final int code;
    private final String message;

    FriendErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}