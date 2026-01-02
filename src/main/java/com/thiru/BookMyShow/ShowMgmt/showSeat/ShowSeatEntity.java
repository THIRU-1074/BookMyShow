package com.thiru.BookMyShow.ShowMgmt.showSeat;

import com.thiru.BookMyShow.ShowMgmt.seat.SeatEntity;
import com.thiru.BookMyShow.ShowMgmt.seatCategory.SeatCategoryEntity;
import com.thiru.BookMyShow.ShowMgmt.show.ShowEntity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "show_seat", uniqueConstraints = @UniqueConstraint(columnNames = { "show_id", "seat_id" }), indexes = {

        // show → seats lookup
        @Index(name = "idx_show_seat_show_id", columnList = "show_id"),

        // seat locking
        @Index(name = "idx_show_seat_show_seat", columnList = "show_id, seat_id"),

        // availability filtering
        @Index(name = "idx_show_seat_show_status", columnList = "show_id, show_seat_availability_status"),

        // category grouping
        @Index(name = "idx_show_seat_show_category", columnList = "show_id, category_id")
})
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
    private SeatCategoryEntity showSeatCategory;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SeatAvailabilityStatus showSeatAvailabilityStatus;
}
