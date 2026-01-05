package com.thiru.BookMyShow.ShowMgmt.show;

import java.util.*;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

import com.thiru.BookMyShow.ShowMgmt.auditorium.AuditoriumEntity;
import com.thiru.BookMyShow.ShowMgmt.event.EventEntity;

@Entity
@Table(name = "show", indexes = {
                // date-based browsing
                @Index(name = "idx_show_datetime", columnList = "show_date_time"),

                // joins
                @Index(name = "idx_show_event_id", columnList = "event_id"),
                @Index(name = "idx_show_auditorium_id", columnList = "auditorium_id"),

                // composite indexes (performance boosters)
                @Index(name = "idx_show_event_datetime", columnList = "event_id, show_date_time"),
                @Index(name = "idx_show_auditorium_datetime", columnList = "auditorium_id, show_date_time")
})
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
        @CollectionTable(name = "showGenres", joinColumns = @JoinColumn(name = "showId"), indexes = {
                        @Index(name = "idx_show_genres_genre", columnList = "genre"),
                        @Index(name = "idx_show_genres_show_id", columnList = "show_id")
        })
        @Column(name = "genre")
        private Set<Genre> genres;

        @ElementCollection
        @Enumerated(EnumType.STRING)
        @CollectionTable(name = "showLanguages", joinColumns = @JoinColumn(name = "showId"), indexes = {
                        @Index(name = "idx_show_languages_language", columnList = "language"),
                        @Index(name = "idx_show_languages_show_id", columnList = "show_id")
        })
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
