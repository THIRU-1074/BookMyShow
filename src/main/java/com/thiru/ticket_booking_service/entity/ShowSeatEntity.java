package com.thiru.ticket_booking_service.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "show_seats",
       uniqueConstraints = @UniqueConstraint(columnNames = {"show_id", "seat_id"}))
@Getter 
@Setter
@NoArgsConstructor 
@AllArgsConstructor 
@Builder
public class ShowSeatEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long showSeatId;

    @ManyToOne
    @JoinColumn(name = "show_id")
    private ShowEntity show;

    @ManyToOne
    @JoinColumn(name = "seat_id")
    private SeatEntity seat;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity bookedBy; // null = available
}

