package com.church.backend.attendance.entity;

import com.church.backend.identity.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(
		name = "presences",
		uniqueConstraints = @UniqueConstraint(name = "uk_presences_user_event", columnNames = { "user_id", "event_id" })
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Presence {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "event_id", nullable = false)
	private Event event;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@Column(name = "checked_in_at", nullable = false)
	private Instant checkedInAt;

	@PrePersist
	void prePersist() {
		if (checkedInAt == null) {
			checkedInAt = Instant.now();
		}
	}
}
