package com.firstcomestore.domain.order.exception;

import com.firstcomestore.common.exception.ApplicationException;
import com.firstcomestore.common.exception.ErrorCode;

public class OrderNotFoundException extends ApplicationException {

    private static final ErrorCode ERROR_CODE = ErrorCode.ORDER_NOT_FOUND;

    public OrderNotFoundException() {
        super(ERROR_CODE);
    }
}
