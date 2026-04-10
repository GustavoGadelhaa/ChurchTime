package com.church.backend.identity.repository;

import com.church.backend.identity.entity.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface GroupRepository extends JpaRepository<Group, Long> {

	List<Group> findByChurchIdAndActiveTrueOrderByNameAsc(Long churchId);

	boolean existsByChurchIdAndLeaderIdAndActiveTrue(Long churchId, Long leaderUserId);

	List<Group> findByLeaderIdAndActiveTrue(Long leaderUserId);

	@Query("SELECT g FROM Group g LEFT JOIN FETCH g.leader WHERE g.id = :id")
	Optional<Group> findByIdWithLeader(@Param("id") Long id);

	@Query("SELECT COUNT(g) FROM Group g WHERE g.church.id = :churchId AND g.active = true")
	int countByChurchIdAndActiveTrue(@Param("churchId") Long churchId);
}