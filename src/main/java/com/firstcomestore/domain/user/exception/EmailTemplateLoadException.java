package com.firstcomestore.domain.user.exception;

import com.firstcomestore.common.exception.ApplicationException;
import com.firstcomestore.common.exception.ErrorCode;

public class EmailTemplateLoadException extends ApplicationException {

    private static final ErrorCode ERROR_CODE = ErrorCode.EMAIL_TEMPLATE_LOAD_FAILURE;

    public EmailTemplateLoadException() {

        super(ERROR_CODE);
    }
}
