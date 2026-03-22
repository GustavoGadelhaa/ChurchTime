package com.church.backend.identity.dto;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.io.IOException;
import java.util.Locale;

public final class AuthDtos {

	private AuthDtos() {
	}

	public record LoginRequest(
			@NotBlank(message = "E-mail é obrigatório")
			@Email(message = "E-mail inválido")
			@JsonDeserialize(using = NormalizedEmailJsonDeserializer.class)
			String email,
			@NotBlank(message = "Senha é obrigatória")
			@Size(min = 1, max = 200, message = "Senha deve ter entre 1 e 200 caracteres")
			String password
	) {
	}

	public record RegisterRequest(
			@NotBlank(message = "Nome é obrigatório")
			@Size(max = 100, message = "Nome deve ter no máximo 100 caracteres")
			@JsonDeserialize(using = TrimJsonDeserializer.class)
			String name,
			@NotBlank(message = "E-mail é obrigatório")
			@Email(message = "E-mail inválido")
			@Size(max = 150, message = "E-mail deve ter no máximo 150 caracteres")
			@JsonDeserialize(using = NormalizedEmailJsonDeserializer.class)
			String email,
			@NotBlank(message = "Senha é obrigatória")
			@Size(min = 6, max = 100, message = "Senha deve ter entre 6 e 100 caracteres")
			String password,
			@Size(max = 20, message = "Telefone deve ter no máximo 20 caracteres")
			@JsonDeserialize(using = TrimBlankToNullJsonDeserializer.class)
			String phone
	) {
	}

	public record TokenResponse(String accessToken) {
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
