package com.church.backend.attendance.repository;

import com.church.backend.attendance.entity.Event;
import com.church.backend.attendance.entity.EventStatus;
import com.church.backend.config.messaging.dtoMessage.EventMessageDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long>  {

	List<Event> findByGroupIdOrderByEventDateDesc(Long groupId);

	@Query("select e from Event e join fetch e.group g left join fetch g.leader where e.id = :id")
	Optional<Event> findByIdWithGroup(@Param("id") Long id);

	@Query(value = """
         SELECT e.id ,e.title, e.location, e.event_date, e.group_id
         FROM events e
         WHERE e.reminded = false
           AND e.event_date > NOW()
           AND e.event_date <= NOW() + INTERVAL '6 hours'
         ORDER BY e.event_date ASC
         """, nativeQuery = true)
	List<EventMessageDTO> findUpcomingWithinSixHours();

	@Modifying
	@Query(value = """
         UPDATE events
         SET reminded = true
         WHERE id = :id
         """, nativeQuery = true)
	void markAsReminded(@Param("id") Long id);

	@Query("SELECT COUNT(e) FROM Event e WHERE e.group.church.id = :churchId AND e.status = :status")
	int countByChurchIdAndStatus(@Param("churchId") Long churchId, @Param("status") EventStatus status);

	@Query("SELECT COUNT(e) FROM Event e WHERE e.group.id = :groupId AND e.status = :status")
	int countByGroupIdAndStatus(@Param("groupId") Long groupId, @Param("status") EventStatus status);
}
