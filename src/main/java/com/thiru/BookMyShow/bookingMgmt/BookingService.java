package com.thiru.BookMyShow.bookingMgmt;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.data.jpa.domain.Specification;
import java.util.*;
import jakarta.persistence.criteria.Predicate;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

import com.thiru.BookMyShow.exception.*;
import com.thiru.BookMyShow.ShowMgmt.showSeat.*;
import com.thiru.BookMyShow.ShowMgmt.show.*;
import com.thiru.BookMyShow.bookingMgmt.DTO.*;
import com.thiru.BookMyShow.userMgmt.*;
import com.thiru.BookMyShow.ShowMgmt.event.*;
import com.thiru.BookMyShow.ShowMgmt.auditorium.*;
import com.thiru.BookMyShow.ShowMgmt.venue.*;
import com.thiru.BookMyShow.ShowMgmt.venue.DTO.*;
import com.thiru.BookMyShow.ShowMgmt.seat.*;
import com.thiru.BookMyShow.ShowMgmt.seat.DTO.*;
import com.thiru.BookMyShow.ShowMgmt.showSeatPricing.*;

@Service
@RequiredArgsConstructor
@Transactional
public class BookingService {
        private final BookingRepository bookingRepo;
        private final ShowSeatRepository showSeatRepo;
        private final UserRepository userRepo;
        private final ShowSeatPricingRepository showSeatPricingRepo;
        private final VenueService venueService;
        private final SeatService seatService;

        public void createBookings(CreateBookings req) {
                UserEntity user = userRepo.findByName(req.getUserName())
                                .orElseThrow(() -> new ResponseStatusException(
                                                HttpStatus.NOT_FOUND,
                                                "User not found: " + req.getUserName()));
                if (user.getRole().equals(Role.ADMIN))
                        throw new AccessDeniedException("Only Users can book..!");
                for (CreateBooking cb : req.getBookings())
                        createBooking(cb, user);
        }

        public void createBooking(CreateBooking req, UserEntity user) {

                // 1️⃣ DB lock seat
                ShowSeatEntity seat = showSeatRepo.lockById(req.getSeatId())
                                .orElseThrow(() -> new EntityNotFoundException("Seat not found"));

                // 2️⃣ Business lock validation
                if (seat.getStatus() != SeatAvailabilityStatus.AVAILABLE) {
                        throw new IllegalStateException("Seat not available");
                }

                ShowEntity show = seat.getShow();

                // 3️⃣ Check show booking status
                if (show.getBookingStatus() != BookingStatus.OPEN) {
                        throw new IllegalStateException("Show booking is not open");
                }

                // 4️⃣ Lock seat (business)
                seat.setStatus(SeatAvailabilityStatus.LOCKED);

                // 5️⃣ Reduce availability
                if (show.getAvailableSeatCount() <= 0) {
                        throw new IllegalStateException("No seats available");
                }

                show.setAvailableSeatCount(
                                show.getAvailableSeatCount() - 1);

                // 6️⃣ Create booking
                BookingEntity booking = new BookingEntity();
                booking.setUser(user);
                booking.setShowSeat(seat);
                booking.setBookingTime(LocalDateTime.now());

                bookingRepo.save(booking);

                // 7️⃣ Finalize seat
                seat.setStatus(SeatAvailabilityStatus.BOOKED);

                if (show.getAvailableSeatCount() == 0) {
                        show.setBookingStatus(BookingStatus.SOLD_OUT);
                }

        }

        public void deleteBookings(DeleteBookings request) {
                UserEntity user = userRepo.findByName(request.getUserName())
                                .orElseThrow(() -> new ResponseStatusException(
                                                HttpStatus.NOT_FOUND,
                                                "User not found: " + request.getUserName()));
                if (user.getRole().equals(Role.ADMIN))
                        throw new AccessDeniedException("Only Users can book..!");
                for (Long bookingId : request.getBookingIds())
                        deleteBooking(bookingId);
        }

