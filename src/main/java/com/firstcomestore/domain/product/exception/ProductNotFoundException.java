package com.firstcomestore.domain.product.exception;

import com.firstcomestore.common.exception.ApplicationException;
import com.firstcomestore.common.exception.ErrorCode;

public class ProductNotFoundException extends ApplicationException {

    private static final ErrorCode ERROR_CODE = ErrorCode.PRODUCT_NOT_FOUND;

    public ProductNotFoundException() {
        super(ERROR_CODE);
    }
}
