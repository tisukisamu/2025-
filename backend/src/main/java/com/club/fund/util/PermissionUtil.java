package com.club.fund.util;

import com.alibaba.fastjson2.JSON;

import java.util.ArrayList;
import java.util.List;

public class PermissionUtil {

    private PermissionUtil() {
    }

    public static List<String> parsePermissions(String permissionsJson) {
        if (permissionsJson == null || permissionsJson.isEmpty()) {
            return new ArrayList<>();
        }
        try {
            return JSON.parseArray(permissionsJson, String.class);
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public static String toJson(List<String> permissions) {
        if (permissions == null || permissions.isEmpty()) {
            return "[]";
        }
        return JSON.toJSONString(permissions);
    }

    public static boolean hasPermission(List<String> permissions, String permission) {
        return permissions != null && permissions.contains(permission);
    }

    public static boolean hasAnyPermission(List<String> permissions, String... requiredPermissions) {
        if (permissions == null || requiredPermissions == null) {
            return false;
        }
        for (String p : requiredPermissions) {
            if (permissions.contains(p)) {
                return true;
            }
        }
        return false;
    }

    public static boolean hasAllPermissions(List<String> permissions, String... requiredPermissions) {
        if (permissions == null || requiredPermissions == null) {
            return false;
        }
        for (String p : requiredPermissions) {
            if (!permissions.contains(p)) {
                return false;
            }
        }
        return true;
    }
}