        public void deleteBooking(Long bookingId) {

                BookingEntity booking = bookingRepo.findById(bookingId)
                                .orElseThrow(() -> new EntityNotFoundException("Booking not found"));

                ShowSeatEntity seat = booking.getShowSeat();
                ShowEntity show = seat.getShow();

                seat.setStatus(SeatAvailabilityStatus.AVAILABLE);
                show.setAvailableSeatCount(show.getAvailableSeatCount() + 1);

                bookingRepo.delete(booking);
        }

        public static Specification<BookingEntity> byFilter(ReadBookings req) {

                return (root, query, cb) -> {

                        List<Predicate> predicates = new ArrayList<>();

                        if (req.getUserName() != null) {
                                predicates.add(
                                                cb.equal(root.get("user").get("userName"), req.getUserName()));
                        }

                        if (req.getShowId() != null) {
                                predicates.add(
                                                cb.equal(
                                                                root.get("showSeat").get("show").get("showId"),
                                                                req.getShowId()));
                        }

                        if (req.getSeatCategoryId() != null) {
                                predicates.add(
                                                cb.equal(
                                                                root.get("showSeat")
                                                                                .get("seatCategory")
                                                                                .get("id"),
                                                                req.getSeatCategoryId()));
                        }

                        return cb.and(predicates.toArray(new Predicate[0]));
                };
        }

        public List<ReadBookingResponse> readBookings(ReadBookings request) {

                List<BookingEntity> bookings;

                // ✅ Case 1: bookingIds provided → fetch directly
                if (request.getBookingIds() != null &&
                                request.getBookingIds().length > 0) {

                        bookings = bookingRepo.findAllById(
                                        List.of(request.getBookingIds()));

                }
                // ✅ Case 2: dynamic filtering
                else {
                        bookings = bookingRepo.findAll(
                                        BookingService.byFilter(request));
                }

                return bookings.stream()
                                .map(this::mapToResponse)
                                .toList();
        }

        // ---- Mapping logic ----
        private ReadBookingResponse mapToResponse(BookingEntity booking) {

                ShowSeatEntity seat = booking.getShowSeat();
                ShowEntity show = seat.getShow();
                AuditoriumEntity auditorium = show.getAuditorium();
                VenueEntity venueEntity = auditorium.getVenue();
                EventEntity event = show.getEvent();

                // ---- Venue (ID-only → read service) ----
                VenueReadResponse venue = venueService.read(
                                ReadVenue.builder()
                                                .venueId(venueEntity.getVenueId())
                                                .build())
                                .get(0);

                // ---- Seat (ID-only → read service) ----
                SeatReadResponse seatResponse = seatService.read(
                                ReadSeats.builder()
                                                .auditoriumId(auditorium.getAuditoriumId())
                                                .seats(new ReadSeat[] {
                                                                ReadSeat.builder()
                                                                                .seatId(seat.getSeat().getSeatId())
                                                                                .build()
                                                })
                                                .build())
                                .get(0);
                ShowSeatPricingEntity pricing = showSeatPricingRepo
                                .findByShow_ShowIdAndSeatCategory_Id(
                                                show.getShowId(),
                                                seat.getCategory().getId())
                                .orElseThrow(() -> new EntityNotFoundException(
                                                "Pricing not configured for show "
                                                                + show.getShowId()
                                                                + " and seat category "
                                                                + seat.getCategory().getName()));

                return ReadBookingResponse.builder()
                                // Booking
                                .bookingId(booking.getBookingId())
                                .bookingTime(booking.getBookingTime())

                                // Event
                                .eventId(event.getEventId())
                                .eventName(event.getEventName())

                                // Venue
                                .venue(venue)

                                // Auditorium
                                .auditoriumId(auditorium.getAuditoriumId())
                                .auditoriumName(auditorium.getAuditoriumName())

                                // Seat
                                .seat(seatResponse)
                                .seatCategory(seat.getCategory().getName())
                                .price(pricing.getPrice().doubleValue())

                                // Show
                                .showId(show.getShowId())
                                .showName(show.getShowName())
                                .showDateTime(show.getShowDateTime())
                                .duration(show.getDurationMinutes())

                                .build();
        }

}
