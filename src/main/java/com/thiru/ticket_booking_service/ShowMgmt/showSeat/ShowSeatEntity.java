package com.thiru.ticket_booking_service.ShowMgmt.showSeat;

import com.thiru.ticket_booking_service.ShowMgmt.seat.SeatEntity;
import com.thiru.ticket_booking_service.ShowMgmt.seatCategory.SeatCategoryEntity;
import com.thiru.ticket_booking_service.ShowMgmt.show.ShowEntity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "show_seats", uniqueConstraints = @UniqueConstraint(columnNames = { "show_id", "seat_id" }))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShowSeatEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long showSeatId;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "showId", nullable = false)
    private ShowEntity show;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "seatId", nullable = false)
    private SeatEntity seat;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SeatCategoryEntity category;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SeatAvailabilityStatus status;
}
