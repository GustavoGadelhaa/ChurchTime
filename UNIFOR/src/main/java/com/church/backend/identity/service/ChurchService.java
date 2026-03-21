package com.church.backend.identity.service;

import com.church.backend.identity.dto.ChurchDtos.ChurchResponse;
import com.church.backend.identity.dto.ChurchDtos.CreateChurchRequest;
import com.church.backend.identity.dto.ChurchDtos.UpdateChurchRequest;
import com.church.backend.identity.entity.Church;
import com.church.backend.identity.repository.ChurchRepository;
import com.church.backend.shared.exception.NotFoundException;
import com.church.backend.shared.security.AccessPolicy;
import com.church.backend.shared.security.CurrentUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ChurchService {

	private final ChurchRepository churchRepository;
	private final CurrentUserService currentUserService;
	private final AccessPolicy accessPolicy;

	@Transactional(readOnly = true)
	public List<ChurchResponse> list() {
		accessPolicy.requireAdmin(currentUserService.requireCurrent());
		return churchRepository.findAllByActiveTrueOrderByNameAsc().stream().map(ChurchService::toResponse).toList();
	}

	@Transactional(readOnly = true)
	public ChurchResponse get(Long id) {
		accessPolicy.requireAdmin(currentUserService.requireCurrent());
		Church church = churchRepository.findById(id).orElseThrow(() -> new NotFoundException("Igreja não encontrada"));
		if (!church.isActive()) {
			throw new NotFoundException("Igreja não encontrada");
		}
		return toResponse(church);
	}

	public ChurchResponse create(CreateChurchRequest request) {
		accessPolicy.requireAdmin(currentUserService.requireCurrent());
		Church church = Church.builder().name(request.name().trim()).build();
		return toResponse(churchRepository.save(church));
	}

	public ChurchResponse update(Long id, UpdateChurchRequest request) {
		accessPolicy.requireAdmin(currentUserService.requireCurrent());
		Church church = churchRepository.findById(id).orElseThrow(() -> new NotFoundException("Igreja não encontrada"));
		if (!church.isActive()) {
			throw new NotFoundException("Igreja não encontrada");
		}
		church.setName(request.name().trim());
		return toResponse(churchRepository.save(church));
	}

	public void delete(Long id) {
		accessPolicy.requireAdmin(currentUserService.requireCurrent());
		Church church = churchRepository.findById(id).orElseThrow(() -> new NotFoundException("Igreja não encontrada"));
		if (!church.isActive()) {
			return;
		}
		church.setActive(false);
	}

	public Church requireActiveChurch(Long id) {
		Church church = churchRepository.findById(id).orElseThrow(() -> new NotFoundException("Igreja não encontrada"));
		if (!church.isActive()) {
			throw new NotFoundException("Igreja não encontrada");
		}
		return church;
	}

	private static ChurchResponse toResponse(Church church) {
		return new ChurchResponse(church.getId(), church.getName(), church.isActive(), church.getCreatedAt());
	}
}
