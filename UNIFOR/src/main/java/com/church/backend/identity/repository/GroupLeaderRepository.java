package com.church.backend.identity.repository;

import com.church.backend.identity.entity.GroupLeader;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GroupLeaderRepository extends JpaRepository<GroupLeader, Long> {

	List<GroupLeader> findByChurchId(Long churchId);

	List<GroupLeader> findByUserId(Long userId);

	List<GroupLeader> findByGroupId(Long groupId);

	Optional<GroupLeader> findByUserIdAndGroupId(Long userId, Long groupId);

	void deleteByUserIdAndGroupId(Long userId, Long groupId);

	boolean existsByUserIdAndGroupId(Long userId, Long groupId);
}