package com.thiru.ticket_booking_service.repository;

import java.util.*;
import com.thiru.ticket_booking_service.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShowRepository extends JpaRepository<ShowEntity, Long> {
    List<ShowEntity> findByEvent_EventId(Long eventId);
}