package com.firstcomestore.domain.user.exception;

import com.firstcomestore.common.exception.ApplicationException;
import com.firstcomestore.common.exception.ErrorCode;

public class InvalidLoginException extends ApplicationException {

    private static final ErrorCode ERROR_CODE = ErrorCode.INVALID_LOGIN;

    public InvalidLoginException() {
        super(ERROR_CODE);
    }
}
