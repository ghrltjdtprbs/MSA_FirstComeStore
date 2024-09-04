package com.firstcomestore.domain.order.exception;

import com.firstcomestore.common.exception.ApplicationException;
import com.firstcomestore.common.exception.ErrorCode;

public class LockAcquisitionFailureException extends ApplicationException {

    private static final ErrorCode ERROR_CODE = ErrorCode.LOCK_ACQUISITION_FAIL;

    public LockAcquisitionFailureException() {
        super(ERROR_CODE);
    }
}
