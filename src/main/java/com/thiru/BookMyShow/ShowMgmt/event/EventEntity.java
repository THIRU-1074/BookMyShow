package com.thiru.BookMyShow.ShowMgmt.event;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

import com.thiru.BookMyShow.ShowMgmt.show.ShowEntity;
import com.thiru.BookMyShow.userMgmt.UserEntity;

@Entity
@Table(name = "event", indexes = {
        @Index(name = "idx_event_type", columnList = "event_type")
})
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
