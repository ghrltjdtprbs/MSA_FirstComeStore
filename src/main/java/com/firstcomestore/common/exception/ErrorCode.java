package com.firstcomestore.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    // EMAIL
    EMAIL_SENDING_FAILURE(HttpStatus.INTERNAL_SERVER_ERROR, "이메일 전송에 실패했습니다."),
    EMAIL_TEMPLATE_LOAD_FAILURE(HttpStatus.INTERNAL_SERVER_ERROR, "이메일 템플릿 로드에 실패했습니다."),
    EMAIL_VERIFICATION_CODE_EXPIRED(HttpStatus.GONE, "인증 코드의 유효 기간이 만료되었습니다."),

    // USER
    USER_ALREADY_REGISTERED(HttpStatus.BAD_REQUEST, "이미 가입된 회원입니다."),
    INVALID_LOGIN(HttpStatus.BAD_REQUEST, "잘못된 이메일 또는 비밀번호입니다."),
    USER_NOT_FOUND(HttpStatus.BAD_REQUEST, "존재하지 않는 회원입니다."),

    // PRODUCT
    PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND,"존재하지 않는 상품입니다."),


    // ORDER
    INSUFFICIENT_STOCKE(HttpStatus.CONFLICT,"재고가 부족합니다."),
    ORDER_NOT_FOUND(HttpStatus.NOT_FOUND,"존재하지 않는 주문입니다."),
    CANCEL_NOT_ALLOWED(HttpStatus.BAD_REQUEST,"주문 취소는 아직 발송되지 않은 주문에 대해서만 가능합니다."),
    RETURN_NOT_ALLOWED(HttpStatus.BAD_REQUEST,"반품 가능 기간이 아닙니다.(배송 완료 후 1일 이내에 가능)"),
    INSUFFICIENT_STOCK(HttpStatus.BAD_REQUEST,"재고가 부족합니다."),
    OPTION_NOT_FOUND(HttpStatus.NOT_FOUND,"존재하지 않는 옵션입니다."),

    // WISH
    WISH_NOT_FOUND(HttpStatus.NOT_FOUND,"존재하지 않는 wish 입니다."),

    // 5xx
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 에러");

    private final HttpStatus httpStatus;
    private final String message;

    ErrorCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }
}
