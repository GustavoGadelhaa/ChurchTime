package com.church.backend.attendance.controller;

import com.church.backend.attendance.dto.PresenceDtos.PresenceResponse;
import com.church.backend.attendance.service.PresenceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "Presence", description = "Check-in e presença em eventos")
public class PresenceController {

	private final PresenceService presenceService;

	@PostMapping("/api/events/{eventId}/checkin")
	@ResponseStatus(HttpStatus.CREATED)
	@Operation(summary = "Fazer check-in", description = "Registra check-in do usuário autenticado no evento. Evento deve estar OPEN e usuário deve ser MEMBER do grupo. Corpo vazio {}.")
	@ApiResponses({
			@ApiResponse(responseCode = "201", description = "Check-in realizado com sucesso"),
			@ApiResponse(responseCode = "400", description = "Evento não está aberto"),
			@ApiResponse(responseCode = "403", description = "Sem permissão ou usuário não pertence ao grupo"),
			@ApiResponse(responseCode = "409", description = "Check-in já registrado")
	})
	public PresenceResponse checkIn(@PathVariable Long eventId) {
		return presenceService.checkIn(eventId);
	}

	@GetMapping("/api/events/{eventId}/presences")
	@Operation(summary = "Listar presenças do evento", description = "Lista todos os check-ins de um evento. Requer ADMIN ou LEADER.")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Lista de presenças retornada"),
			@ApiResponse(responseCode = "404", description = "Evento não encontrado"),
			@ApiResponse(responseCode = "403", description = "Sem permissão")
	})
	public List<PresenceResponse> list(@PathVariable Long eventId) {
		return presenceService.listByEvent(eventId);
	}
}
