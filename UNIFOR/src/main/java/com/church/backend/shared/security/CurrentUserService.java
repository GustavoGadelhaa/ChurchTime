package com.church.backend.shared.security;

import com.church.backend.identity.entity.User;
import com.church.backend.identity.repository.UserRepository;
import com.church.backend.shared.exception.ApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CurrentUserService {

	private final UserRepository userRepository;

	@Transactional(readOnly = true)
	public User requireCurrent() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth == null || !auth.isAuthenticated()) {
			throw new ApiException(HttpStatus.UNAUTHORIZED, "Não autenticado");
		}
		String email = auth.getName();
		return userRepository.findByEmailForSession(email)
				.orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "Sessão inválida"));
	}
}
