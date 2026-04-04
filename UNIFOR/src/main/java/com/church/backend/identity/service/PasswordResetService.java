package com.church.backend.identity.service;

import com.church.backend.identity.dto.AuthDtos.ForgotPasswordRequest;
import com.church.backend.identity.dto.AuthDtos.ResetPasswordRequest;
import com.church.backend.identity.entity.PasswordResetToken;
import com.church.backend.identity.entity.User;
import com.church.backend.identity.repository.PasswordResetTokenRepository;
import com.church.backend.identity.repository.UserRepository;
import com.church.backend.shared.exception.BadRequestException;
import com.church.backend.shared.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Instant;

@Service
@RequiredArgsConstructor
@Slf4j
public class PasswordResetService {

	private static final int TOKEN_LENGTH = 6;
	private static final int EXPIRATION_MINUTES = 15;
	private static final SecureRandom SECURE_RANDOM = new SecureRandom();

	private final UserRepository userRepository;
	private final PasswordResetTokenRepository tokenRepository;
	private final JavaMailSender mailSender;
	private final PasswordEncoder passwordEncoder;

	@Value("${app.mail.from:ChurchTime <techjga@gmail.com>}")
	private String mailFrom;

	@Transactional
	public void requestReset(ForgotPasswordRequest request) {
		userRepository.findByEmailIgnoreCase(request.email())
				.ifPresentOrElse(user -> {
					tokenRepository.markAllUsedByUserId(user.getId());
					String token = generateToken();
					Instant expiresAt = Instant.now().plusSeconds(EXPIRATION_MINUTES * 60L);

					PasswordResetToken resetToken = PasswordResetToken.builder()
							.user(user)
							.token(token)
							.expiresAt(expiresAt)
							.build();
					tokenRepository.save(resetToken);

					sendResetEmail(user.getEmail(), user.getName(), token);
				}, () -> {
					log.info("Password reset requested for non-existent email: {}", request.email());
				});
	}

	@Transactional
	public void resetPassword(ResetPasswordRequest request) {
		PasswordResetToken resetToken = tokenRepository.findByToken(request.token())
				.orElseThrow(() -> new BadRequestException("Token inválido"));

		if (resetToken.isUsed()) {
			throw new BadRequestException("Token já utilizado");
		}

		if (resetToken.isExpired()) {
			throw new BadRequestException("Token expirado");
		}

		User user = resetToken.getUser();
		user.setPasswordHash(passwordEncoder.encode(request.newPassword()));
		userRepository.save(user);

		resetToken.setUsed(true);
		tokenRepository.save(resetToken);
	}

	private String generateToken() {
		int token = SECURE_RANDOM.nextInt((int) Math.pow(10, TOKEN_LENGTH));
		return String.format("%0" + TOKEN_LENGTH + "d", token);
	}

	private void sendResetEmail(String to, String name, String token) {
		try {
			SimpleMailMessage message = new SimpleMailMessage();
			message.setFrom(mailFrom);
			message.setTo(to);
			message.setSubject("ChurchTime - Recuperação de Senha");
			message.setText(
					"Olá, " + name + "!\n\n" +
					"Recebemos uma solicitação de recuperação de senha para sua conta no ChurchTime.\n\n" +
					"Seu código de recuperação é: " + token + "\n\n" +
					"Este código expira em " + EXPIRATION_MINUTES + " minutos.\n" +
					"Se você não solicitou esta recuperação, ignore este email.\n\n" +
					"Atenciosamente,\n" +
					"Equipe ChurchTime"
			);
			mailSender.send(message);
			log.info("Password reset email sent to {}", to);
		} catch (Exception e) {
			log.error("Failed to send password reset email to {}: {}", to, e.getMessage());
			throw new RuntimeException("Não foi possível enviar o e-mail de recuperação. Verifique a configuração do servidor de e-mail.");
		}
	}
}
