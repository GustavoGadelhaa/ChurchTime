package com.church.backend.identity.controller;

import com.church.backend.identity.dto.UserDtos.AssignUserGroupRequest;
import com.church.backend.identity.dto.UserDtos.CreateUserRequest;
import com.church.backend.identity.dto.UserDtos.UpdateUserRequest;
import com.church.backend.identity.dto.UserDtos.UserResponse;
import com.church.backend.identity.service.UserService;
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
@Tag(name = "Users", description = "CRUD de usuários/membros")
public class UserController {

	private final UserService userService;

	@PostMapping("/api/users")
	@ResponseStatus(HttpStatus.CREATED)
	@Operation(summary = "Criar usuário", description = "Cria um novo usuário (membro/líder). Requer role ADMIN.")
	@ApiResponses({
			@ApiResponse(responseCode = "201", description = "Usuário criado com sucesso"),
			@ApiResponse(responseCode = "400", description = "Dados inválidos"),
			@ApiResponse(responseCode = "403", description = "Sem permissão"),
			@ApiResponse(responseCode = "409", description = "E-mail já cadastrado")
	})
	public UserResponse create(@RequestBody @Valid CreateUserRequest request) {
		return userService.create(request);
	}

	@GetMapping("/api/users")
	@Operation(summary = "Listar usuários", description = "Lista todos os usuários ativos. Requer role ADMIN.")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Lista de usuários retornada"),
			@ApiResponse(responseCode = "403", description = "Sem permissão")
	})
	public List<UserResponse> list() {
		return userService.list();
	}

	@GetMapping("/api/users/{id}")
	@Operation(summary = "Buscar usuário por ID", description = "Retorna um usuário pelo ID. Requer ADMIN ou LEADER (escopo).")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Usuário encontrado"),
			@ApiResponse(responseCode = "404", description = "Usuário não encontrado"),
			@ApiResponse(responseCode = "403", description = "Sem permissão")
	})
	public UserResponse get(@PathVariable Long id) {
		return userService.get(id);
	}

	@GetMapping("/api/groups/{groupId}/users")
	@Operation(summary = "Listar usuários do grupo", description = "Lista todos os usuários de um grupo. Requer ADMIN ou LEADER.")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Lista de usuários do grupo"),
			@ApiResponse(responseCode = "403", description = "Sem permissão")
	})
	public List<UserResponse> listByGroup(@PathVariable Long groupId) {
		return userService.listByGroup(groupId);
	}

	@PutMapping("/api/users/{id}")
	@Operation(summary = "Atualizar usuário", description = "Atualiza dados do usuário. Requer role ADMIN.")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Usuário atualizado"),
			@ApiResponse(responseCode = "400", description = "Dados inválidos"),
			@ApiResponse(responseCode = "404", description = "Usuário não encontrado"),
			@ApiResponse(responseCode = "403", description = "Sem permissão")
	})
	public UserResponse update(@PathVariable Long id, @RequestBody @Valid UpdateUserRequest request) {
		return userService.update(id, request);
	}

	@PutMapping("/api/users/{id}/group")
	@Operation(summary = "Atribuir grupo ao usuário", description = "Move usuário para outro grupo. Requer role ADMIN.")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Grupo atribuído"),
			@ApiResponse(responseCode = "400", description = "Dados inválidos"),
			@ApiResponse(responseCode = "404", description = "Usuário ou grupo não encontrado"),
			@ApiResponse(responseCode = "403", description = "Sem permissão")
	})
	public UserResponse assignGroup(@PathVariable Long id, @RequestBody @Valid AssignUserGroupRequest request) {
		return userService.assignGroup(id, request);
	}

	@DeleteMapping("/api/users/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@Operation(summary = "Deletar usuário", description = "Soft delete do usuário (active=false). Requer role ADMIN.")
	@ApiResponses({
			@ApiResponse(responseCode = "204", description = "Usuário deletado"),
			@ApiResponse(responseCode = "404", description = "Usuário não encontrado"),
			@ApiResponse(responseCode = "403", description = "Sem permissão")
	})
	public void delete(@PathVariable Long id) {
		userService.delete(id);
	}
}
