package com.thiru.BookMyShow.ShowMgmt.venue;

import java.util.*;

import com.thiru.BookMyShow.ShowMgmt.auditorium.AuditoriumEntity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "venues")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VenueEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long venueId;

    private String city;
    private String pincode;
    private String addressLine1;
    private String addressLine2;
    private String landmark;

    @OneToMany(mappedBy = "venue")
    private List<AuditoriumEntity> auditoriums;
}
