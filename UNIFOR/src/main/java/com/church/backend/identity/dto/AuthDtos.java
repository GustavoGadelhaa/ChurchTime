package com.church.backend.identity.dto;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.util.Locale;

public final class AuthDtos {

	private AuthDtos() {
	}

	@Data
	@AllArgsConstructor
	public static class LoginRequest {
		@NotBlank(message = "E-mail é obrigatório")
		@Email(message = "E-mail inválido")
		@JsonDeserialize(using = NormalizedEmailJsonDeserializer.class)
		private String email;

		@NotBlank(message = "Senha é obrigatória")
		@Size(min = 1, max = 200, message = "Senha deve ter entre 1 e 200 caracteres")
		private String password;
	}

	@Data
	@AllArgsConstructor
	public static class RegisterRequest {
		@NotBlank(message = "Nome é obrigatório")
		@Size(max = 100, message = "Nome deve ter no máximo 100 caracteres")
		@JsonDeserialize(using = TrimJsonDeserializer.class)
		private String name;

		@NotBlank(message = "E-mail é obrigatório")
		@Email(message = "E-mail inválido")
		@Size(max = 150, message = "E-mail deve ter no máximo 150 caracteres")
		@JsonDeserialize(using = NormalizedEmailJsonDeserializer.class)
		private String email;

		@NotBlank(message = "Senha é obrigatória")
		@Size(min = 6, max = 100, message = "Senha deve ter entre 6 e 100 caracteres")
		private String password;

		@Size(max = 20, message = "Telefone deve ter no máximo 20 caracteres")
		@JsonDeserialize(using = TrimBlankToNullJsonDeserializer.class)
		private String phone;
	}

	@Data
	@AllArgsConstructor
	public static class TokenResponse {
		private String accessToken;
	}

	@Data
	@AllArgsConstructor
	public static class ForgotPasswordRequest {
		@NotBlank(message = "E-mail é obrigatório")
		@Email(message = "E-mail inválido")
		@JsonDeserialize(using = NormalizedEmailJsonDeserializer.class)
		private String email;
	}

	@Data
	@AllArgsConstructor
	public static class ResetPasswordRequest {
		@NotBlank(message = "Token é obrigatório")
		@Size(min = 6, max = 6, message = "Token deve ter 6 dígitos")
		private String token;

		@NotBlank(message = "Nova senha é obrigatória")
		@Size(min = 6, max = 100, message = "Senha deve ter entre 6 e 100 caracteres")
		private String newPassword;
	}

	public static final class TrimJsonDeserializer extends JsonDeserializer<String> {

		@Override
		public String deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
			String v = p.getValueAsString();
			if (v == null) {
				return null;
			}
			return v.trim();
		}
	}

	public static final class TrimBlankToNullJsonDeserializer extends JsonDeserializer<String> {

		@Override
		public String deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
			String v = p.getValueAsString();
			if (v == null) {
				return null;
			}
			String t = v.trim();
			return t.isEmpty() ? null : t;
		}
	}

	public static final class NormalizedEmailJsonDeserializer extends JsonDeserializer<String> {

		@Override
		public String deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
			String v = p.getValueAsString();
			if (v == null) {
				return null;
			}
			return v.trim().toLowerCase(Locale.ROOT);
		}
	}
}
