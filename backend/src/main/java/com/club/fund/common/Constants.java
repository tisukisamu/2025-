package com.club.fund.common;

public class Constants {

    private Constants() {
    }

    public static final String TOKEN_HEADER = "Authorization";
    public static final String TOKEN_PREFIX = "Bearer ";

    public static final String ROLE_MEMBER = "member";
    public static final String ROLE_PRESIDENT = "president";
    public static final String ROLE_TEACHER = "teacher";
    public static final String ROLE_ADMIN = "admin";

    public static final String STATUS_PENDING = "PENDING";
    public static final String STATUS_PRESIDENT_APPROVED = "PRESIDENT_APPROVED";
    public static final String STATUS_TEACHER_APPROVED = "TEACHER_APPROVED";
    public static final String STATUS_COMPLETED = "COMPLETED";
    public static final String STATUS_REJECTED = "REJECTED";
    public static final String STATUS_CANCELLED = "CANCELLED";

    public static final String FLOW_TYPE_INCOME = "INCOME";
    public static final String FLOW_TYPE_EXPENSE = "EXPENSE";
    public static final String FLOW_TYPE_REFUND = "REFUND";
    public static final String FLOW_TYPE_ADJUST = "ADJUST";

    public static final String APPLY_TYPE_ACTIVITY_FUND = "ACTIVITY_FUND";
    public static final String APPLY_TYPE_MATERIAL = "MATERIAL";
    public static final String APPLY_TYPE_REIMBURSEMENT = "REIMBURSEMENT";
    public static final String APPLY_TYPE_OTHER = "OTHER";

    public static final String NOTIFICATION_TYPE_SYSTEM = "SYSTEM";
    public static final String NOTIFICATION_TYPE_APPROVAL = "APPROVAL";
    public static final String NOTIFICATION_TYPE_ACTIVITY = "ACTIVITY";
    public static final String NOTIFICATION_TYPE_WARNING = "WARNING";

    public static final String APPROVAL_ACTION_APPROVE = "APPROVE";
    public static final String APPROVAL_ACTION_REJECT = "REJECT";
    public static final String APPROVAL_ACTION_RETURN = "RETURN";
}
