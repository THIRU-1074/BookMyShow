package com.thiru.BookMyShow.testutil;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Set;

import com.thiru.BookMyShow.ShowMgmt.auditorium.AuditoriumEntity;
import com.thiru.BookMyShow.ShowMgmt.event.EventEntity;
import com.thiru.BookMyShow.ShowMgmt.event.EventType;
import com.thiru.BookMyShow.ShowMgmt.seat.SeatEntity;
import com.thiru.BookMyShow.ShowMgmt.seatCategory.SeatCategoryEntity;
import com.thiru.BookMyShow.ShowMgmt.show.Genre;
import com.thiru.BookMyShow.ShowMgmt.show.Language;
import com.thiru.BookMyShow.ShowMgmt.show.ShowBookingStatus;
import com.thiru.BookMyShow.ShowMgmt.show.ShowEntity;
import com.thiru.BookMyShow.ShowMgmt.showSeat.SeatAvailabilityStatus;
import com.thiru.BookMyShow.ShowMgmt.showSeat.ShowSeatEntity;
import com.thiru.BookMyShow.ShowMgmt.showSeatPricing.ShowSeatPricingEntity;
import com.thiru.BookMyShow.bookingMgmt.BookingEntity;
import com.thiru.BookMyShow.bookingMgmt.TicketBookingStatus;
import com.thiru.BookMyShow.userMgmt.Role;
import com.thiru.BookMyShow.userMgmt.UserEntity;
import com.thiru.BookMyShow.ShowMgmt.venue.VenueEntity;

/**
 * Factory helpers for building fully wired domain objects that tests can reuse
 * without repeating verbose entity setup logic.
 */
public final class BookingTestDataFactory {

    private BookingTestDataFactory() {
    }

    public static UserEntity user(Long userId, String userName, Role role) {
        return UserEntity.builder()
                .userId(userId)
                .userName(userName)
                .hashedPwd("hashed-password")
                .mailId(userName + "@bookmyshow.test")
                .phoneNumber("90000000" + (userId == null ? "0" : userId))
                .role(role)
                .build();
    }

    public static VenueEntity venue(Long venueId, String city) {
        return VenueEntity.builder()
                .venueId(venueId)
                .city(city)
                .pincode("560001")
                .addressLine1("Main Street")
                .addressLine2("Block A")
                .landmark("Central Park")
                .auditoriums(Collections.emptyList())
                .build();
    }

    public static AuditoriumEntity auditorium(Long auditoriumId, VenueEntity venue, UserEntity admin) {
        return AuditoriumEntity.builder()
                .auditoriumId(auditoriumId)
                .auditoriumName("Auditorium-" + (auditoriumId == null ? "X" : auditoriumId))
                .venue(venue)
                .admin(admin)
                .build();
    }

    public static EventEntity event(Long eventId, String eventName, UserEntity admin) {
        return EventEntity.builder()
                .eventId(eventId)
                .eventName(eventName)
                .eventType(EventType.MOVIE)
                .admin(admin)
                .shows(Collections.emptyList())
                .build();
    }

    public static SeatCategoryEntity seatCategory(Long id, String name) {
        return SeatCategoryEntity.builder()
                .id(id)
                .name(name)
                .description(name + " seats")
                .build();
    }

    public static SeatEntity seat(Long seatId, AuditoriumEntity auditorium, String seatNo) {
        return SeatEntity.builder()
                .seatId(seatId)
                .seatNo(seatNo)
                .row(1)
                .col(1)
                .stance(1)
                .auditorium(auditorium)
                .build();
    }

    public static ShowEntity show(Long showId, AuditoriumEntity auditorium, EventEntity event, long availableSeats) {
        return ShowEntity.builder()
                .showId(showId)
                .auditorium(auditorium)
                .event(event)
                .showName("Prime Time Show")
                .showDateTime(LocalDateTime.now().plusDays(1))
                .durationMinutes(120)
                .genres(Set.of(Genre.ACTION))
                .languages(Set.of(Language.ENGLISH))
                .bookingStatus(ShowBookingStatus.OPEN)
                .availableSeatCount(availableSeats)
                .rating(4.5)
                .build();
    }

    public static ShowSeatEntity showSeat(Long showSeatId, ShowEntity show, SeatEntity seat,
            SeatCategoryEntity category, SeatAvailabilityStatus status) {
        return ShowSeatEntity.builder()
                .showSeatId(showSeatId)
                .show(show)
                .seat(seat)
                .showSeatCategory(category)
                .showSeatAvailabilityStatus(status)
                .build();
    }

    public static BookingEntity booking(Long bookingId, UserEntity user, ShowSeatEntity showSeat,
            LocalDateTime bookingTime, TicketBookingStatus status) {
        return BookingEntity.builder()
                .bookingId(bookingId)
                .user(user)
                .showSeat(showSeat)
                .bookingTime(bookingTime)
                .ticketBookingStatus(status)
                .build();
    }

    public static ShowSeatPricingEntity pricing(Long id, ShowEntity show, SeatCategoryEntity category, double price) {
        return ShowSeatPricingEntity.builder()
                .showSeatPricingId(id)
                .show(show)
                .seatCategory(category)
                .price(price)
                .build();
    }
}
