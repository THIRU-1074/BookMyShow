package com.thiru.BookMyShow.ShowMgmt.event;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface EventRepository extends JpaRepository<EventEntity, Long> {

    List<EventEntity> findByEventType(EventType eventType);

    boolean existsByEventName(String eventName);

    EventEntity findByEventId(Long eventId);

    List<EventEntity> findByEventName(String eventName);

    List<EventEntity> findByEventNameAndEventType(String eventName, EventType eventType);

    List<EventEntity> findByAdmin_UserId(Long userId);
}
