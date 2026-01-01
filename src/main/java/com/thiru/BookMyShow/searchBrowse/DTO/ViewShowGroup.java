package com.thiru.BookMyShow.searchBrowse.DTO;

import java.time.LocalDate;
import lombok.*;

@Getter
@Setter
@Builder
public class ViewShowGroup {
    private LocalDate date;
    private Long venueId;
}
