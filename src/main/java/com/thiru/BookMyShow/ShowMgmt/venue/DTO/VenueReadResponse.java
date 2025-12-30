package com.thiru.BookMyShow.ShowMgmt.venue.DTO;

import lombok.*;

@Getter
@Setter
@Builder
public class VenueReadResponse {
    private Long venueId;

    private String city;
    private String pincode;
    private String addressLine1;
    private String addressLine2;
    private String landmark;
}
