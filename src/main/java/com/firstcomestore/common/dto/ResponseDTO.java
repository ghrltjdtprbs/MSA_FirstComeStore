package com.firstcomestore.common.dto;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.firstcomestore.common.exception.ErrorCode;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseDTO<T> {

    private final int code;
    private final T data;
    private final String message;

    @Builder
    private ResponseDTO(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static ResponseDTO<Void> ok() {
        return ResponseDTO.<Void>builder()
            .code(HttpStatus.OK.value())
            .data(null)
            .message(null)
            .build();
    }

    public static <T> ResponseDTO<T> okWithData(T data) {
        return ResponseDTO.<T>builder()
            .code(HttpStatus.OK.value())
            .data(data)
            .message(null)
            .build();
    }

    public static <T> ResponseDTO<T> okWithMessageAndData(String message, T data) {
        return ResponseDTO.<T>builder()
            .code(HttpStatus.OK.value())
            .message(message)
            .data(data)
            .build();
    }

    public static ResponseDTO<Void> error(ErrorCode errorCode) {
        return ResponseDTO.<Void>builder()
            .code(errorCode.getHttpStatus().value())
            .message(errorCode.getMessage())
            .data(null)
            .build();
    }

    public static ResponseDTO<Void> errorWithMessage(HttpStatus httpStatus, String errorMessage) {
        return ResponseDTO.<Void>builder()
            .code(httpStatus.value())
            .message(errorMessage)
            .data(null)
            .build();
    }
}
