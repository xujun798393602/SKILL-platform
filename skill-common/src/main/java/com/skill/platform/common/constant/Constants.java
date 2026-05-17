package com.skill.platform.common.constant;

/**
 * Application-wide constants for the SKILL Management Platform.
 */
public final class Constants {

    private Constants() {
        // utility class, no instantiation
    }

    // ---- File handling ----

    /** Maximum file upload size in bytes (100 MB). */
    public static final long MAX_FILE_SIZE = 100 * 1024 * 1024L;

    /** Maximum number of files in a single batch upload. */
    public static final int MAX_BATCH_SIZE = 50;

    /** Temporary upload directory. */
    public static final String UPLOAD_TEMP_DIR = "/tmp/skill-platform/uploads";

    // ---- Authentication / Token ----

    /** Access token expiry in seconds (2 hours). */
    public static final long TOKEN_EXPIRY = 7200;

    /** Refresh token expiry in seconds (7 days). */
    public static final long REFRESH_TOKEN_EXPIRY = 604800;

    /** Token issuer claim. */
    public static final String TOKEN_ISSUER = "skill-platform";

    /** Authorization header name. */
    public static final String AUTH_HEADER = "Authorization";

    /** Bearer token prefix. */
    public static final String BEARER_PREFIX = "Bearer ";

    // ---- Pagination ----

    /** Default page number (1-based). */
    public static final int DEFAULT_PAGE = 1;

    /** Default page size. */
    public static final int DEFAULT_PAGE_SIZE = 20;

    /** Maximum allowed page size. */
    public static final int MAX_PAGE_SIZE = 100;

    // ---- Rating ----

    /** Minimum rating value. */
    public static final int RATING_MIN = 1;

    /** Maximum rating value. */
    public static final int RATING_MAX = 5;

    // ---- Cache TTL (seconds) ----

    /** Short-lived cache TTL (5 minutes). */
    public static final int CACHE_TTL_SHORT = 300;

    /** Medium-lived cache TTL (30 minutes). */
    public static final int CACHE_TTL_MEDIUM = 1800;

    /** Long-lived cache TTL (24 hours). */
    public static final int CACHE_TTL_LONG = 86400;

    // ---- Deployment ----

    /** Maximum concurrent deployments per environment. */
    public static final int MAX_CONCURRENT_DEPLOYS = 3;

    /** Deployment timeout in seconds (10 minutes). */
    public static final int DEPLOY_TIMEOUT = 600;

    // ---- Version ----

    /** Semantic version pattern (e.g. 1.2.3). */
    public static final String SEMVER_PATTERN = "^\\d+\\.\\d+\\.\\d+(-[a-zA-Z0-9.]+)?$";

    // ---- Security ----

    /** Minimum password length. */
    public static final int PASSWORD_MIN_LENGTH = 8;

    /** Maximum login attempts before lockout. */
    public static final int MAX_LOGIN_ATTEMPTS = 5;

    /** Account lockout duration in seconds (15 minutes). */
    public static final int LOCKOUT_DURATION = 900;
}
