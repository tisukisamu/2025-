package com.club.fund.util;

import com.club.fund.entity.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtil {

    private SecurityUtil() {
    }

    public static User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof User) {
            return (User) authentication.getPrincipal();
        }
        return null;
    }

    public static Long getCurrentUserId() {
        User user = getCurrentUser();
        return user != null ? user.getId() : null;
    }

    public static String getCurrentUsername() {
        User user = getCurrentUser();
        return user != null ? user.getUsername() : null;
    }

    public static String getCurrentUserRole() {
        User user = getCurrentUser();
        return user != null && user.getRole() != null ? user.getRole().getRoleCode() : null;
    }

    public static boolean hasRole(String roleCode) {
        String currentRole = getCurrentUserRole();
        return currentRole != null && currentRole.equals(roleCode);
    }

    public static boolean isAdmin() {
        return hasRole("admin");
    }

    public static boolean isTeacher() {
        return hasRole("teacher");
    }

    public static boolean isPresident() {
        return hasRole("president");
    }

    public static boolean isMember() {
        return hasRole("member");
    }
}
