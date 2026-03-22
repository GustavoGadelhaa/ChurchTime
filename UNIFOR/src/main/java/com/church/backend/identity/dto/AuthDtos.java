package com.church.backend.identity.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public final class AuthDtos {

	private AuthDtos() {
	}

	public record LoginRequest(
			@NotBlank @Email String email,
			@NotBlank @Size(min = 1, max = 200) String password
	) {
	}

	public record RegisterRequest(
			@NotBlank @Size(max = 100) String name,
			@NotBlank @Email @Size(max = 150) String email,
			@NotBlank @Size(min = 6, max = 100) String password,
			@Size(max = 20) String phone
	) {
	}

	public record TokenResponse(String accessToken) {
	}
}
