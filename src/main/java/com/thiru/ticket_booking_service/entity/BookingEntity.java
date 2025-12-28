package com.thiru.ticket_booking_service.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

import com.thiru.ticket_booking_service.entity.enums.SeatCategory;

@Entity
@Table(name = "bookings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "booking_id")
    private Long bookingId;

    /**
     * MANY bookings -> ONE user
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Column(name = "event_id", nullable = false)
    private Long eventId;

    @Column(name = "show_id", nullable = false)
    private Long showId;

    @Column(name = "booking_time", nullable = false)
    private LocalDateTime bookingTime;

    @Column(name = "seat_no", nullable = false)
    private String seatNo;

    @Enumerated(EnumType.STRING)
    @Column(name = "seat_category", nullable = false)
    private SeatCategory seatCategory;
}

/* 
EXAMPLE USAGE

BookingEntity booking = BookingEntity.builder()
        .user(userEntity)
        .eventId(eventId)
        .showId(showId)
        .seatNo("A12")
        .seatCategory(SeatCategory.PREMIUM)
        .bookingTime(LocalDateTime.now())
        .build();

bookingRepository.save(booking);

*/