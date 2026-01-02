package com.thiru.BookMyShow.searchBrowse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.thiru.BookMyShow.ShowMgmt.auditorium.AuditoriumEntity;
import com.thiru.BookMyShow.ShowMgmt.event.EventEntity;
import com.thiru.BookMyShow.ShowMgmt.event.EventType;
import com.thiru.BookMyShow.ShowMgmt.show.Genre;
import com.thiru.BookMyShow.ShowMgmt.show.Language;
import com.thiru.BookMyShow.ShowMgmt.show.ShowEntity;
import com.thiru.BookMyShow.ShowMgmt.show.ShowRepository;
import com.thiru.BookMyShow.ShowMgmt.venue.VenueEntity;
import com.thiru.BookMyShow.exception.ResourceNotFoundException;
import com.thiru.BookMyShow.searchBrowse.DTO.BrowseEvent;
import com.thiru.BookMyShow.searchBrowse.DTO.BrowseEventResponse;
import com.thiru.BookMyShow.searchBrowse.DTO.ViewShowDetail;
import com.thiru.BookMyShow.searchBrowse.DTO.ViewShowDetailResponse;
import com.thiru.BookMyShow.searchBrowse.DTO.ViewShowGroup;
import com.thiru.BookMyShow.searchBrowse.DTO.ViewShowGroupResponse;
import com.thiru.BookMyShow.testutil.BookingTestDataFactory;

@ExtendWith(MockitoExtension.class)
class SearchBrowseServiceTest {

    @Mock
    private SearchBrowseRepository searchBrowseRepository;
    @Mock
    private ShowRepository showRepository;

    private SearchBrowseService service;

    @BeforeEach
    void setUp() {
        service = new SearchBrowseService(searchBrowseRepository, showRepository);
    }

    @Test
    void viewShowReturnsProjectionForExistingShow() {
        ShowEntity show = sampleShow();
        when(showRepository.findByShowId(101L)).thenReturn(Optional.of(show));

        ViewShowDetailResponse response = service.viewShow(ViewShowDetail.builder().showId(101L).build());

        assertThat(response.getShow().getShowId()).isEqualTo(101L);
        assertThat(response.getShow().getEventId()).isEqualTo(show.getEvent().getEventId());
    }

    @Test
    void viewShowThrowsWhenShowMissing() {
        when(showRepository.findByShowId(9L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.viewShow(ViewShowDetail.builder().showId(9L).build()))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void viewShowGroupBuildsDateRangeAndMapsResults() {
        ShowEntity show = sampleShow();
        LocalDate date = LocalDate.of(2026, 1, 12);
        when(showRepository.findShowsForVenueAndDateRange(any(), any(), any()))
                .thenReturn(List.of(show));

        ViewShowGroupResponse response = service.viewShowGroup(
                ViewShowGroup.builder()
                        .date(date)
                        .venueId(55L)
                        .build());

        ArgumentCaptor<LocalDateTime> startCaptor = ArgumentCaptor.forClass(LocalDateTime.class);
        ArgumentCaptor<LocalDateTime> endCaptor = ArgumentCaptor.forClass(LocalDateTime.class);
        verify(showRepository).findShowsForVenueAndDateRange(
                org.mockito.ArgumentMatchers.eq(55L),
                startCaptor.capture(),
                endCaptor.capture());

        assertThat(startCaptor.getValue()).isEqualTo(date.atStartOfDay());
        assertThat(endCaptor.getValue()).isEqualTo(date.plusDays(1).atStartOfDay());
        assertThat(response.getShows()).hasSize(1);
        assertThat(response.getShows().get(0).getShow().getShowId()).isEqualTo(101L);
    }

    @Test
    void browseFiltersEventsAndMapsToReadDto() {
        EventEntity match = BookingTestDataFactory.event(20L, "Music Fest", null);
        match.setEventType(EventType.CONCERT);
        when(searchBrowseRepository.browseEvents(
                org.mockito.ArgumentMatchers.eq("Bangalore"),
                org.mockito.ArgumentMatchers.eq(Genre.ACTION),
                org.mockito.ArgumentMatchers.eq(Language.ENGLISH),
                any(LocalDateTime.class),
                any(LocalDateTime.class)))
                .thenReturn(List.of(match));

        LocalDate date = LocalDate.of(2026, 1, 5);
        BrowseEvent request = BrowseEvent.builder()
                .city("Bangalore")
                .date(date)
                .genre(Genre.ACTION)
                .language(Language.ENGLISH)
                .build();

        BrowseEventResponse response = service.browse(request);

        ArgumentCaptor<LocalDateTime> startCaptor = ArgumentCaptor.forClass(LocalDateTime.class);
        ArgumentCaptor<LocalDateTime> endCaptor = ArgumentCaptor.forClass(LocalDateTime.class);
        verify(searchBrowseRepository).browseEvents(
                org.mockito.ArgumentMatchers.eq("Bangalore"),
                org.mockito.ArgumentMatchers.eq(Genre.ACTION),
                org.mockito.ArgumentMatchers.eq(Language.ENGLISH),
                startCaptor.capture(),
                endCaptor.capture());

        assertThat(startCaptor.getValue()).isEqualTo(date.atStartOfDay());
        assertThat(endCaptor.getValue()).isEqualTo(date.plusDays(1).atStartOfDay());
        assertThat(response.getEvents()).hasSize(1);
        assertThat(response.getEvents().get(0).getEventId()).isEqualTo(20L);
    }

    private ShowEntity sampleShow() {
        EventEntity event = BookingTestDataFactory.event(10L, "Sample Event", null);
        VenueEntity venue = BookingTestDataFactory.venue(1L, "Bengaluru");
        AuditoriumEntity auditorium = BookingTestDataFactory.auditorium(1L, venue, null);
        ShowEntity show = BookingTestDataFactory.show(101L, auditorium, event, 20L);
        show.setEvent(event);
        return show;
    }
}
