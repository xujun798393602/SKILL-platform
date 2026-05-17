package com.skill.platform.common.util;

import java.util.Collections;
import java.util.List;

/**
 * Thread-local holder for the authenticated user's context.
 * <p>
 * Populated by the authentication filter at the start of a request and
 * cleared at the end. Service-layer code can call the static getters to
 * obtain the current user's identity without passing it through every
 * method signature.
 */
public final class UserContext {

    private UserContext() {
        // utility class, no instantiation
    }

    private static final ThreadLocal<String> USER_ID = new ThreadLocal<>();
    private static final ThreadLocal<String> EMPLOYEE_ID = new ThreadLocal<>();
    private static final ThreadLocal<List<String>> ROLES = new ThreadLocal<>();
    private static final ThreadLocal<String> IP_ADDRESS = new ThreadLocal<>();
    private static final ThreadLocal<String> USER_AGENT = new ThreadLocal<>();

    /**
     * Set the full user context for the current thread.
     *
     * @param userId     the internal user ID (UUID string)
     * @param employeeId the employee identifier (e.g. badge number)
     * @param roles      the roles assigned to the user
     */
    public static void set(String userId, String employeeId, List<String> roles) {
        USER_ID.set(userId);
        EMPLOYEE_ID.set(employeeId);
        ROLES.set(roles != null ? List.copyOf(roles) : Collections.emptyList());
    }

    public static void set(String userId, String employeeId, List<String> roles, String ipAddress, String userAgent) {
        set(userId, employeeId, roles);
        IP_ADDRESS.set(ipAddress);
        USER_AGENT.set(userAgent);
    }

    /**
     * Get the internal user ID.
     *
     * @return the user ID as a UUID string, or {@code null} if not set
     */
    public static String getUserId() {
        return USER_ID.get();
    }

    /**
     * Get the employee identifier.
     *
     * @return the employee ID, or {@code null} if not set
     */
    public static String getEmployeeId() {
        return EMPLOYEE_ID.get();
    }

    /**
     * Get the roles assigned to the current user.
     *
     * @return an unmodifiable list of roles, or an empty list if not set
     */
    public static List<String> getRoles() {
        List<String> roles = ROLES.get();
        return roles != null ? roles : Collections.emptyList();
    }

    /**
     * Check whether the current user has a specific role.
     *
     * @param role the role to check
     * @return {@code true} if the user has the role
     */
    public static boolean hasRole(String role) {
        return getRoles().contains(role);
    }

    public static String getIpAddress() {
        return IP_ADDRESS.get();
    }

    public static String getUserAgent() {
        return USER_AGENT.get();
    }

    /**
     * Clear all context for the current thread.
     * <p>
     * Must be called in a {@code finally} block or via a servlet filter
     * to prevent thread-local leaks in a servlet container.
     */
    public static void clear() {
        USER_ID.remove();
        EMPLOYEE_ID.remove();
        ROLES.remove();
        IP_ADDRESS.remove();
        USER_AGENT.remove();
    }
}
