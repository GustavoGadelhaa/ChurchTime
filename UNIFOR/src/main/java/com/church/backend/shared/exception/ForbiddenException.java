package com.church.backend.shared.exception;

import org.springframework.http.HttpStatus;

public class ForbiddenException extends ApiException {

	public ForbiddenException(String message) {
		super(HttpStatus.FORBIDDEN, message);
	}
}
