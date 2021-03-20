package com.black.fixedlength.exception;

import java.io.IOException;

public class FixedLengthFormatException extends IOException {

	public FixedLengthFormatException(String msg){
		super(msg);
	}

	public FixedLengthFormatException(Throwable cause) {
        super(cause);
    }

	public FixedLengthFormatException(String message, Throwable cause) {
        super(message, cause);
    }
}
