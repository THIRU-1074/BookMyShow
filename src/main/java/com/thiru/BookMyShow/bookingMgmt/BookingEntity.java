package com.thiru.BookMyShow.bookingMgmt;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import com.thiru.BookMyShow.ShowMgmt.showSeat.ShowSeatEntity;
import com.thiru.BookMyShow.userMgmt.UserEntity;

@Entity
@Table(name = "bookings", indexes = {

        // user → bookings
        @Index(name = "idx_booking_user_id", columnList = "user_id"),

        // sorted booking history
        @Index(name = "idx_booking_user_time", columnList = "user_id, booking_time"),

        // showSeat → bookings
        @Index(name = "idx_booking_show_seat_id", columnList = "show_seat_id"),

        // status-based checks
        @Index(name = "idx_booking_show_seat_status", columnList = "show_seat_id, ticket_booking_status")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bookingId")
    private Long bookingId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "userId", nullable = false)
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "showSeatId", nullable = false)
    private ShowSeatEntity showSeat;

    @Column(name = "bookingTime", nullable = false)
    private LocalDateTime bookingTime;

    @Enumerated(EnumType.STRING)
    private TicketBookingStatus ticketBookingStatus;

}
