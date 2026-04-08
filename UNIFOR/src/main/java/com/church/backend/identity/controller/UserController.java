package com.church.backend.identity.controller;

import com.church.backend.identity.dto.UserDtos.AssignUserGroupRequest;
import com.church.backend.identity.dto.UserDtos.CreateUserRequest;
import com.church.backend.identity.dto.UserDtos.UpdateUserRequest;
import com.church.backend.identity.dto.UserDtos.UserResponse;
import com.church.backend.identity.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class UserController {

	private final UserService userService;

	@PostMapping("/api/users")
	@ResponseStatus(HttpStatus.CREATED)
	public UserResponse create(@RequestBody @Valid CreateUserRequest request) {
		log.info("[USER] POST /api/users - Name: {}, Email: {}, Timestamp: {}", 
				request.getName(), maskEmail(request.getEmail()), java.time.LocalDateTime.now());
		UserResponse response = userService.create(request);
		log.info("[USER] POST /api/users - CREATED - UserId: {}, Timestamp: {}", 
				response.getId(), java.time.LocalDateTime.now());
		return response;
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

	private String maskEmail(String email) {
		if (email == null) return "null";
		int atIndex = email.indexOf('@');
		if (atIndex <= 1) return "***@***";
		return email.substring(0, 1) + "***" + email.substring(atIndex);
	}
}
