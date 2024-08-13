package com.firstcomestore.domain.wishlist.exception;

import com.firstcomestore.common.exception.ApplicationException;
import com.firstcomestore.common.exception.ErrorCode;

public class WishNotFoundException extends ApplicationException {

    private static final ErrorCode ERROR_CODE = ErrorCode.WISH_NOT_FOUND;

    public WishNotFoundException() {

        super(ERROR_CODE);
    }
}
