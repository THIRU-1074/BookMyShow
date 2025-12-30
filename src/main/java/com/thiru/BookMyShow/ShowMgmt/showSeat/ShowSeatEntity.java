package com.thiru.BookMyShow.ShowMgmt.showSeat;

import com.thiru.BookMyShow.ShowMgmt.seat.SeatEntity;
import com.thiru.BookMyShow.ShowMgmt.seatCategory.SeatCategoryEntity;
import com.thiru.BookMyShow.ShowMgmt.show.ShowEntity;

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

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "categoryId", nullable = false)
    private SeatCategoryEntity category;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SeatAvailabilityStatus status;
}
