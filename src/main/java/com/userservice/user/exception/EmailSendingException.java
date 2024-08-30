package com.userservice.user.exception;

import com.userservice.common.exception.ApplicationException;
import com.userservice.common.exception.ErrorCode;

public class EmailSendingException extends ApplicationException {

    private static final ErrorCode ERROR_CODE = ErrorCode.EMAIL_SENDING_FAILURE;

    public EmailSendingException() {

        super(ERROR_CODE);
    }

}
