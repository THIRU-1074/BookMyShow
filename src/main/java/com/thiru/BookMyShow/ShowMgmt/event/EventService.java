package com.thiru.BookMyShow.ShowMgmt.event;

import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.security.access.AccessDeniedException;
import java.util.*;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Join;

import com.thiru.BookMyShow.ShowMgmt.AuthorizationPolicy;
import com.thiru.BookMyShow.userMgmt.*;
import com.thiru.BookMyShow.ShowMgmt.event.DTO.*;

@Service
@RequiredArgsConstructor
public class EventService implements AuthorizationPolicy<EventEntity, UserEntity> {
    private final EventRepository eventRepo;
    private final UserRepository userRepo;

    @Override
    public void canCreate(UserEntity ue) {
        if (ue.getRole().equals(Role.ADMIN))
            return;
        throw new AccessDeniedException("Only Admin can create...!");
    }

    @Override
    public void canUpdate(EventEntity ee, UserEntity ue) {
        if (!ue.getRole().equals(Role.ADMIN))
            throw new AccessDeniedException("Only Admin can update...!");
        if (ee.getAdmin().getUserId().equals(ue.getUserId()))
            return;
        throw new AccessDeniedException("You could update your Events only...!");
    }

    @Override
    public void canDelete(EventEntity ee, UserEntity ue) {
        if (!ue.getRole().equals(Role.ADMIN))
            throw new AccessDeniedException("Only Admin can delete...!");
        if (ee.getAdmin().getUserId().equals(ue.getUserId()))
            return;
        throw new AccessDeniedException("You could delete your Events only...!");
    }

    @Override
    public void canRead(EventEntity ee, UserEntity ue) {
        return;
    }

    public void create(CreateEvent event) {
        UserEntity admin = userRepo.findByUserName(event.getUserName())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "User not found: " + event.getUserName()));
        this.canCreate(admin);
        event.setEventType(event.getEventType());
        EventEntity eventEntity = EventEntity.builder()
                .eventType(event.getEventType())
                .eventName(event.getEventName())
                .admin(admin)
                .build();

        eventRepo.save(eventEntity);
    }

    public void update(UpdateEvent event) {
        UserEntity admin = userRepo.findByUserName(event.getUserName())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "User not found: " + event.getUserName()));
        EventEntity existing = eventRepo.findById(event.getEventId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Event not found: " + event.getEventId()));
        this.canUpdate(existing, admin);
        if (event.getEventType() != null)
            existing.setEventType(event.getEventType());
        if (event.getEventName() != null)
            existing.setEventName(event.getEventName());

        eventRepo.save(existing);
    }

    public void delete(DeleteEvent event) {
        UserEntity admin = userRepo.findByUserName(event.getUserName())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "User not found: " + event.getUserName()));
        EventEntity existing = eventRepo.findById(event.getEventId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Event not found: " + event.getEventId()));
        this.canDelete(existing, admin);
        eventRepo.delete(existing);
    }

    public static Specification<EventEntity> withFilters(ReadEvent r) {

        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (r.getEventId() != null) {
                predicates.add(cb.equal(
                        root.get("eventId"),
                        r.getEventId()));
            }

            if (r.getEventName() != null) {
                predicates.add(cb.like(
                        cb.lower(root.get("eventName")),
                        "%" + r.getEventName().toLowerCase() + "%"));
            }

            if (r.getEventType() != null) {
                predicates.add(cb.equal(
                        root.get("eventType"),
                        r.getEventType()));
            }

            if (r.getUserName() != null) {
                Join<EventEntity, UserEntity> adminJoin = root.join("admin");

                predicates.add(cb.equal(
                        adminJoin.get("userName"),
                        r.getUserName()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    public List<EventReadResponse> read(ReadEvent request) {

        this.canRead(null, null);

        List<EventEntity> events = eventRepo.findAll(EventService.withFilters(request));

        return events.stream()
                .map(this::toResponse)
                .toList();
    }

    private EventReadResponse toResponse(EventEntity entity) {
        return EventReadResponse.builder()
                .eventName(entity.getEventName())
                .eventId(entity.getEventId())
                .eventType(entity.getEventType())
                .build();
    }
}