package com.firstcomestore.domain.wishlist.exception;


import com.firstcomestore.common.exception.ApplicationException;
import com.firstcomestore.common.exception.ErrorCode;

public class OptionNotFoundException extends ApplicationException {

    private static final ErrorCode ERROR_CODE = ErrorCode.OPTION_NOT_FOUND;

    public OptionNotFoundException() {

        super(ERROR_CODE);
    }
}
