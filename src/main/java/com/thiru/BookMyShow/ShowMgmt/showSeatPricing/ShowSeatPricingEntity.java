package com.thiru.BookMyShow.ShowMgmt.showSeatPricing;

import lombok.*;
import jakarta.persistence.*;

import com.thiru.BookMyShow.ShowMgmt.seatCategory.*;
import com.thiru.BookMyShow.ShowMgmt.show.*;

@Entity
@Table(name = "show_seat_pricing", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "show_id", "seat_category_id" })
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShowSeatPricingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Many prices belong to one show
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "show_id", nullable = false)
    private ShowEntity show;

    // Many prices belong to one seat category
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "seat_category_id", nullable = false)
    private SeatCategoryEntity seatCategory;

    @Column(nullable = false)
    private Double price;
}
