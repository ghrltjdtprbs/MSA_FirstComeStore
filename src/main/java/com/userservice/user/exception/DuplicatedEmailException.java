package com.userservice.user.exception;

import com.userservice.common.exception.ApplicationException;
import com.userservice.common.exception.ErrorCode;

public class DuplicatedEmailException extends ApplicationException {

    private static final ErrorCode ERROR_CODE = ErrorCode.USER_ALREADY_REGISTERED;

    public DuplicatedEmailException() {
        super(ERROR_CODE);
    }

}
