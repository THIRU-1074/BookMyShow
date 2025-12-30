package com.thiru.ticket_booking_service.ShowMgmt.auditorium;

import java.util.*;

import com.thiru.ticket_booking_service.ShowMgmt.seat.SeatEntity;
import com.thiru.ticket_booking_service.ShowMgmt.venue.VenueEntity;
import com.thiru.ticket_booking_service.userMgmt.UserEntity;

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
    @JoinColumn(name = "venueId")
    private VenueEntity venue;

    @ManyToOne
    @JoinColumn(name = "userId")
    private UserEntity admin;

    @OneToMany(mappedBy = "auditorium")
    private List<SeatEntity> seats;
}
