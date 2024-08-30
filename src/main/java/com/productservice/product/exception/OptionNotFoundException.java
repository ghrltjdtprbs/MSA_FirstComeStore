package com.productservice.product.exception;


import com.productservice.common.exception.ApplicationException;
import com.productservice.common.exception.ErrorCode;

public class OptionNotFoundException extends ApplicationException {

    private static final ErrorCode ERROR_CODE = ErrorCode.OPTION_NOT_FOUND;

    public OptionNotFoundException() {

        super(ERROR_CODE);
    }
}
