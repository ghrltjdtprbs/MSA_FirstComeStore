package com.firstcomestore.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    // USER
    USER_ALREADY_REGISTERED(HttpStatus.BAD_REQUEST, "이미 가입된 회원입니다."),
    INVALID_LOGIN(HttpStatus.BAD_REQUEST, "잘못된 이메일 또는 비밀번호입니다."),
    USER_NOT_FOUND(HttpStatus.BAD_REQUEST, "존재하지 않는 회원입니다."),

    // 5xx
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 에러");

    private final HttpStatus httpStatus;
    private final String message;

    ErrorCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }
}
