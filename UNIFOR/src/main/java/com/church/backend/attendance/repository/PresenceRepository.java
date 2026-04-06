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

	@Query(value = """
			SELECT COUNT(*)
			FROM presences p
			JOIN events e ON p.event_id = e.id
			JOIN groups g ON e.group_id = g.id
			WHERE g.church_id = :churchId
			  AND p.checked_in_at >= CURRENT_DATE
			""", nativeQuery = true)
	int countTodayCheckinsByChurchId(@Param("churchId") Long churchId);
}
