package com.church.backend.config.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final JwtService jwtService;

	@Override
	protected void doFilterInternal(
			@NonNull HttpServletRequest request,
			@NonNull HttpServletResponse response,
			@NonNull FilterChain filterChain
	) throws ServletException, IOException {

		final String authHeader = request.getHeader("Authorization");
		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
			filterChain.doFilter(request, response);
			return;
		}

		final String jwt = authHeader.substring(7).trim();
		if (jwt.isEmpty()) {
			filterChain.doFilter(request, response);
			return;
		}

		try {
			final String userEmail = jwtService.extractUsername(jwt);

			if (userEmail != null && !userEmail.isBlank()
					&& SecurityContextHolder.getContext().getAuthentication() == null
					&& jwtService.isTokenValid(jwt, userEmail)) {

				String role = jwtService.extractRole(jwt);
				UserDetails userDetails = User.builder()
						.username(userEmail)
						.password("")
						.authorities(Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role)))
						.build();

				UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
						userDetails,
						null,
						userDetails.getAuthorities()
				);
				authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
				SecurityContextHolder.getContext().setAuthentication(authToken);
			}
		}
		catch (Exception e) {
			log.debug("JWT validation failed: {}", e.getMessage());
		}

		filterChain.doFilter(request, response);
	}
}
