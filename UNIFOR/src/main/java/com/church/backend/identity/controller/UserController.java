package com.church.backend.identity.controller;

import com.church.backend.identity.dto.UserDtos.AssignUserGroupRequest;
import com.church.backend.identity.dto.UserDtos.CreateUserRequest;
import com.church.backend.identity.dto.UserDtos.UpdateUserRequest;
import com.church.backend.identity.dto.UserDtos.UserResponse;
import com.church.backend.identity.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class UserController {

	private final UserService userService;

	@PostMapping("/api/users")
	@ResponseStatus(HttpStatus.CREATED)
	public UserResponse create(@RequestBody @Valid CreateUserRequest request) {
		return userService.create(request);
	}

	@GetMapping("/api/users")
	public List<UserResponse> list() {
		return userService.list();
	}

	@GetMapping("/api/users/{id}")
	public UserResponse get(@PathVariable Long id) {
		return userService.get(id);
	}

	@GetMapping("/api/groups/{groupId}/users")
	public List<UserResponse> listByGroup(@PathVariable Long groupId) {
		return userService.listByGroup(groupId);
	}

	@PutMapping("/api/users/{id}")
	public UserResponse update(@PathVariable Long id, @RequestBody @Valid UpdateUserRequest request) {
		return userService.update(id, request);
	}

	@PutMapping("/api/users/{id}/group")
	public UserResponse assignGroup(@PathVariable Long id, @RequestBody @Valid AssignUserGroupRequest request) {
		return userService.assignGroup(id, request);
	}

	@DeleteMapping("/api/users/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(@PathVariable Long id) {
		userService.delete(id);
	}
}
