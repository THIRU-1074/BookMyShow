package com.thiru.BookMyShow.bookingMgmt;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.thiru.BookMyShow.ShowMgmt.auditorium.AuditoriumEntity;
import com.thiru.BookMyShow.ShowMgmt.seat.SeatEntity;
import com.thiru.BookMyShow.ShowMgmt.seat.SeatService;
import com.thiru.BookMyShow.ShowMgmt.seatCategory.SeatCategoryEntity;
import com.thiru.BookMyShow.ShowMgmt.show.ShowBookingStatus;
import com.thiru.BookMyShow.ShowMgmt.show.ShowEntity;
import com.thiru.BookMyShow.ShowMgmt.showSeat.SeatAvailabilityStatus;
import com.thiru.BookMyShow.ShowMgmt.showSeat.ShowSeatEntity;
import com.thiru.BookMyShow.ShowMgmt.showSeat.ShowSeatRepository;
import com.thiru.BookMyShow.ShowMgmt.showSeatPricing.ShowSeatPricingEntity;
import com.thiru.BookMyShow.ShowMgmt.showSeatPricing.ShowSeatPricingRepository;
import com.thiru.BookMyShow.ShowMgmt.venue.VenueEntity;
import com.thiru.BookMyShow.ShowMgmt.venue.VenueService;
import com.thiru.BookMyShow.bookingMgmt.DTO.CreateBooking;
import com.thiru.BookMyShow.bookingMgmt.DTO.CreateBookings;
import com.thiru.BookMyShow.paymentGateWay.PaymentGateway;
import com.thiru.BookMyShow.testutil.BookingTestDataFactory;
import com.thiru.BookMyShow.userMgmt.Role;
import com.thiru.BookMyShow.userMgmt.UserEntity;
import com.thiru.BookMyShow.userMgmt.UserRepository;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private ShowSeatRepository showSeatRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ShowSeatPricingRepository showSeatPricingRepository;
    @Mock
    private VenueService venueService;
    @Mock
    private SeatService seatService;
    @Mock
    private PaymentGateway paymentGateway;
    @Mock
    private ShowSeatPricingRepository pricingRepository;
    @Mock
    private BookingEventPublisher bookingEventPublisher;

    private BookingService bookingService;

    @BeforeEach
    void setUp() {
        bookingService = new BookingService(
                bookingRepository,
                showSeatRepository,
                userRepository,
                showSeatPricingRepository,
                venueService,
                seatService,
                paymentGateway,
                pricingRepository,
                bookingEventPublisher);
    }

    @Test
    void prepareBookingLocksSeatAndPersistsBooking() {
        UserEntity user = BookingTestDataFactory.user(1L, "api-user", Role.USER);
        ShowEntity show = minimalShow(5L);
        SeatCategoryEntity category = BookingTestDataFactory.seatCategory(1L, "VIP");
        ShowSeatEntity showSeat = buildShowSeat(show, category, SeatAvailabilityStatus.AVAILABLE);

        CreateBooking request = new CreateBooking();
        request.setSeatId(showSeat.getShowSeatId());
        request.setShowId(show.getShowId());

        when(showSeatRepository.lockById(request.getSeatId())).thenReturn(Optional.of(showSeat));
        when(bookingRepository.save(any(BookingEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        BookingEntity saved = bookingService.prepareBooking(request, user);

        assertThat(showSeat.getShowSeatAvailabilityStatus()).isEqualTo(SeatAvailabilityStatus.LOCKED);
        assertThat(show.getAvailableSeatCount()).isEqualTo(4L);
        assertThat(saved.getTicketBookingStatus()).isEqualTo(TicketBookingStatus.PENDING_PAYMENT);
        assertThat(saved.getUser()).isEqualTo(user);
        verify(bookingRepository).save(any(BookingEntity.class));
    }

    @Test
    void calculateAmountAggregatesConfiguredPricing() {
        ShowEntity show = minimalShow(5L);
        SeatCategoryEntity gold = BookingTestDataFactory.seatCategory(1L, "GOLD");
        SeatCategoryEntity silver = BookingTestDataFactory.seatCategory(2L, "SILVER");
        ShowSeatEntity showSeatGold = buildShowSeat(show, gold, SeatAvailabilityStatus.AVAILABLE);
        ShowSeatEntity showSeatSilver = buildShowSeat(show, silver, SeatAvailabilityStatus.AVAILABLE);

        BookingEntity goldBooking = BookingTestDataFactory.booking(1L, null, showSeatGold, LocalDateTime.now(),
                TicketBookingStatus.PENDING_PAYMENT);
        BookingEntity silverBooking = BookingTestDataFactory.booking(2L, null, showSeatSilver, LocalDateTime.now(),
                TicketBookingStatus.PENDING_PAYMENT);

        when(pricingRepository.findByShowAndSeatCategory(show, gold))
                .thenReturn(Optional.of(BookingTestDataFactory.pricing(1L, show, gold, 250.0)));
        when(pricingRepository.findByShowAndSeatCategory(show, silver))
                .thenReturn(Optional.of(BookingTestDataFactory.pricing(2L, show, silver, 200.0)));

        Double total = bookingService.calculateAmount(List.of(goldBooking, silverBooking));

        assertThat(total).isEqualTo(450.0);
    }

    @Test
    void deleteBookingRestoresSeatCapacityAndDeletesRecord() {
        ShowEntity show = minimalShow(3L);
        SeatCategoryEntity category = BookingTestDataFactory.seatCategory(1L, "VIP");
        ShowSeatEntity showSeat = buildShowSeat(show, category, SeatAvailabilityStatus.LOCKED);
        UserEntity user = BookingTestDataFactory.user(2L, "john", Role.USER);
        BookingEntity booking = BookingTestDataFactory.booking(100L, user, showSeat, LocalDateTime.now(),
                TicketBookingStatus.PENDING_PAYMENT);

        when(bookingRepository.findById(100L)).thenReturn(Optional.of(booking));

        bookingService.deleteBooking(100L);

        assertThat(showSeat.getShowSeatAvailabilityStatus()).isEqualTo(SeatAvailabilityStatus.AVAILABLE);
        assertThat(show.getAvailableSeatCount()).isEqualTo(4L);
        verify(bookingRepository).delete(booking);
    }

    @Test
    void createBookingsChargesCustomerAndFinalizesTicket() {
        UserEntity user = BookingTestDataFactory.user(1L, "mobile-user", Role.USER);
        ShowEntity show = minimalShow(1L);
        SeatCategoryEntity category = BookingTestDataFactory.seatCategory(1L, "RECLINER");
        ShowSeatEntity seat = buildShowSeat(show, category, SeatAvailabilityStatus.AVAILABLE);
        CreateBooking createBooking = new CreateBooking();
        createBooking.setSeatId(seat.getShowSeatId());
        createBooking.setShowId(show.getShowId());
        CreateBookings request = new CreateBookings();
        request.setUserName(user.getUserName());
        request.setBookings(List.of(createBooking));

        ShowSeatPricingEntity pricing = BookingTestDataFactory.pricing(1L, show, category, 500.0);

        when(userRepository.findByUserName(user.getUserName())).thenReturn(Optional.of(user));
        when(showSeatRepository.lockById(seat.getShowSeatId())).thenReturn(Optional.of(seat));
        when(pricingRepository.findByShowAndSeatCategory(show, category)).thenReturn(Optional.of(pricing));
        when(paymentGateway.charge(user, 1, 500.0)).thenReturn(true);

        AtomicReference<BookingEntity> persisted = new AtomicReference<>();
        when(bookingRepository.save(any(BookingEntity.class))).thenAnswer(invocation -> {
            BookingEntity entity = invocation.getArgument(0);
            persisted.set(entity);
            return entity;
        });

        bookingService.createBookings(request);

        assertThat(seat.getShowSeatAvailabilityStatus()).isEqualTo(SeatAvailabilityStatus.BOOKED);
        assertThat(show.getBookingStatus()).isEqualTo(ShowBookingStatus.SOLD_OUT);
        assertThat(persisted.get().getTicketBookingStatus()).isEqualTo(TicketBookingStatus.CONFIRMED);
        verify(paymentGateway).charge(user, 1, 500.0);
    }

    private ShowSeatEntity buildShowSeat(ShowEntity show, SeatCategoryEntity category, SeatAvailabilityStatus status) {
        VenueEntity venue = BookingTestDataFactory.venue(1L, "City");
        AuditoriumEntity auditorium = BookingTestDataFactory.auditorium(1L, venue, null);
        SeatEntity seat = BookingTestDataFactory.seat(1L, auditorium, "A1");
        show.setAuditorium(auditorium);
        return BookingTestDataFactory.showSeat(1L, show, seat, category, status);
    }

    private ShowEntity minimalShow(long availableSeatCount) {
        ShowEntity show = BookingTestDataFactory.show(10L, null, null, availableSeatCount);
        show.setBookingStatus(ShowBookingStatus.OPEN);
        show.setAvailableSeatCount(availableSeatCount);
        return show;
    }
}
