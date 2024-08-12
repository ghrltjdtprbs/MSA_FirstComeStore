package com.firstcomestore.domain.user.exception;

import com.firstcomestore.common.exception.ApplicationException;
import com.firstcomestore.common.exception.ErrorCode;

public class VerificationCodeExpiredException extends ApplicationException {

    private static final ErrorCode ERROR_CODE = ErrorCode.EMAIL_VERIFICATION_CODE_EXPIRED;

    public VerificationCodeExpiredException() {

        super(ERROR_CODE);
    }
}
