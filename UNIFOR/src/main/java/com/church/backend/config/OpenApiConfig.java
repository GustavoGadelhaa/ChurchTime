package com.church.backend.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

	@Bean
	OpenAPI openAPI() {
		String securitySchemeName = "Bearer Authentication";

		return new OpenAPI()
				.info(new Info()
						.title("ChurchTime API")
						.version("1.0.0")
						.description("Backend para gerenciamento de presença em eventos de igrejas. "
								+ "Organize igrejas, grupos (células/ministérios), usuários e eventos com controle de check-in.")
						.contact(new Contact()
								.name("ChurchTime")
								.email("techjga@gmail.com"))
						.license(new License()
								.name("Proprietary")
								.url("https://github.com/anomalyco/opencode/issues")))
				.addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
				.components(new Components()
						.addSecuritySchemes(securitySchemeName,
								new SecurityScheme()
										.name(securitySchemeName)
										.type(SecurityScheme.Type.HTTP)
										.scheme("bearer")
										.bearerFormat("JWT")
										.description("Insira o token JWT retornado no login/registro")));
	}
}
