package com.thiru.BookMyShow.searchBrowse.DTO;

import java.time.LocalDate;

import com.thiru.BookMyShow.ShowMgmt.show.*;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class BrowseEvent {
    private String city;
    private LocalDate date;
    private Genre genre;
    private Language language;
}
