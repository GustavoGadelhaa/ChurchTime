package com.church.backend.identity.controller;

import com.church.backend.identity.dto.GroupDtos.AssignLeaderRequest;
import com.church.backend.identity.dto.GroupDtos.CreateGroupRequest;
import com.church.backend.identity.dto.GroupDtos.GroupResponse;
import com.church.backend.identity.dto.GroupDtos.UpdateGroupRequest;
import com.church.backend.identity.service.GroupService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class GroupController {

	private final GroupService groupService;

	@PostMapping("/api/churches/{churchId}/groups")
	@ResponseStatus(HttpStatus.CREATED)
	public GroupResponse create(@PathVariable Long churchId, @RequestBody @Valid CreateGroupRequest request) {
		return groupService.create(churchId, request);
	}

	@GetMapping("/api/churches/{churchId}/groups")
	public List<GroupResponse> listByChurch(@PathVariable Long churchId) {
		return groupService.listByChurch(churchId);
	}

	@GetMapping("/api/groups/{id}")
	public GroupResponse get(@PathVariable Long id) {
		return groupService.get(id);
	}

	@PutMapping("/api/groups/{id}")
	public GroupResponse update(@PathVariable Long id, @RequestBody @Valid UpdateGroupRequest request) {
		return groupService.update(id, request);
	}

	@PutMapping("/api/groups/{id}/leader")
	public GroupResponse assignLeader(@PathVariable Long id, @RequestBody AssignLeaderRequest request) {
		return groupService.assignLeader(id, request);
	}

	@DeleteMapping("/api/groups/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(@PathVariable Long id) {
		groupService.delete(id);
	}
}
