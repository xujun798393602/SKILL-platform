package com.skill.platform.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Generic API response wrapper.
 *
 * @param <T> the type of the response data
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private String code;
    private String message;
    private T data;

    /**
     * Create a successful response with data only.
     *
     * @param data the response data
     * @param <T>  the data type
     * @return a successful ApiResponse
     */
    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .code("SUCCESS")
                .message("OK")
                .data(data)
                .build();
    }

    /**
     * Create a successful response with a custom message and data.
     *
     * @param message the success message
     * @param data    the response data
     * @param <T>     the data type
     * @return a successful ApiResponse
     */
    public static <T> ApiResponse<T> success(String message, T data) {
        return ApiResponse.<T>builder()
                .code("SUCCESS")
                .message(message)
                .data(data)
                .build();
    }

    /**
     * Create a successful response with a custom message and no data.
     *
     * @param message the success message
     * @param <T>     the data type
     * @return a successful ApiResponse with null data
     */
    public static <T> ApiResponse<T> success(String message) {
        return ApiResponse.<T>builder()
                .code("SUCCESS")
                .message(message)
                .build();
    }

    /**
     * Create an error response with an error code and message.
     *
     * @param code    the error code
     * @param message the error message
     * @param <T>     the data type
     * @return an error ApiResponse
     */
    public static <T> ApiResponse<T> error(String code, String message) {
        return ApiResponse.<T>builder()
                .code(code)
                .message(message)
                .build();
    }

    /**
     * Create an error response with an error code, message, and data.
     *
     * @param code    the error code
     * @param message the error message
     * @param data    additional error data
     * @param <T>     the data type
     * @return an error ApiResponse
     */
    public static <T> ApiResponse<T> error(String code, String message, T data) {
        return ApiResponse.<T>builder()
                .code(code)
                .message(message)
                .data(data)
                .build();
    }
}
