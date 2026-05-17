package com.skill.platform.common.exception;

import lombok.Getter;

/**
 * Business exception for the SKILL Management Platform.
 * <p>
 * Thrown when a business rule is violated or a recoverable error occurs
 * during request processing. Carries an error code and HTTP status so that
 * the {@link GlobalExceptionHandler} can translate it into a structured
 * {@link com.skill.platform.common.response.ApiResponse}.
 */
@Getter
public class BusinessException extends RuntimeException {

    private final String errorCode;
    private final int httpStatus;

    /**
     * Construct a BusinessException with an error code, message, and HTTP status.
     *
     * @param errorCode  a machine-readable error code (e.g. "AUTH001")
     * @param message    a human-readable description of the error
     * @param httpStatus the HTTP status code to return to the client
     */
    public BusinessException(String errorCode, String message, int httpStatus) {
        super(message);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
    }

    /**
     * Construct a BusinessException from an {@link ErrorCode} enum entry.
     *
     * @param errorCode the ErrorCode enum entry
     */
    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode.getCode();
        this.httpStatus = errorCode.getHttpStatus();
    }

    // ---- Common error codes (convenience constants) ----

    /** Authentication token is missing or invalid. */
    public static final String AUTH001 = "AUTH001";
    /** Authentication token has expired. */
    public static final String AUTH002 = "AUTH002";
    /** Insufficient permissions for the requested operation. */
    public static final String AUTH003 = "AUTH003";

    /** File upload failed. */
    public static final String UPLOAD001 = "UPLOAD001";
    /** Uploaded file exceeds the maximum allowed size. */
    public static final String UPLOAD002 = "UPLOAD002";
    /** Unsupported file type. */
    public static final String UPLOAD003 = "UPLOAD003";

    /** Resource not found. */
    public static final String SYSTEM001 = "SYSTEM001";
    /** Internal server error. */
    public static final String SYSTEM002 = "SYSTEM002";
    /** Request parameter validation failed. */
    public static final String SYSTEM003 = "SYSTEM003";
}
