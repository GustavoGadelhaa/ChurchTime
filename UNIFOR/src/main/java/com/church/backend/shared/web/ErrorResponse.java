package com.church.backend.shared.web;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;

@Data
@AllArgsConstructor
public class ErrorResponse {
	private Instant timestamp;
	private int status;
	private String error;
	private String message;

	public static ErrorResponse of(int status, String error, String message) {
		return new ErrorResponse(Instant.now(), status, error, message);
	}
}
