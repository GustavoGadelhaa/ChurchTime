package com.church.backend.config.security;

import com.church.backend.identity.entity.User;
import com.church.backend.identity.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountUserDetailsService implements UserDetailsService {

	private final UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		log.debug("Loading user by email: {}", email);

		User user = userRepository.findByEmailIgnoreCase(email)
				.orElseThrow(() -> {
					log.warn("User not found with email: {}", email);
					return new UsernameNotFoundException("User not found with email: " + email);
				});

		if (!user.isActive()) {
			log.warn("User inactive: {}", email);
			throw new UsernameNotFoundException("User not found with email: " + email);
		}

		return org.springframework.security.core.userdetails.User.builder()
				.username(user.getEmail())
				.password(user.getPasswordHash())
				.authorities(Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole().name())))
				.accountExpired(false)
				.accountLocked(false)
				.credentialsExpired(false)
				.disabled(false)
				.build();
	}
}
