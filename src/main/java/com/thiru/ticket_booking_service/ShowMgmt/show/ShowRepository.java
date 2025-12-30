package com.thiru.ticket_booking_service.ShowMgmt.show;

import java.util.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ShowRepository extends JpaRepository<ShowEntity, Long>, JpaSpecificationExecutor<ShowEntity> {
    List<ShowEntity> findByEvent_EventId(Long eventId);
}