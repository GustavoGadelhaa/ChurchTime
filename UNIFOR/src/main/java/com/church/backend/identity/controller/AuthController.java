package com.church.backend.identity.controller;

import com.church.backend.identity.dto.AuthDtos.ForgotPasswordRequest;
import com.church.backend.identity.dto.AuthDtos.LoginRequest;
import com.church.backend.identity.dto.AuthDtos.RegisterRequest;
import com.church.backend.identity.dto.AuthDtos.ResetPasswordRequest;
import com.church.backend.identity.dto.AuthDtos.TokenResponse;
import com.church.backend.identity.service.AuthService;
import com.church.backend.identity.service.PasswordResetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

	private final AuthService authService;
	private final PasswordResetService passwordResetService;

	@PostMapping("/login")
	public TokenResponse login(@RequestBody @Valid LoginRequest request) {
		log.info("[AUTH] POST /api/auth/login - Email: {} - Timestamp: {}", 
				request.getEmail(), java.time.LocalDateTime.now());
		TokenResponse response = authService.login(request);
		log.info("[AUTH] POST /api/auth/login - SUCCESS - Email: {} - Timestamp: {}", 
				request.getEmail(), java.time.LocalDateTime.now());
		return response;
	}

	@PostMapping("/register")
	@ResponseStatus(HttpStatus.CREATED)
	public TokenResponse register(@RequestBody @Valid RegisterRequest request) {
		log.info("[AUTH] POST /api/auth/register - Name: {}, Email: {} - Timestamp: {}", 
				request.getName(), request.getEmail(), java.time.LocalDateTime.now());
		TokenResponse response = authService.register(request);
		log.info("[AUTH] POST /api/auth/register - CREATED - Email: {} - Timestamp: {}", 
				request.getEmail(), java.time.LocalDateTime.now());
		return response;
	}

	@PostMapping("/forgot-password")
	public void forgotPassword(@RequestBody @Valid ForgotPasswordRequest request) {
		log.info("[AUTH] POST /api/auth/forgot-password - Email: {} - Timestamp: {}", 
				request.getEmail(), java.time.LocalDateTime.now());
		passwordResetService.requestReset(request);
		log.info("[AUTH] POST /api/auth/forgot-password - SENT - Email: {} - Timestamp: {}", 
				request.getEmail(), java.time.LocalDateTime.now());
	}

	@PostMapping("/reset-password")
	public void resetPassword(@RequestBody @Valid ResetPasswordRequest request) {
		log.info("[AUTH] POST /api/auth/reset-password - Token received - Timestamp: {}", java.time.LocalDateTime.now());
		passwordResetService.resetPassword(request);
		log.info("[AUTH] POST /api/auth/reset-password - SUCCESS - Timestamp: {}", java.time.LocalDateTime.now());
	}
}
