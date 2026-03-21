package com.church.backend.identity.service;

import com.church.backend.config.JwtService;
import com.church.backend.identity.dto.AuthDtos.LoginRequest;
import com.church.backend.identity.dto.AuthDtos.TokenResponse;
import com.church.backend.identity.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

	private final AuthenticationManager authenticationManager;
	private final UserRepository userRepository;
	private final JwtService jwtService;

	@Transactional(readOnly = true)
	public TokenResponse login(LoginRequest request) {
		String email = request.email().trim();
		authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, request.password()));
		var user = userRepository.findByEmailIgnoreCase(email).orElseThrow();
		return new TokenResponse(jwtService.generate(user));
	}
}
