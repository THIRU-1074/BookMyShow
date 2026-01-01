package com.thiru.BookMyShow.searchBrowse.DTO;

import java.util.*;
import com.thiru.BookMyShow.ShowMgmt.event.DTO.EventReadResponse;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class BrowseEventResponse {
    private List<EventReadResponse> events;
}
