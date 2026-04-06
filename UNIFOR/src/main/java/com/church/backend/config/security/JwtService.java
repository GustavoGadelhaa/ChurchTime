package com.church.backend.config.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Slf4j
@Service
public class JwtService {

	private static final String CLAIM_ROLE = "role";
	private static final String CLAIM_USER_ID = "userId";
	private static final String CLAIM_NAME = "name";
	private static final String DEFAULT_ROLE = "MEMBER";

	@Value("${security.jwt.secret-key}")
	private String secretKey;

	@Value("${security.jwt.expiration-time}")
	private long jwtExpiration;

	public String extractUsername(String token) {
		return extractClaim(token, Claims::getSubject);
	}

	public Long extractUserId(String token) {
		try {
			Claims claims = extractAllClaims(token);
			Object userIdObj = claims.get(CLAIM_USER_ID);
			if (userIdObj == null) {
				return null;
			}
			if (userIdObj instanceof Long l) {
				return l;
			}
			if (userIdObj instanceof Integer i) {
				return i.longValue();
			}
			if (userIdObj instanceof Number n) {
				return n.longValue();
			}
			return null;
		}
		catch (Exception e) {
			log.warn("Erro ao extrair userId do token: {}", e.getMessage());
			return null;
		}
	}

	public String extractRole(String token) {
		try {
			Claims claims = extractAllClaims(token);
			String role = claims.get(CLAIM_ROLE, String.class);
			return role != null && !role.isBlank() ? role : DEFAULT_ROLE;
		}
		catch (Exception e) {
			log.warn("Erro ao extrair role do token: {}", e.getMessage());
			return DEFAULT_ROLE;
		}
	}

	public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
		final Claims claims = extractAllClaims(token);
		return claimsResolver.apply(claims);
	}

	public String generateToken(String email, Long userId, String role, String name) {
		Map<String, Object> claims = buildClaims(userId, role, name);
		return buildToken(claims, email, jwtExpiration);
	}

	private Map<String, Object> buildClaims(Long userId, String role, String name) {
		Map<String, Object> claims = new HashMap<>();
		if (userId != null) {
			claims.put(CLAIM_USER_ID, userId);
		}
		if (role != null && !role.isBlank()) {
			claims.put(CLAIM_ROLE, role);
		}
		if (name != null && !name.isBlank()) {
			claims.put(CLAIM_NAME, name);
		}
		return claims;
	}

	private String buildToken(Map<String, Object> extraClaims, String subject, long expiration) {
		return Jwts.builder()
				.claims(extraClaims)
				.subject(subject)
				.issuedAt(new Date(System.currentTimeMillis()))
				.expiration(new Date(System.currentTimeMillis() + expiration))
				.signWith(getSignInKey())
				.compact();
	}

	public boolean isTokenValid(String token, String username) {
		final String extractedUsername = extractUsername(token);
		return extractedUsername != null
				&& extractedUsername.equalsIgnoreCase(username)
				&& !isTokenExpired(token);
	}

	private boolean isTokenExpired(String token) {
		return extractExpiration(token).before(new Date());
	}

	private Date extractExpiration(String token) {
		return extractClaim(token, Claims::getExpiration);
	}

	private Claims extractAllClaims(String token) {
		return Jwts.parser()
				.verifyWith(getSignInKey())
				.build()
				.parseSignedClaims(token)
				.getPayload();
	}

	private SecretKey getSignInKey() {
		byte[] keyBytes = Decoders.BASE64.decode(secretKey);
		return Keys.hmacShaKeyFor(keyBytes);
	}
}
