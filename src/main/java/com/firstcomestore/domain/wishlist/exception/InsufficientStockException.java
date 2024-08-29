package com.firstcomestore.domain.wishlist.exception;

import com.firstcomestore.common.exception.ApplicationException;
import com.firstcomestore.common.exception.ErrorCode;

public class InsufficientStockException extends ApplicationException {

    private static final ErrorCode ERROR_CODE = ErrorCode.INSUFFICIENT_STOCK;

    public InsufficientStockException() {

        super(ERROR_CODE);
    }
}
