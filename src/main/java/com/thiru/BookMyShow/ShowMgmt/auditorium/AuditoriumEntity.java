package com.thiru.BookMyShow.ShowMgmt.auditorium;

import java.util.*;

import com.thiru.BookMyShow.ShowMgmt.seat.SeatEntity;
import com.thiru.BookMyShow.ShowMgmt.venue.VenueEntity;
import com.thiru.BookMyShow.userMgmt.UserEntity;

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
}
