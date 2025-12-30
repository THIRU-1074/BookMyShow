package com.thiru.ticket_booking_service.ShowMgmt.event;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

import com.thiru.ticket_booking_service.ShowMgmt.show.ShowEntity;
import com.thiru.ticket_booking_service.userMgmt.UserEntity;

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
    @Column(name = "eventId")
    private Long eventId;

    /**
     * Admin who created/manages this event
     * MANY events -> ONE admin (user)
     */

    @Enumerated(EnumType.STRING)
    @Column(name = "eventType", nullable = false)
    private EventType eventType;

    @Column(name = "eventName", nullable = false)
    private String eventName;

    @ManyToOne
    @JoinColumn(name = "userId")
    private UserEntity admin;

    @OneToMany(mappedBy = "event", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<ShowEntity> shows;
}
