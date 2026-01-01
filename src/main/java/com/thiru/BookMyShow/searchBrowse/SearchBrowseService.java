package com.thiru.BookMyShow.searchBrowse;

import lombok.*;
import org.springframework.stereotype.Service;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDate;
import com.thiru.BookMyShow.ShowMgmt.event.*;
import com.thiru.BookMyShow.ShowMgmt.event.DTO.*;
import com.thiru.BookMyShow.ShowMgmt.show.*;
import com.thiru.BookMyShow.ShowMgmt.show.DTO.*;
import com.thiru.BookMyShow.exception.ResourceNotFoundException;
import com.thiru.BookMyShow.searchBrowse.DTO.*;

@Service
@RequiredArgsConstructor
public class SearchBrowseService {

        private final SearchBrowseRepository searchBrowseRepo;
        private final ShowRepository showRepo;

        public ViewShowDetailResponse viewShow(ViewShowDetail request) {
                ShowEntity show = showRepo.findByShowId(request.getShowId())
                                .orElseThrow(() -> new ResourceNotFoundException("Invalid show id"));
                return toViewShowDetailResponse(show);
        }

        public ViewShowGroupResponse viewShowGroup(ViewShowGroup request) {

                // 1. Convert LocalDate → LocalDateTime range
                LocalDate date = request.getDate();

                LocalDateTime startDateTime = date.atStartOfDay();
                LocalDateTime endDateTime = date.plusDays(1).atStartOfDay();

                // 2. Fetch shows (single query)
                List<ShowEntity> shows = showRepo.findShowsForVenueAndDateRange(
                                request.getVenueId(),
                                startDateTime,
                                endDateTime);

                // 3. Map entities → DTOs
                List<ViewShowDetailResponse> responses = shows.stream()
                                .map(this::toViewShowDetailResponse)
                                .toList();

                // 4. Build response
                return ViewShowGroupResponse.builder()
                                .shows(responses)
                                .build();
        }

        private ViewShowDetailResponse toViewShowDetailResponse(ShowEntity show) {

                ShowReadResponse showRead = ShowReadResponse.builder()
                                .showId(show.getShowId())
                                .showDateTime(show.getShowDateTime())
                                .eventId(show.getEvent().getEventId())
                                .build();

                return ViewShowDetailResponse.builder()
                                .show(showRead)
                                .build();
        }

        public BrowseEventResponse browse(BrowseEvent request) {

                LocalDateTime start = null;
                LocalDateTime end = null;

                if (request.getDate() != null) {
                        start = request.getDate().atStartOfDay();
                        end = request.getDate().plusDays(1).atStartOfDay();
                }

                List<EventEntity> events = searchBrowseRepo.browseEvents(
                                request.getCity(),
                                request.getGenre(),
                                request.getLanguage(),
                                start,
                                end);

                List<EventReadResponse> responses = events.stream()
                                .map(entity -> EventReadResponse.builder()
                                                .eventName(entity.getEventName())
                                                .eventId(entity.getEventId())
                                                .eventType(entity.getEventType())
                                                .build())
                                .toList();

                return BrowseEventResponse.builder()
                                .events(responses)
                                .build();
        }
}
