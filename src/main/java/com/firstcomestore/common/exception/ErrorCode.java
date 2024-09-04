package com.firstcomestore.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    // USER
    USER_NOT_FOUND(HttpStatus.BAD_REQUEST, "존재하지 않는 회원입니다."),

    // ORDER
    INSUFFICIENT_STOCKE(HttpStatus.CONFLICT, "재고가 부족합니다."),
    ORDER_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 주문입니다."),
    CANCEL_NOT_ALLOWED(HttpStatus.BAD_REQUEST, "주문 취소는 아직 발송되지 않은 주문에 대해서만 가능합니다."),
    RETURN_NOT_ALLOWED(HttpStatus.BAD_REQUEST, "반품 가능 기간이 아닙니다.(배송 완료 후 1일 이내에 가능)"),
    INSUFFICIENT_STOCK(HttpStatus.BAD_REQUEST, "재고가 부족합니다."),
    OPTION_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 옵션입니다."),
    LOCK_ACQUISITION_FAIL(HttpStatus.CONFLICT, "락을 획득할 수 없습니다. 잠시 후 다시 시도해 주세요."),
    EXCEED_PURCHASE_LIMIT(HttpStatus.BAD_REQUEST, "구매 제한을 초과했습니다."),

    // WISH
    WISH_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 wish 입니다."),

    // 5xx
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 에러");

    private final HttpStatus httpStatus;
    private final String message;

    ErrorCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }
}
