package com.club.fund.common;

import lombok.Getter;

@Getter
public enum ErrorCode {

    SUCCESS(200, "操作成功"),
    BAD_REQUEST(400, "请求参数错误"),
    UNAUTHORIZED(401, "未授权或Token过期"),
    FORBIDDEN(403, "权限不足"),
    NOT_FOUND(404, "资源不存在"),
    INTERNAL_ERROR(500, "服务器内部错误"),

    USERNAME_EXISTS(1001, "用户名已存在"),
    PASSWORD_ERROR(1002, "密码错误"),
    USER_NOT_FOUND(1003, "用户不存在"),
    USER_DISABLED(1004, "用户已被禁用"),

    CLUB_NOT_FOUND(2001, "社团不存在"),
    NOT_CLUB_MEMBER(2002, "用户不在社团中"),
    CLUB_CODE_EXISTS(2003, "社团编码已存在"),

    APPLY_NOT_FOUND(3001, "申请不存在"),
    BALANCE_NOT_ENOUGH(3002, "余额不足"),
    NO_APPROVAL_PERMISSION(3003, "无权审批"),
    APPLY_STATUS_ERROR(3004, "申请状态错误"),

    FILE_UPLOAD_ERROR(4001, "文件上传失败"),
    FILE_TYPE_ERROR(4002, "文件类型不支持"),
    FILE_SIZE_ERROR(4003, "文件大小超过限制");

    private final Integer code;
    private final String message;

    ErrorCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
