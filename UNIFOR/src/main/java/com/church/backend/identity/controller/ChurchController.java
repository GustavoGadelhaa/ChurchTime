package com.church.backend.identity.controller;

import com.church.backend.identity.dto.ChurchDtos.ChurchResponse;
import com.church.backend.identity.dto.ChurchDtos.CreateChurchRequest;
import com.church.backend.identity.dto.ChurchDtos.UpdateChurchRequest;
import com.church.backend.identity.service.ChurchService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/churches")
@RequiredArgsConstructor
@Slf4j
public class ChurchController {

	private final ChurchService churchService;

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public ChurchResponse create(@RequestBody @Valid CreateChurchRequest request) {
		log.info("[CHURCH] POST /api/churches - Name: {}, Timestamp: {}", 
				request.getName(), java.time.LocalDateTime.now());
		ChurchResponse response = churchService.create(request);
		log.info("[CHURCH] POST /api/churches - CREATED - ChurchId: {}, Timestamp: {}", 
				response.getId(), java.time.LocalDateTime.now());
		return response;
	}

	@GetMapping
	public List<ChurchResponse> list() {
		return churchService.list();
	}

	@GetMapping("/{id}")
	public ChurchResponse get(@PathVariable Long id) {
		return churchService.get(id);
	}

	@PutMapping("/{id}")
	public ChurchResponse update(@PathVariable Long id, @RequestBody @Valid UpdateChurchRequest request) {
		return churchService.update(id, request);
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(@PathVariable Long id) {
		churchService.delete(id);
	}
}
