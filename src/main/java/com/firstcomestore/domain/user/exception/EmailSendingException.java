package com.firstcomestore.domain.user.exception;

import com.firstcomestore.common.exception.ApplicationException;
import com.firstcomestore.common.exception.ErrorCode;

public class EmailSendingException extends ApplicationException {

    private static final ErrorCode ERROR_CODE = ErrorCode.EMAIL_SENDING_FAILURE;

    public EmailSendingException() {

        super(ERROR_CODE);
    }

}
