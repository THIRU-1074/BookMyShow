package com.thiru.ticket_booking_service.entity;

import java.util.*;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "auditoriums")
@Getter 
@Setter
@NoArgsConstructor 
@AllArgsConstructor 
@Builder
public class AuditoriumEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long auditoriumId;

    private String auditoriumName;

    @ManyToOne
    @JoinColumn(name = "venue_id")
    private VenueEntity venue;

    @OneToMany(mappedBy = "auditorium")
    private List<SeatEntity> seats;
}

