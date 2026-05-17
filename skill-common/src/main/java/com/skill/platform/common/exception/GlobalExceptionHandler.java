package com.skill.platform.common.exception;

import com.skill.platform.common.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Global exception handler that translates exceptions into structured
 * {@link ApiResponse} objects.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handle business exceptions thrown from service / controller layers.
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusinessException(
            BusinessException ex, HttpServletRequest request) {
        log.warn("Business exception at [{} {}]: code={}, message={}",
                request.getMethod(), request.getRequestURI(),
                ex.getErrorCode(), ex.getMessage());

        ApiResponse<Void> body = ApiResponse.error(ex.getErrorCode(), ex.getMessage());
        return ResponseEntity.status(ex.getHttpStatus()).body(body);
    }

    /**
     * Handle Bean Validation failures on {@code @Valid} / {@code @Validated} parameters.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<List<Map<String, String>>>> handleValidationException(
            MethodArgumentNotValidException ex, HttpServletRequest request) {
        log.warn("Validation failed at [{} {}]: {}",
                request.getMethod(), request.getRequestURI(), ex.getMessage());

        List<Map<String, String>> fieldErrors = ex.getBindingResult().getFieldErrors().stream()
                .map(this::toFieldErrorMap)
                .collect(Collectors.toList());

        ApiResponse<List<Map<String, String>>> body =
                ApiResponse.error("VALIDATION001", "Request parameter validation failed", fieldErrors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    /**
     * Handle Spring Security access denied exceptions.
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Void>> handleAccessDeniedException(
            AccessDeniedException ex, HttpServletRequest request) {
        log.warn("Access denied at [{} {}]: {}",
                request.getMethod(), request.getRequestURI(), ex.getMessage());

        ApiResponse<Void> body = ApiResponse.error("AUTH003", "Insufficient permissions for the requested operation");
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(body);
    }

    /**
     * Catch-all handler for unexpected exceptions.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(
            Exception ex, HttpServletRequest request) {
        log.error("Unexpected error at [{} {}]", request.getMethod(), request.getRequestURI(), ex);

        ApiResponse<Void> body = ApiResponse.error("SYSTEM002", "Internal server error");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }

    // ---- helpers ----

    private Map<String, String> toFieldErrorMap(FieldError fe) {
        return Map.of(
                "field", fe.getField(),
                "message", fe.getDefaultMessage() != null ? fe.getDefaultMessage() : "Invalid value"
        );
    }
}
