package com.church.backend.identity.repository;

import com.church.backend.identity.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

	Optional<User> findByEmailIgnoreCase(String email);

	@Query("""
			select distinct u from User u
			left join fetch u.group g
			left join fetch g.church c
			where lower(u.email) = lower(:email)
			""")
	Optional<User> findByEmailForSession(@Param("email") String email);

	List<User> findByGroupIdAndActiveTrueOrderByNameAsc(Long groupId);

	List<User> findByActiveTrueOrderByNameAsc();
}
