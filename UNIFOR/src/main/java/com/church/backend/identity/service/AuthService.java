package com.church.backend.identity.service;

import com.church.backend.config.security.JwtService;
import com.church.backend.identity.dto.AuthDtos.LoginRequest;
import com.church.backend.identity.dto.AuthDtos.RegisterRequest;
import com.church.backend.identity.dto.AuthDtos.TokenResponse;
import com.church.backend.identity.entity.User;
import com.church.backend.identity.entity.UserRole;
import com.church.backend.identity.repository.UserRepository;
import com.church.backend.shared.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

	private final AuthenticationManager authenticationManager;
	private final UserRepository userRepository;
	private final JwtService jwtService;
	private final PasswordEncoder passwordEncoder;

	@Transactional(readOnly = true)
	public TokenResponse login(LoginRequest request) {
		String email = request.getEmail();
		authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, request.getPassword()));
		var user = userRepository.findByEmailIgnoreCase(email).orElseThrow();
		return new TokenResponse(jwtService.generateToken(user.getEmail(), user.getId(), user.getRole().name(), user.getName()));
	}

	@Transactional
	public TokenResponse register(RegisterRequest request) {
		String email = request.getEmail();
		if (userRepository.findByEmailIgnoreCase(email).isPresent()) {
			throw new BadRequestException("E-mail já cadastrado");
		}
		User user = User.builder()
				.name(request.getName())
				.email(email)
				.passwordHash(passwordEncoder.encode(request.getPassword()))
				.phone(request.getPhone())
				.role(UserRole.MEMBER)
				.build();
		user = userRepository.save(user);
		return new TokenResponse(jwtService.generateToken(user.getEmail(), user.getId(), user.getRole().name(), user.getName()));
	}
}
