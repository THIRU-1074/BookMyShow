package com.thiru.ticket_booking_service.repository;

import com.thiru.ticket_booking_service.entity.*;
import com.thiru.ticket_booking_service.entity.enums.EventType;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventRepository extends JpaRepository<EventEntity, Long> {

    List<EventEntity> findByAdmin_UserId(Long adminUserId);

    List<EventEntity> findByEventType(EventType eventType);

    boolean existsByEventName(String eventName);
}

