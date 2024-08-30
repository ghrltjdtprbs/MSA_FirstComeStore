package com.userservice.user.exception;

import com.userservice.common.exception.ApplicationException;
import com.userservice.common.exception.ErrorCode;

public class UserNotFoundException extends ApplicationException {

    private static final ErrorCode ERROR_CODE = ErrorCode.USER_NOT_FOUND;

    public UserNotFoundException() {
        super(ERROR_CODE);
    }
}
