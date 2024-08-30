package com.userservice.user.exception;

import com.userservice.common.exception.ApplicationException;
import com.userservice.common.exception.ErrorCode;

public class EmailTemplateLoadException extends ApplicationException {

    private static final ErrorCode ERROR_CODE = ErrorCode.EMAIL_TEMPLATE_LOAD_FAILURE;

    public EmailTemplateLoadException() {

        super(ERROR_CODE);
    }
}
