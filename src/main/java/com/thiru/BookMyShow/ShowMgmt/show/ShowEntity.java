package com.thiru.BookMyShow.ShowMgmt.show;

import java.util.*;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

import com.thiru.BookMyShow.ShowMgmt.auditorium.AuditoriumEntity;
import com.thiru.BookMyShow.ShowMgmt.event.EventEntity;

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
    @JoinColumn(name = "eventId")
    private EventEntity event;

    private String showName;

    private LocalDateTime showDateTime;

    private Integer durationMinutes;

    @ElementCollection
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "showGenres", joinColumns = @JoinColumn(name = "showId"))
    @Column(name = "genre")
    private Set<Genre> genres;

    @ElementCollection
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "showLanguages", joinColumns = @JoinColumn(name = "showId"))
    @Column(name = "language")
    private Set<Language> languages;

    private Double rating;

    @ManyToOne
    @JoinColumn(name = "auditoriumId")
    private AuditoriumEntity auditorium;

    @Enumerated(EnumType.STRING)
    private ShowBookingStatus bookingStatus;

    private Long availableSeatCount;

}
