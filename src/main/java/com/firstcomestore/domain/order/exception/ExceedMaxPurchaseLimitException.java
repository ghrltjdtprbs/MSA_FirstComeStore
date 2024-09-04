package com.firstcomestore.domain.order.exception;

import com.firstcomestore.common.exception.ApplicationException;
import com.firstcomestore.common.exception.ErrorCode;

public class ExceedMaxPurchaseLimitException extends ApplicationException {

    private static final ErrorCode ERROR_CODE = ErrorCode.EXCEED_PURCHASE_LIMIT;

    public ExceedMaxPurchaseLimitException() {
        super(ERROR_CODE);
    }
}
