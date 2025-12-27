package com.thiru.ticket_booking_service.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "seats",
       uniqueConstraints = @UniqueConstraint(columnNames = {"auditorium_id", "seat_no"}))
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class SeatEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long seatId;

    @Column(name = "seat_no")
    private String seatNo; // A1, A2, B3 etc

    @Enumerated(EnumType.STRING)
    private SeatCategory category;

    @ManyToOne
    @JoinColumn(name = "auditorium_id")
    private AuditoriumEntity auditorium;
}

