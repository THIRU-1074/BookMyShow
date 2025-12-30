package com.thiru.ticket_booking_service.ShowMgmt.seatCategory;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "seat_category", uniqueConstraints = {
        @UniqueConstraint(columnNames = "name")
})
@Setter
@Getter
@Builder
public class SeatCategoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String name; // e.g. REGULAR, VIP, RECLINER

    @Column(length = 255)
    private String description; // short description

    // getters & setters
}
