package com.church.backend.attendance.repository;

import com.church.backend.attendance.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long> {

	List<Event> findByGroupIdOrderByEventDateDesc(Long groupId);

	@Query("select e from Event e join fetch e.group g left join fetch g.leader where e.id = :id")
	Optional<Event> findByIdWithGroup(@Param("id") Long id);
}
