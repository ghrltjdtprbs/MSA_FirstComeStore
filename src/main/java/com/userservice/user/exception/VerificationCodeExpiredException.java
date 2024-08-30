package com.userservice.user.exception;

import com.userservice.common.exception.ApplicationException;
import com.userservice.common.exception.ErrorCode;

public class VerificationCodeExpiredException extends ApplicationException {

    private static final ErrorCode ERROR_CODE = ErrorCode.EMAIL_VERIFICATION_CODE_EXPIRED;

    public VerificationCodeExpiredException() {

        super(ERROR_CODE);
    }
}
