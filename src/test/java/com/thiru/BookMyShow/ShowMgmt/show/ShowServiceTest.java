package com.thiru.BookMyShow.ShowMgmt.show;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.thiru.BookMyShow.ShowMgmt.auditorium.AuditoriumEntity;
import com.thiru.BookMyShow.ShowMgmt.auditorium.AuditoriumRepository;
import com.thiru.BookMyShow.ShowMgmt.event.EventEntity;
import com.thiru.BookMyShow.ShowMgmt.event.EventRepository;
import com.thiru.BookMyShow.ShowMgmt.seat.SeatRepository;
import com.thiru.BookMyShow.ShowMgmt.show.DTO.CreateShow;
import com.thiru.BookMyShow.ShowMgmt.show.DTO.ReadShow;
import com.thiru.BookMyShow.ShowMgmt.show.DTO.UpdateShow;
import com.thiru.BookMyShow.ShowMgmt.show.DTO.ShowReadResponse;
import com.thiru.BookMyShow.ShowMgmt.showSeat.ShowSeatService;
import com.thiru.BookMyShow.ShowMgmt.venue.VenueEntity;
import com.thiru.BookMyShow.exception.InvalidBookingStatusTransitionException;
import com.thiru.BookMyShow.testutil.BookingTestDataFactory;
import com.thiru.BookMyShow.userMgmt.Role;
import com.thiru.BookMyShow.userMgmt.UserEntity;
import com.thiru.BookMyShow.userMgmt.UserRepository;

@ExtendWith(MockitoExtension.class)
class ShowServiceTest {

    @Mock
    private ShowRepository showRepository;
    @Mock
    private EventRepository eventRepository;
    @Mock
    private AuditoriumRepository auditoriumRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ShowSeatService showSeatService;
    @Mock
    private SeatRepository seatRepository;

    private ShowService showService;

    @BeforeEach
    void setUp() {
        showService = new ShowService(
                showRepository,
                eventRepository,
                auditoriumRepository,
                userRepository,
                showSeatService,
                seatRepository);
    }

    @Test
    void createShowUsesSeatCapacityAndTriggersSeatCreation() {
        CreateShow request = new CreateShow();
        request.setEventId(1L);
        request.setAuditoriumId(2L);
        request.setShowName("Morning Show");
        request.setShowDateTime(LocalDateTime.now().plusDays(1));
        request.setDurationMinutes(120);
        request.setGenres(Set.of(Genre.ACTION));
        request.setLanguages(Set.of(Language.ENGLISH));
        request.setUserName("admin");

        UserEntity admin = BookingTestDataFactory.user(10L, "admin", Role.ADMIN);
        EventEntity event = BookingTestDataFactory.event(1L, "Premiere", admin);
        event.setAdmin(admin);
        AuditoriumEntity auditorium = BookingTestDataFactory.auditorium(2L,
                BookingTestDataFactory.venue(5L, "City"), admin);

        when(userRepository.findByUserName("admin")).thenReturn(Optional.of(admin));
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(auditoriumRepository.findById(2L)).thenReturn(Optional.of(auditorium));
        when(seatRepository.countByAuditorium(auditorium)).thenReturn(50L);
        when(showRepository.save(any(ShowEntity.class))).thenAnswer(invocation -> {
            ShowEntity entity = invocation.getArgument(0);
            entity.setShowId(99L);
            return entity;
        });

        showService.create(request);

        ArgumentCaptor<ShowEntity> captor = ArgumentCaptor.forClass(ShowEntity.class);
        verify(showRepository).save(captor.capture());
        assertThat(captor.getValue().getAvailableSeatCount()).isEqualTo(50L);
        verify(showSeatService).createShowSeats(99L);
    }

    @Test
    void updateReducesSeatCountAndMarksSoldOut() {
        UpdateShow request = new UpdateShow();
        request.setShowId(5L);
        request.setUserName("admin");
        request.setBookedSeatCount(10L);

        UserEntity admin = BookingTestDataFactory.user(1L, "admin", Role.ADMIN);
        VenueEntity venue = BookingTestDataFactory.venue(1L, "City");
        AuditoriumEntity auditorium = BookingTestDataFactory.auditorium(1L, venue, admin);
        EventEntity event = BookingTestDataFactory.event(2L, "Event", admin);
        event.setAdmin(admin);
        ShowEntity show = ShowEntity.builder()
                .showId(5L)
                .event(event)
                .auditorium(auditorium)
                .availableSeatCount(10L)
                .bookingStatus(ShowBookingStatus.OPEN)
                .build();

        when(userRepository.findByUserName("admin")).thenReturn(Optional.of(admin));
        when(showRepository.findById(5L)).thenReturn(Optional.of(show));

        showService.update(request);

        assertThat(show.getAvailableSeatCount()).isZero();
        assertThat(show.getBookingStatus()).isEqualTo(ShowBookingStatus.SOLD_OUT);
        verify(showRepository).save(show);
    }

    @Test
    void updateBookingStatusThrowsWhenShowCancelled() {
        ShowEntity show = new ShowEntity();
        show.setBookingStatus(ShowBookingStatus.CANCELLED);

        assertThatThrownBy(() -> showService.updateBookingStatus(show, ShowBookingStatus.PAUSED))
                .isInstanceOf(InvalidBookingStatusTransitionException.class);
    }

    @Test
    void readSingleShowReturnsProjection() {
        UserEntity admin = BookingTestDataFactory.user(1L, "admin", Role.ADMIN);
        VenueEntity venue = BookingTestDataFactory.venue(1L, "City");
        AuditoriumEntity auditorium = BookingTestDataFactory.auditorium(1L, venue, admin);
        EventEntity event = BookingTestDataFactory.event(2L, "Event", admin);
        event.setAdmin(admin);
        ShowEntity show = ShowEntity.builder()
                .showId(7L)
                .showName("Preview")
                .event(event)
                .auditorium(auditorium)
                .showDateTime(LocalDateTime.now())
                .durationMinutes(90)
                .availableSeatCount(10L)
                .genres(Set.of(Genre.ACTION))
                .languages(Set.of(Language.ENGLISH))
                .build();

        when(userRepository.findByUserName("admin")).thenReturn(Optional.of(admin));
        when(showRepository.findById(7L)).thenReturn(Optional.of(show));

        List<ShowReadResponse> result = showService.read(ReadShow.builder()
                .userName("admin")
                .showId(7L)
                .build());

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getShowId()).isEqualTo(7L);
    }

    @Test
    void readWithoutFiltersThrowsIllegalArgumentException() {
        UserEntity admin = BookingTestDataFactory.user(1L, "admin", Role.ADMIN);
        when(userRepository.findByUserName("admin")).thenReturn(Optional.of(admin));

        assertThatThrownBy(() -> showService.read(ReadShow.builder().userName("admin").build()))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
