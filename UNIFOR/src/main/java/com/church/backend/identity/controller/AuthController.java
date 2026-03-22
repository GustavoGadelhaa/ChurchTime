package com.church.backend.identity.controller;

import com.church.backend.identity.dto.AuthDtos.LoginRequest;
import com.church.backend.identity.dto.AuthDtos.RegisterRequest;
import com.church.backend.identity.dto.AuthDtos.TokenResponse;
import com.church.backend.identity.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

	private final AuthService authService;

	@PostMapping("/login")
	public TokenResponse login(@RequestBody @Valid LoginRequest request) {
		return authService.login(request);
	}

	@PostMapping("/register")
	@ResponseStatus(HttpStatus.CREATED)
	public TokenResponse register(@RequestBody @Valid RegisterRequest request) {
		return authService.register(request);
	}
}
