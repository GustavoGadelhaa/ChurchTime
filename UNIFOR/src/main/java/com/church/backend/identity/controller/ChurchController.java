package com.church.backend.identity.controller;

import com.church.backend.identity.dto.ChurchDtos.ChurchResponse;
import com.church.backend.identity.dto.ChurchDtos.CreateChurchRequest;
import com.church.backend.identity.dto.ChurchDtos.UpdateChurchRequest;
import com.church.backend.identity.service.ChurchService;
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
@RequestMapping("/api/churches")
@RequiredArgsConstructor
@Tag(name = "Churches", description = "CRUD de igrejas (requer ADMIN)")
public class ChurchController {

	private final ChurchService churchService;

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	@Operation(summary = "Criar igreja", description = "Cria uma nova igreja. Requer role ADMIN.")
	@ApiResponses({
			@ApiResponse(responseCode = "201", description = "Igreja criada com sucesso"),
			@ApiResponse(responseCode = "400", description = "Dados inválidos"),
			@ApiResponse(responseCode = "403", description = "Sem permissão")
	})
	public ChurchResponse create(@RequestBody @Valid CreateChurchRequest request) {
		return churchService.create(request);
	}

	@GetMapping
	@Operation(summary = "Listar igrejas", description = "Lista todas as igrejas ativas. Requer role ADMIN.")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Lista de igrejas retornada"),
			@ApiResponse(responseCode = "403", description = "Sem permissão")
	})
	public List<ChurchResponse> list() {
		return churchService.list();
	}

	@GetMapping("/{id}")
	@Operation(summary = "Buscar igreja por ID", description = "Retorna uma igreja pelo ID. Requer role ADMIN.")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Igreja encontrada"),
			@ApiResponse(responseCode = "404", description = "Igreja não encontrada"),
			@ApiResponse(responseCode = "403", description = "Sem permissão")
	})
	public ChurchResponse get(@PathVariable Long id) {
		return churchService.get(id);
	}

	@PutMapping("/{id}")
	@Operation(summary = "Atualizar igreja", description = "Atualiza o nome da igreja. Requer role ADMIN.")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Igreja atualizada"),
			@ApiResponse(responseCode = "400", description = "Dados inválidos"),
			@ApiResponse(responseCode = "404", description = "Igreja não encontrada"),
			@ApiResponse(responseCode = "403", description = "Sem permissão")
	})
	public ChurchResponse update(@PathVariable Long id, @RequestBody @Valid UpdateChurchRequest request) {
		return churchService.update(id, request);
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@Operation(summary = "Deletar igreja", description = "Soft delete da igreja (active=false). Requer role ADMIN.")
	@ApiResponses({
			@ApiResponse(responseCode = "204", description = "Igreja deletada"),
			@ApiResponse(responseCode = "404", description = "Igreja não encontrada"),
			@ApiResponse(responseCode = "403", description = "Sem permissão")
	})
	public void delete(@PathVariable Long id) {
		churchService.delete(id);
	}
}
