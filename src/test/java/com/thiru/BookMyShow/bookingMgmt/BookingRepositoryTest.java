package com.thiru.BookMyShow.bookingMgmt;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import com.thiru.BookMyShow.ShowMgmt.auditorium.AuditoriumEntity;
import com.thiru.BookMyShow.ShowMgmt.event.EventEntity;
import com.thiru.BookMyShow.ShowMgmt.seat.SeatEntity;
import com.thiru.BookMyShow.ShowMgmt.seatCategory.SeatCategoryEntity;
import com.thiru.BookMyShow.ShowMgmt.show.ShowEntity;
import com.thiru.BookMyShow.ShowMgmt.showSeat.SeatAvailabilityStatus;
import com.thiru.BookMyShow.ShowMgmt.showSeat.ShowSeatEntity;
import com.thiru.BookMyShow.testutil.BookingTestDataFactory;
import com.thiru.BookMyShow.userMgmt.Role;
import com.thiru.BookMyShow.userMgmt.UserEntity;
import com.thiru.BookMyShow.ShowMgmt.venue.VenueEntity;

@DataJpaTest
class BookingRepositoryTest {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void findByUserIdReturnsOnlyUsersBookings() {
        BookingEntity booking = persistBookingGraph("primary-user");
        persistBookingGraph("another-user");

        List<BookingEntity> results = bookingRepository.findByUser_UserId(booking.getUser().getUserId());

        assertThat(results)
                .singleElement()
                .extracting(b -> b.getUser().getUserName())
                .isEqualTo("primary-user");
    }

    private BookingEntity persistBookingGraph(String userName) {
        UserEntity admin = BookingTestDataFactory.user(null, "admin-" + userName, Role.ADMIN);
        entityManager.persist(admin);

        VenueEntity venue = BookingTestDataFactory.venue(null, "Bengaluru");
        entityManager.persist(venue);

        AuditoriumEntity auditorium = BookingTestDataFactory.auditorium(null, venue, admin);
        entityManager.persist(auditorium);

        EventEntity event = BookingTestDataFactory.event(null, "Event-" + userName, admin);
        entityManager.persist(event);

        ShowEntity show = BookingTestDataFactory.show(null, auditorium, event, 10L);
        entityManager.persist(show);

        SeatCategoryEntity category = BookingTestDataFactory.seatCategory(null, "VIP");
        entityManager.persist(category);

        SeatEntity seat = BookingTestDataFactory.seat(null, auditorium, "A1");
        entityManager.persist(seat);

        ShowSeatEntity showSeat = BookingTestDataFactory.showSeat(null, show, seat, category,
                SeatAvailabilityStatus.AVAILABLE);
        entityManager.persist(showSeat);

        UserEntity customer = BookingTestDataFactory.user(null, userName, Role.USER);
        entityManager.persist(customer);

        BookingEntity booking = BookingTestDataFactory.booking(null, customer, showSeat, LocalDateTime.now(),
                TicketBookingStatus.PENDING_PAYMENT);
        entityManager.persist(booking);
        entityManager.flush();
        return booking;
    }
}
