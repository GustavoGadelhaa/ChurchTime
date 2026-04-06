package com.church.backend.identity.controller;

import com.church.backend.identity.dto.GroupDtos.AssignLeaderRequest;
import com.church.backend.identity.dto.GroupDtos.CreateGroupRequest;
import com.church.backend.identity.dto.GroupDtos.GroupResponse;
import com.church.backend.identity.dto.GroupDtos.UpdateGroupRequest;
import com.church.backend.identity.service.GroupService;
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
@Tag(name = "Groups", description = "CRUD de grupos (células/ministérios)")
public class GroupController {

	private final GroupService groupService;

	@PostMapping("/api/churches/{churchId}/groups")
	@ResponseStatus(HttpStatus.CREATED)
	@Operation(summary = "Criar grupo", description = "Cria um novo grupo/célula vinculado a uma igreja. Requer role ADMIN.")
	@ApiResponses({
			@ApiResponse(responseCode = "201", description = "Grupo criado com sucesso"),
			@ApiResponse(responseCode = "400", description = "Dados inválidos"),
			@ApiResponse(responseCode = "404", description = "Igreja não encontrada"),
			@ApiResponse(responseCode = "403", description = "Sem permissão")
	})
	public GroupResponse create(@PathVariable Long churchId, @RequestBody @Valid CreateGroupRequest request) {
		return groupService.create(churchId, request);
	}

	@GetMapping("/api/churches/{churchId}/groups")
	@Operation(summary = "Listar grupos da igreja", description = "Lista todos os grupos ativos de uma igreja. Acessível para usuários autenticados.")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Lista de grupos retornada"),
			@ApiResponse(responseCode = "404", description = "Igreja não encontrada"),
			@ApiResponse(responseCode = "401", description = "Não autenticado")
	})
	public List<GroupResponse> listByChurch(@PathVariable Long churchId) {
		return groupService.listByChurch(churchId);
	}

	@GetMapping("/api/groups")
	@Operation(summary = "Listar grupos da minha igreja", description = "Lista todos os grupos ativos da igreja do usuário autenticado. Qualquer role autenticada pode acessar.")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Lista de grupos retornada"),
			@ApiResponse(responseCode = "400", description = "Usuário sem grupo associado"),
			@ApiResponse(responseCode = "401", description = "Não autenticado")
	})
	public List<GroupResponse> listMyChurchGroups() {
		return groupService.listMyChurchGroups();
	}

	@GetMapping("/api/groups/my-church")
	@Operation(summary = "Listar grupos da minha igreja (alias)", description = "Alias para GET /api/groups. Lista todos os grupos ativos da igreja do usuário autenticado.")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Lista de grupos retornada"),
			@ApiResponse(responseCode = "400", description = "Usuário sem grupo associado"),
			@ApiResponse(responseCode = "401", description = "Não autenticado")
	})
	public List<GroupResponse> listMyChurchGroupsAlias() {
		return groupService.listMyChurchGroups();
	}

	@PutMapping("/api/groups/{id}/join")
	@Operation(summary = "Entrar em um grupo", description = "Membro troca para outro grupo da mesma igreja. O grupo de destino deve estar ativo e pertencer à mesma igreja do usuário.")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Grupo trocado com sucesso"),
			@ApiResponse(responseCode = "400", description = "Usuário sem grupo associado"),
			@ApiResponse(responseCode = "403", description = "Apenas membros ou grupo de outra igreja"),
			@ApiResponse(responseCode = "404", description = "Grupo não encontrado")
	})
	public GroupResponse joinGroup(@PathVariable Long id) {
		return groupService.joinGroup(id);
	}

	@GetMapping("/api/groups/{id}")
	@Operation(summary = "Buscar grupo por ID", description = "Retorna um grupo pelo ID. Requer ADMIN ou LEADER.")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Grupo encontrado"),
			@ApiResponse(responseCode = "404", description = "Grupo não encontrado"),
			@ApiResponse(responseCode = "403", description = "Sem permissão")
	})
	public GroupResponse get(@PathVariable Long id) {
		return groupService.get(id);
	}

	@PutMapping("/api/groups/{id}")
	@Operation(summary = "Atualizar grupo", description = "Atualiza nome e descrição do grupo. Requer role ADMIN.")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Grupo atualizado"),
			@ApiResponse(responseCode = "400", description = "Dados inválidos"),
			@ApiResponse(responseCode = "404", description = "Grupo não encontrado"),
			@ApiResponse(responseCode = "403", description = "Sem permissão")
	})
	public GroupResponse update(@PathVariable Long id, @RequestBody @Valid UpdateGroupRequest request) {
		return groupService.update(id, request);
	}

	@PutMapping("/api/groups/{id}/leader")
	@Operation(summary = "Atribuir/remover líder do grupo", description = "Define ou remove o líder de um grupo. Requer role ADMIN.")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Líder atualizado"),
			@ApiResponse(responseCode = "400", description = "Dados inválidos"),
			@ApiResponse(responseCode = "404", description = "Grupo ou usuário não encontrado"),
			@ApiResponse(responseCode = "403", description = "Sem permissão")
	})
	public GroupResponse assignLeader(@PathVariable Long id, @RequestBody AssignLeaderRequest request) {
		return groupService.assignLeader(id, request);
	}

	@DeleteMapping("/api/groups/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@Operation(summary = "Deletar grupo", description = "Soft delete do grupo (active=false). Requer role ADMIN.")
	@ApiResponses({
			@ApiResponse(responseCode = "204", description = "Grupo deletado"),
			@ApiResponse(responseCode = "404", description = "Grupo não encontrado"),
			@ApiResponse(responseCode = "403", description = "Sem permissão")
	})
	public void delete(@PathVariable Long id) {
		groupService.delete(id);
	}
}
