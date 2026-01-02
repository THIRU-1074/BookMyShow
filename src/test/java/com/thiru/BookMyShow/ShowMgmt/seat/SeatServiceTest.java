package com.thiru.BookMyShow.ShowMgmt.seat;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.thiru.BookMyShow.ShowMgmt.auditorium.AuditoriumEntity;
import com.thiru.BookMyShow.ShowMgmt.auditorium.AuditoriumRepository;
import com.thiru.BookMyShow.ShowMgmt.seat.DTO.CreateSeat;
import com.thiru.BookMyShow.ShowMgmt.seat.DTO.CreateSeats;
import com.thiru.BookMyShow.ShowMgmt.seat.DTO.ReadSeat;
import com.thiru.BookMyShow.ShowMgmt.seat.DTO.ReadSeats;
import com.thiru.BookMyShow.ShowMgmt.seat.DTO.SeatReadResponse;
import com.thiru.BookMyShow.ShowMgmt.seat.DTO.UpdateSeat;
import com.thiru.BookMyShow.ShowMgmt.seat.DTO.UpdateSeats;
import com.thiru.BookMyShow.testutil.BookingTestDataFactory;

@ExtendWith(MockitoExtension.class)
class SeatServiceTest {

        @Mock
        private AuditoriumRepository auditoriumRepository;
        @Mock
        private SeatRepository seatRepository;

        private SeatService seatService;

        @BeforeEach
        void setUp() {
                seatService = new SeatService(auditoriumRepository, seatRepository);
        }

        @Test
        void createMapsIncomingSeatsAndSavesAll() {
                CreateSeat seat = new CreateSeat();
                seat.setSeatNo("A1");
                seat.setRow(1);
                seat.setCol(1);
                seat.setStance(1);

                CreateSeats request = new CreateSeats();
                request.setAuditoriumId(10L);
                request.setSeats(new CreateSeat[] { seat });

                AuditoriumEntity auditorium = BookingTestDataFactory.auditorium(10L,
                                BookingTestDataFactory.venue(1L, "City"), null);
                when(auditoriumRepository.findById(10L)).thenReturn(Optional.of(auditorium));

                seatService.create(request);

                ArgumentCaptor<List<SeatEntity>> captor = ArgumentCaptor.forClass(List.class);
                verify(seatRepository).saveAll(captor.capture());
                assertThat(captor.getValue()).hasSize(1);
                assertThat(captor.getValue().get(0).getSeatNo()).isEqualTo("A1");
        }

        @Test
        void updateChangesSeatNumbersWhenFound() {
                UpdateSeat updateSeat = new UpdateSeat();
                updateSeat.setSeatId(5L);
                updateSeat.setSeatNo("B2");

                UpdateSeats request = new UpdateSeats();
                request.setAuditoriumId(2L);
                request.setSeats(new UpdateSeat[] { updateSeat });

                AuditoriumEntity auditorium = BookingTestDataFactory.auditorium(2L,
                                BookingTestDataFactory.venue(1L, "City"), null);
                SeatEntity entity = SeatEntity.builder()
                                .seatId(5L)
                                .seatNo("A1")
                                .row(1)
                                .col(1)
                                .stance(1)
                                .auditorium(auditorium)
                                .build();

                when(seatRepository.findBySeatIdAndAuditorium_AuditoriumId(5L, 2L)).thenReturn(Optional.of(entity));

                seatService.update(request);

                assertThat(entity.getSeatNo()).isEqualTo("B2");
        }

        @Test
        void readWithSeatIdsMapsEntitiesToResponses() {
                AuditoriumEntity auditorium = BookingTestDataFactory.auditorium(2L,
                                BookingTestDataFactory.venue(1L, "City"), null);
                SeatEntity entity = SeatEntity.builder()
                                .seatId(5L)
                                .seatNo("A1")
                                .row(1)
                                .col(1)
                                .stance(1)
                                .auditorium(auditorium)
                                .build();

                when(seatRepository.findBySeatIdInAndAuditorium_AuditoriumId(List.of(5L), 2L))
                                .thenReturn(List.of(entity));

                List<SeatReadResponse> responses = seatService.read(ReadSeats.builder()
                                .auditoriumId(2L)
                                .seats(new ReadSeat[] { ReadSeat.builder().seatId(5L).build() })
                                .build());

                assertThat(responses)
                                .singleElement()
                                .extracting(SeatReadResponse::getSeatNo)
                                .isEqualTo("A1");
        }

        @Test
        void readThrowsWhenAuditoriumMissing() {
                assertThatThrownBy(() -> seatService.read(ReadSeats.builder().build()))
                                .isInstanceOf(IllegalArgumentException.class);
        }
}
