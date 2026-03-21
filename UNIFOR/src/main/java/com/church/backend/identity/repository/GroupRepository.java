package com.church.backend.identity.repository;

import com.church.backend.identity.entity.Group;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GroupRepository extends JpaRepository<Group, Long> {

	List<Group> findByChurchIdAndActiveTrueOrderByNameAsc(Long churchId);

	boolean existsByChurchIdAndLeaderIdAndActiveTrue(Long churchId, Long leaderUserId);

	List<Group> findByLeaderIdAndActiveTrue(Long leaderUserId);
}
