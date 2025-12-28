package com.thiru.ticket_booking_service.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

import com.thiru.ticket_booking_service.entity.enums.EventType;

@Entity
@Table(name = "events")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_id")
    private Long eventId;

    /**
     * Admin who created/manages this event
     * MANY events -> ONE admin (user)
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity admin;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false)
    private EventType eventType;

    @Column(name = "event_name", nullable = false)
    private String eventName;

    /**
     * ONE event -> MANY shows
     */
    @OneToMany(mappedBy = "event", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<ShowEntity> shows;
}

