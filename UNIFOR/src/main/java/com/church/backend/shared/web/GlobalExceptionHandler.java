package com.church.backend.shared.web;

import com.church.backend.shared.exception.ApiException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(ApiException.class)
	public ResponseEntity<ErrorResponse> handleApi(ApiException ex) {
		var body = ErrorResponse.of(ex.getStatus().value(), ex.getStatus().name(), ex.getMessage());
		return ResponseEntity.status(ex.getStatus()).body(body);
	}

	@ExceptionHandler(BadCredentialsException.class)
	public ResponseEntity<ErrorResponse> handleBadCredentials(BadCredentialsException ex) {
		var status = HttpStatus.UNAUTHORIZED;
		var body = ErrorResponse.of(status.value(), status.name(), "Credenciais inválidas");
		return ResponseEntity.status(status).body(body);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
		var status = HttpStatus.BAD_REQUEST;
		String msg = ex.getBindingResult().getFieldErrors().stream()
				.map(FieldError::getDefaultMessage)
				.collect(Collectors.joining("; "));
		var body = ErrorResponse.of(status.value(), status.name(), msg);
		return ResponseEntity.status(status).body(body);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> handleGeneric(Exception ex) {
		var status = HttpStatus.INTERNAL_SERVER_ERROR;
		var body = ErrorResponse.of(status.value(), status.name(), "Erro interno");
		return ResponseEntity.status(status).body(body);
	}
}
