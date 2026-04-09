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

	@Query("SELECT COUNT(u) FROM User u WHERE u.group.church.id = :churchId AND u.active = true")
	int countByChurchIdAndActiveTrue(@Param("churchId") Long churchId);

	@Query("SELECT COUNT(u) FROM User u WHERE u.group.id = :groupId AND u.active = true")
	int countByGroupIdAndActiveTrue(@Param("groupId") Long groupId);

	@Query("SELECT u.group.church.id FROM User u WHERE u.id = :userId AND u.group IS NOT NULL AND u.group.church IS NOT NULL")
	Long findUserChurchId(@Param("userId") Long userId);

	@Query("SELECT c.id FROM Church c WHERE c.active = true ORDER BY c.id LIMIT 1")
	Long findFirstActiveChurchId();

	@Query("SELECT g.name FROM User u JOIN u.group g WHERE u.email = :email")
	String findGroupNameByEmail(@Param("email") String email);
}
