package com.daily.daily.member.exception;

import com.daily.daily.common.exception.core.CustomException;
import com.daily.daily.common.exception.core.ErrorCode;

public class DuplicatedUsernameException extends CustomException {
    public DuplicatedUsernameException() {
        super(ErrorCode.DUPLICATED_USERNAME);
    }
}
