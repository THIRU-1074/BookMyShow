package com.thiru.BookMyShow.bookingMgmt;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

import com.thiru.BookMyShow.ShowMgmt.showSeat.ShowSeatEntity;
import com.thiru.BookMyShow.userMgmt.UserEntity;

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

}
