package com.thiru.BookMyShow.ShowMgmt.seatCategory;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "seatCategory", uniqueConstraints = {
        @UniqueConstraint(columnNames = "name")
})
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SeatCategoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String name; // e.g. REGULAR, VIP, RECLINER

    @Column(length = 255)
    private String description; // short description

}
