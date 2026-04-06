package com.church.backend.attendance.controller;

import com.church.backend.attendance.dto.EventDtos.CreateEventRequest;
import com.church.backend.attendance.dto.EventDtos.EventResponse;
import com.church.backend.attendance.dto.EventDtos.UpdateEventRequest;
import com.church.backend.attendance.service.EventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "Events", description = "CRUD de eventos de grupos")
public class EventController {

	private final EventService eventService;

	@PostMapping("/api/groups/{groupId}/events")
	@ResponseStatus(HttpStatus.CREATED)
	@Operation(summary = "Criar evento", description = "Cria um novo evento para um grupo. Requer ADMIN ou LEADER.")
	@ApiResponses({
			@ApiResponse(responseCode = "201", description = "Evento criado com sucesso"),
			@ApiResponse(responseCode = "400", description = "Dados inválidos"),
			@ApiResponse(responseCode = "404", description = "Grupo não encontrado"),
			@ApiResponse(responseCode = "403", description = "Sem permissão")
	})
	public EventResponse create(@PathVariable Long groupId, @RequestBody @Valid CreateEventRequest request) {
		return eventService.create(groupId, request);
	}

	@GetMapping("/api/groups/{groupId}/events")
	@Operation(summary = "Listar eventos do grupo", description = "Lista todos os eventos de um grupo. ADMIN, LEADER ou MEMBER podem acessar.")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Lista de eventos retornada"),
			@ApiResponse(responseCode = "404", description = "Grupo não encontrado"),
			@ApiResponse(responseCode = "403", description = "Sem permissão")
	})
	public List<EventResponse> listByGroup(@PathVariable Long groupId) {
		return eventService.listByGroup(groupId);
	}

	@GetMapping("/api/events/{id}")
	@Operation(summary = "Buscar evento por ID", description = "Retorna um evento pelo ID. ADMIN, LEADER ou MEMBER do grupo podem acessar.")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Evento encontrado"),
			@ApiResponse(responseCode = "404", description = "Evento não encontrado"),
			@ApiResponse(responseCode = "403", description = "Sem permissão")
	})
	public EventResponse get(@PathVariable Long id) {
		return eventService.getForCheckin(id);
	}

	@PutMapping("/api/events/{id}")
	@Operation(summary = "Atualizar evento", description = "Atualiza título, local, data ou status do evento. Requer ADMIN ou LEADER.")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Evento atualizado"),
			@ApiResponse(responseCode = "400", description = "Dados inválidos"),
			@ApiResponse(responseCode = "404", description = "Evento não encontrado"),
			@ApiResponse(responseCode = "403", description = "Sem permissão")
	})
	public EventResponse update(@PathVariable Long id, @RequestBody @Valid UpdateEventRequest request) {
		return eventService.update(id, request);
	}

	@DeleteMapping("/api/events/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@Operation(summary = "Deletar evento", description = "Deleção permanente do evento. Requer ADMIN ou LEADER.")
	@ApiResponses({
			@ApiResponse(responseCode = "204", description = "Evento deletado"),
			@ApiResponse(responseCode = "404", description = "Evento não encontrado"),
			@ApiResponse(responseCode = "403", description = "Sem permissão")
	})
	public void delete(@PathVariable Long id) {
		eventService.delete(id);
	}
}
