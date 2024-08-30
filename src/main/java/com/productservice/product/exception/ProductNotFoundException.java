package com.productservice.product.exception;

import com.productservice.common.exception.ApplicationException;
import com.productservice.common.exception.ErrorCode;

public class ProductNotFoundException extends ApplicationException {

    private static final ErrorCode ERROR_CODE = ErrorCode.PRODUCT_NOT_FOUND;

    public ProductNotFoundException() {
        super(ERROR_CODE);
    }
}
