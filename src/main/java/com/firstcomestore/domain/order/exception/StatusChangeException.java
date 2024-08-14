package com.firstcomestore.domain.order.exception;

import com.firstcomestore.common.exception.ApplicationException;
import com.firstcomestore.common.exception.ErrorCode;

public class StatusChangeException extends ApplicationException {

    public StatusChangeException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}
