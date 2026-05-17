package com.skill.platform.common.exception;

import lombok.Getter;

/**
 * Centralized error code enum for the SKILL Management Platform.
 * <p>
 * Each entry carries a machine-readable code, a default human-readable
 * message, and the HTTP status that should be returned to the client.
 */
@Getter
public enum ErrorCode {

    // ---- AUTH ----
    AUTH001("AUTH001", "Authentication token is missing or invalid", 401),
    AUTH002("AUTH002", "Authentication token has expired", 401),
    AUTH003("AUTH003", "Insufficient permissions for the requested operation", 403),
    AUTH004("AUTH004", "Account is disabled or locked", 403),
    AUTH005("AUTH005", "Invalid username or password", 401),

    // ---- UPLOAD ----
    UPLOAD001("UPLOAD001", "File upload failed", 500),
    UPLOAD002("UPLOAD002", "Uploaded file exceeds the maximum allowed size", 413),
    UPLOAD003("UPLOAD003", "Unsupported file type", 415),
    UPLOAD004("UPLOAD004", "Upload directory does not exist or is not writable", 500),

    // ---- DOWNLOAD ----
    DOWNLOAD001("DOWNLOAD001", "File not found for download", 404),
    DOWNLOAD002("DOWNLOAD002", "File download failed", 500),

    // ---- REVIEW ----
    REVIEW001("REVIEW001", "Review item not found", 404),
    REVIEW002("REVIEW002", "Review already completed", 409),
    REVIEW003("REVIEW003", "Invalid review status transition", 400),

    // ---- RATING ----
    RATING001("RATING001", "Rating value must be between 1 and 5", 400),
    RATING002("RATING002", "Duplicate rating is not allowed", 409),

    // ---- FAVORITE ----
    FAVORITE001("FAVORITE001", "Skill already in favorites", 409),
    FAVORITE002("FAVORITE002", "Favorite entry not found", 404),

    // ---- SHARE ----
    SHARE001("SHARE001", "Share link has expired", 410),
    SHARE002("SHARE002", "Share link not found", 404),

    // ---- SEARCH ----
    SEARCH001("SEARCH001", "Search keyword must not be blank", 400),
    SEARCH002("SEARCH002", "Page number must be >= 1", 400),
    SEARCH003("SEARCH003", "Page size must be >= 1", 400),

    // ---- GRAPH ----
    GRAPH001("GRAPH001", "Graph data generation failed", 500),
    GRAPH002("GRAPH002", "Invalid graph query parameters", 400),

    // ---- LOG ----
    LOG001("LOG001", "Log query failed", 500),
    LOG002("LOG002", "Invalid log filter parameters", 400),

    // ---- CONFIG ----
    CONFIG001("CONFIG001", "Configuration item not found", 404),
    CONFIG002("CONFIG002", "Invalid configuration value", 400),
    CONFIG003("CONFIG003", "Configuration is read-only", 403),

    // ---- HELPDOC ----
    HELPDOC001("HELPDOC001", "Help document not found", 404),
    HELPDOC002("HELPDOC002", "Invalid help document parameters", 400),

    // ---- SUITE ----
    SUITE001("SUITE001", "Test suite not found", 404),
    SUITE002("SUITE002", "Test suite execution failed", 500),
    SUITE003("SUITE003", "Test suite is already running", 409),

    // ---- DEPLOY ----
    DEPLOY001("DEPLOY001", "Deployment failed", 500),
    DEPLOY002("DEPLOY002", "Deployment target environment not found", 404),
    DEPLOY003("DEPLOY003", "Deployment is already in progress", 409),

    // ---- VERSION ----
    VERSION001("VERSION001", "Version not found", 404),
    VERSION002("VERSION002", "Invalid version format", 400),
    VERSION003("VERSION003", "Version conflict detected", 409),

    // ---- VALIDATION ----
    VALIDATION001("VALIDATION001", "Request parameter validation failed", 400),
    VALIDATION002("VALIDATION002", "Request body is missing or empty", 400),
    VALIDATION003("VALIDATION003", "Field value exceeds maximum length", 400),

    // ---- NOTIFY ----
    NOTIFY001("NOTIFY001", "Notification not found", 404),

    // ---- SYSTEM ----
    SYSTEM001("SYSTEM001", "Requested resource not found", 404),
    SYSTEM002("SYSTEM002", "Internal server error", 500),
    SYSTEM003("SYSTEM003", "Service temporarily unavailable", 503),
    SYSTEM004("SYSTEM004", "Request rate limit exceeded", 429);

    private final String code;
    private final String message;
    private final int httpStatus;

    ErrorCode(String code, String message, int httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }
}
