package com.firstcomestore.domain.user.exception;

import com.firstcomestore.common.exception.ApplicationException;
import com.firstcomestore.common.exception.ErrorCode;

public class DuplicatedEmailException extends ApplicationException {

    private static final ErrorCode ERROR_CODE = ErrorCode.USER_ALREADY_REGISTERED;

    public DuplicatedEmailException() {
        super(ERROR_CODE);
    }

}
