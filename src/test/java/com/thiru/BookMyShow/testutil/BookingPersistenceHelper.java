package com.thiru.BookMyShow.testutil;

import java.time.LocalDateTime;

import com.thiru.BookMyShow.ShowMgmt.auditorium.AuditoriumEntity;
import com.thiru.BookMyShow.ShowMgmt.event.EventEntity;
import com.thiru.BookMyShow.ShowMgmt.seat.SeatEntity;
import com.thiru.BookMyShow.ShowMgmt.seatCategory.SeatCategoryEntity;
import com.thiru.BookMyShow.ShowMgmt.show.ShowBookingStatus;
import com.thiru.BookMyShow.ShowMgmt.show.ShowEntity;
import com.thiru.BookMyShow.ShowMgmt.showSeat.SeatAvailabilityStatus;
import com.thiru.BookMyShow.ShowMgmt.showSeat.ShowSeatEntity;
import com.thiru.BookMyShow.bookingMgmt.BookingEntity;
import com.thiru.BookMyShow.bookingMgmt.TicketBookingStatus;
import com.thiru.BookMyShow.userMgmt.Role;
import com.thiru.BookMyShow.userMgmt.UserEntity;
import com.thiru.BookMyShow.ShowMgmt.venue.VenueEntity;

import jakarta.persistence.EntityManager;

/**
 * Persists a fully linked booking graph into the provided {@link EntityManager}
 * so JPA based tests can focus on assertions instead of boilerplate setup.
 */
public final class BookingPersistenceHelper {

    private BookingPersistenceHelper() {
    }

    public static BookingEntity persistBookingGraph(EntityManager entityManager) {
        UserEntity admin = BookingTestDataFactory.user(null, "admin-user", Role.ADMIN);
        entityManager.persist(admin);

        VenueEntity venue = BookingTestDataFactory.venue(null, "Bengaluru");
        entityManager.persist(venue);

        AuditoriumEntity auditorium = BookingTestDataFactory.auditorium(null, venue, admin);
        entityManager.persist(auditorium);

        EventEntity event = BookingTestDataFactory.event(null, "SciFi Mania", admin);
        entityManager.persist(event);

        ShowEntity show = BookingTestDataFactory.show(null, auditorium, event, 5L);
        show.setBookingStatus(ShowBookingStatus.OPEN);
        entityManager.persist(show);

        SeatCategoryEntity category = BookingTestDataFactory.seatCategory(null, "VIP");
        entityManager.persist(category);

        SeatEntity seat = BookingTestDataFactory.seat(null, auditorium, "A1");
        entityManager.persist(seat);

        ShowSeatEntity showSeat = BookingTestDataFactory.showSeat(null, show, seat, category,
                SeatAvailabilityStatus.AVAILABLE);
        entityManager.persist(showSeat);

        UserEntity customer = BookingTestDataFactory.user(null, "jane.doe", Role.USER);
        entityManager.persist(customer);

        BookingEntity booking = BookingTestDataFactory.booking(null, customer, showSeat, LocalDateTime.now(),
                TicketBookingStatus.PENDING_PAYMENT);
        entityManager.persist(booking);
        entityManager.flush();
        entityManager.clear();
        return booking;
    }
}
