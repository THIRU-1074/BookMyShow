package com.thiru.ticket_booking_service.entity;

import java.util.*;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "shows")
@Getter 
@Setter
@NoArgsConstructor 
@AllArgsConstructor 
@Builder
public class ShowEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long showId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id")
    private EventEntity event;

    private String showName;

    private LocalDateTime showTime;

    private Integer durationMinutes;

    @ElementCollection
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "show_genres", joinColumns = @JoinColumn(name = "show_id"))
    @Column(name = "genre")
    private Set<Genre> genres;

    @ElementCollection
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "show_languages", joinColumns = @JoinColumn(name = "show_id"))
    @Column(name = "language")
    private Set<Language> languages;

    private Double rating;

    @ManyToOne
    @JoinColumn(name = "auditorium_id")
    private AuditoriumEntity auditorium;
}

