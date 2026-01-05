package com.thiru.BookMyShow.ShowMgmt.showSeatPricing;

import lombok.*;
import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;

import com.thiru.BookMyShow.ShowMgmt.seatCategory.*;
import com.thiru.BookMyShow.ShowMgmt.show.*;

@Entity
@Table(name = "show_seat_pricing", uniqueConstraints = {
                @UniqueConstraint(columnNames = { "show_id", "seat_category_id" }) }, indexes = {
                                @Index(name = "idx_show_seat_pricing_show_id", columnList = "show_id")
                })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShowSeatPricingEntity {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long showSeatPricingId;

        // Many prices belong to one show
        @ManyToOne(fetch = FetchType.LAZY, optional = false)
        @JoinColumn(name = "showId", nullable = false)
        private ShowEntity show;

        // Many prices belong to one seat category
        @ManyToOne(fetch = FetchType.LAZY, optional = false)
        @JoinColumn(name = "seatCategoryId", nullable = false)
        private SeatCategoryEntity seatCategory;

        @Positive
        @Column(nullable = false)
        private Double price;
}
