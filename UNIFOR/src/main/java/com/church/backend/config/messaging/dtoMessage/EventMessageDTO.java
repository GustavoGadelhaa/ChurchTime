package com.church.backend.config.messaging.dtoMessage;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;


@AllArgsConstructor
@NoArgsConstructor
@Data
public class EventMessageDTO {
	@NotNull
	Long id;
	@NotBlank
	String title;
	@NotBlank
	String location;
	@NotNull
	Timestamp event_date;
	@NotNull
	Long group_id;
}