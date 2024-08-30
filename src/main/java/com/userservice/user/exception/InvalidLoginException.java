package com.userservice.user.exception;

import com.userservice.common.exception.ApplicationException;
import com.userservice.common.exception.ErrorCode;

public class InvalidLoginException extends ApplicationException {

    private static final ErrorCode ERROR_CODE = ErrorCode.INVALID_LOGIN;

    public InvalidLoginException() {
        super(ERROR_CODE);
    }
}
