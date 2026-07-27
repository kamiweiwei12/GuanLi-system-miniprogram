package com.inspection.common;

/**
 * 当前请求用户上下文（ThreadLocal）
 * 仅在请求生命周期内有效，afterCompletion 时 clear
 */
public class UserContext {

    private static final ThreadLocal<UserInfo> USER_HOLDER = new ThreadLocal<>();

    public static void setUser(Long userId, Long roleId, String roleCode, Long departmentId) {
        USER_HOLDER.set(new UserInfo(userId, roleId, roleCode, departmentId));
    }

    public static Long getUserId() {
        UserInfo info = USER_HOLDER.get();
        return info != null ? info.userId : null;
    }

    public static Long getRoleId() {
        UserInfo info = USER_HOLDER.get();
        return info != null ? info.roleId : null;
    }

    public static String getRoleCode() {
        UserInfo info = USER_HOLDER.get();
        return info != null ? info.roleCode : null;
    }

    public static Long getDepartmentId() {
        UserInfo info = USER_HOLDER.get();
        return info != null ? info.departmentId : null;
    }

    public static boolean isAdmin() {
        return "ADMIN".equals(getRoleCode());
    }

    public static boolean isDeptAdmin() {
        return "DEPT_ADMIN".equals(getRoleCode());
    }

    public static void clear() {
        USER_HOLDER.remove();
    }

    /**
     * 用户简要信息
     */
    public static class UserInfo {
        private final Long userId;
        private final Long roleId;
        private final String roleCode;
        private final Long departmentId;

        UserInfo(Long userId, Long roleId, String roleCode, Long departmentId) {
            this.userId = userId;
            this.roleId = roleId;
            this.roleCode = roleCode;
            this.departmentId = departmentId;
        }
    }
}