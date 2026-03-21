package com.church.backend.identity.repository;

import com.church.backend.identity.entity.Church;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChurchRepository extends JpaRepository<Church, Long> {

	List<Church> findAllByActiveTrueOrderByNameAsc();
}
