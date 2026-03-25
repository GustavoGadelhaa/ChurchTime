package com.church.backend.config.messaging.dtoMessage;

import com.church.backend.attendance.entity.EventStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;


@AllArgsConstructor
@NoArgsConstructor
@Data
public class EventMessageDTO {
    Long id;
    String title;
    String location;
    Timestamp event_date;
    Long group_id;
}