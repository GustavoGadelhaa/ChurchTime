package com.church.backend.attendance.repository;

import com.church.backend.attendance.entity.Presence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PresenceRepository extends JpaRepository<Presence, Long> {

	@Query("select p from Presence p join fetch p.user where p.event.id = :eventId order by p.checkedInAt asc")
	List<Presence> findByEventIdOrderByCheckedInAtAsc(@Param("eventId") Long eventId);

	Optional<Presence> findByEventIdAndUserId(Long eventId, Long userId);

	boolean existsByEventIdAndUserId(Long eventId, Long userId);
}
