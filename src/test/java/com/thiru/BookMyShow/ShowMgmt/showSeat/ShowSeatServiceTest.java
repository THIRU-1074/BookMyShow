package com.thiru.BookMyShow.ShowMgmt.showSeat;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;
import com.thiru.BookMyShow.ShowMgmt.auditorium.AuditoriumEntity;
import com.thiru.BookMyShow.ShowMgmt.seat.SeatEntity;
import com.thiru.BookMyShow.ShowMgmt.seat.SeatRepository;
import com.thiru.BookMyShow.ShowMgmt.seatCategory.SeatCategoryEntity;
import com.thiru.BookMyShow.ShowMgmt.seatCategory.SeatCategoryRepository;
import com.thiru.BookMyShow.ShowMgmt.show.ShowEntity;
import com.thiru.BookMyShow.ShowMgmt.show.ShowRepository;
import com.thiru.BookMyShow.ShowMgmt.showSeat.DTO.ReadShowSeat;
import com.thiru.BookMyShow.ShowMgmt.showSeat.DTO.ShowSeatReadResponse;
import com.thiru.BookMyShow.ShowMgmt.showSeat.DTO.UpdateShowSeat;
import com.thiru.BookMyShow.ShowMgmt.showSeat.DTO.UpdateShowSeats;
import com.thiru.BookMyShow.testutil.BookingTestDataFactory;
import com.thiru.BookMyShow.userMgmt.Role;
import com.thiru.BookMyShow.userMgmt.UserEntity;
import com.thiru.BookMyShow.userMgmt.UserRepository;

@ExtendWith(MockitoExtension.class)
class ShowSeatServiceTest {

        @Mock
        private ShowRepository showRepository;
        @Mock
        private SeatRepository seatRepository;
        @Mock
        private ShowSeatRepository showSeatRepository;
        @Mock
        private UserRepository userRepository;
        @Mock
        private SeatCategoryRepository seatCategoryRepository;

        private ShowSeatService showSeatService;

        @BeforeEach
        void setUp() {
                showSeatService = new ShowSeatService(
                                showRepository,
                                seatRepository,
                                showSeatRepository,
                                userRepository,
                                seatCategoryRepository);
        }

        @Test
        void updateAppliesCategoryAndStatusChanges() {
                UserEntity admin = BookingTestDataFactory.user(1L, "admin", Role.ADMIN);
                AuditoriumEntity auditorium = BookingTestDataFactory.auditorium(1L,
                                BookingTestDataFactory.venue(1L, "City"), admin);
                SeatEntity seat = BookingTestDataFactory.seat(10L, auditorium, "A1");
                SeatCategoryEntity initial = BookingTestDataFactory.seatCategory(1L, "REGULAR");
                SeatCategoryEntity vip = BookingTestDataFactory.seatCategory(2L, "VIP");
                ShowEntity show = BookingTestDataFactory.show(5L, auditorium,
                                BookingTestDataFactory.event(3L, "Event", admin), 20L);
                show.getEvent().setAdmin(admin);
                ShowSeatEntity showSeat = BookingTestDataFactory.showSeat(20L, show, seat, initial,
                                SeatAvailabilityStatus.AVAILABLE);

                when(userRepository.findByUserName("admin")).thenReturn(Optional.of(admin));
                when(showRepository.findById(5L)).thenReturn(Optional.of(show));
                when(showSeatRepository.findByShow_ShowIdAndSeat_SeatId(5L, 10L))
                                .thenReturn(Optional.of(showSeat));

                UpdateShowSeat update = new UpdateShowSeat();
                update.setSeatId(10L);
                update.setCategory(vip);
                update.setStatus(SeatAvailabilityStatus.LOCKED);

                UpdateShowSeats request = UpdateShowSeats.builder()
                                .userName("admin")
                                .showId(5L)
                                .seats(List.of(update))
                                .build();

                showSeatService.update(request);

                assertThat(showSeat.getShowSeatCategory()).isEqualTo(vip);
                assertThat(showSeat.getShowSeatAvailabilityStatus()).isEqualTo(SeatAvailabilityStatus.LOCKED);
        }

        @Test
        void readReturnsMappedResponses() {
                UserEntity admin = BookingTestDataFactory.user(1L, "admin", Role.ADMIN);
                AuditoriumEntity auditorium = BookingTestDataFactory.auditorium(1L,
                                BookingTestDataFactory.venue(1L, "City"), admin);
                SeatCategoryEntity category = BookingTestDataFactory.seatCategory(1L, "VIP");
                ShowEntity show = BookingTestDataFactory.show(1L, auditorium,
                                BookingTestDataFactory.event(2L, "Event", admin), 10L);
                show.getEvent().setAdmin(admin);
                SeatEntity seat = BookingTestDataFactory.seat(1L, auditorium, "A1");
                ShowSeatEntity showSeat = BookingTestDataFactory.showSeat(2L, show, seat, category,
                                SeatAvailabilityStatus.AVAILABLE);

                when(showSeatRepository.findAll(any(Specification.class)))
                                .thenReturn(List.of(showSeat));

                ReadShowSeat readRequest = new ReadShowSeat();
                readRequest.setShowId(1L);

                List<ShowSeatReadResponse> responses = showSeatService.read(readRequest);

                assertThat(responses)
                                .singleElement()
                                .extracting(ShowSeatReadResponse::getSeatNo)
                                .isEqualTo("A1");
        }
}
