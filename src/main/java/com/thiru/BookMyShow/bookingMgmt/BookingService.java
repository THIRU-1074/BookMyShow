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
import com.thiru.BookMyShow.paymentGateWay.PaymentGateway;
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
import com.thiru.BookMyShow.ShowMgmt.seatCategory.*;

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
        private final PaymentGateway paymentGateway;
        private final ShowSeatPricingRepository pricingRepo;
        private final BookingEventPublisher bookingEventPublisher;

        public void createBookings(CreateBookings req) {
                UserEntity user = userRepo.findByUserName(req.getUserName())
                                .orElseThrow(() -> new ResponseStatusException(
                                                HttpStatus.NOT_FOUND, "User not found"));

                if (user.getRole().equals(Role.ADMIN))
                        throw new AccessDeniedException("Only Users can book");

                List<BookingEntity> bookings = new ArrayList<>();

                for (CreateBooking cb : req.getBookings()) {
                        bookings.add(prepareBooking(cb, user));
                }

                // 💳 PAYMENT STEP (outside DB writes but inside TX)
                boolean paymentSuccess = paymentGateway.charge(
                                user,
                                bookings.size(),
                                calculateAmount(bookings));

                if (!paymentSuccess) {
                        throw new PaymentFailedException("Payment failed");
                }

                // 🎯 FINALIZE BOOKINGS
                bookings.forEach(this::finalizeBooking);
                BookingConfirmedEvent event = new BookingConfirmedEvent(
                                bookings.get(0).getBookingId(),
                                user.getUserName(),
                                user.getMailId(),
                                bookings.stream()
                                                .map(b -> b.getShowSeat()
                                                                .getSeat()
                                                                .getSeatNo())
                                                .toList(),
                                calculateAmount(bookings));

                bookingEventPublisher.publishBookingConfirmed(event);
        }

        public BookingEntity prepareBooking(CreateBooking req, UserEntity user) {

                // DB-level lock
                ShowSeatEntity seat = showSeatRepo.lockById(req.getSeatId())
                                .orElseThrow(() -> new EntityNotFoundException("Seat not found"));

                if (seat.getShowSeatAvailabilityStatus() != SeatAvailabilityStatus.AVAILABLE) {
                        throw new IllegalStateException("Seat not available");
                }

                ShowEntity show = seat.getShow();

                if (show.getBookingStatus() != ShowBookingStatus.OPEN) {
                        throw new IllegalStateException("Show booking closed");
                }

                if (show.getAvailableSeatCount() <= 0) {
                        throw new IllegalStateException("No seats available");
                }

                // Business lock
                seat.setShowSeatAvailabilityStatus(SeatAvailabilityStatus.LOCKED);
                show.setAvailableSeatCount(show.getAvailableSeatCount() - 1);

                BookingEntity booking = new BookingEntity();
                booking.setUser(user);
                booking.setShowSeat(seat);
                booking.setBookingTime(LocalDateTime.now());
                booking.setTicketBookingStatus(TicketBookingStatus.PENDING_PAYMENT);

                return bookingRepo.save(booking);
        }

        public void finalizeBooking(BookingEntity booking) {

                ShowSeatEntity seat = booking.getShowSeat();
                ShowEntity show = seat.getShow();

                seat.setShowSeatAvailabilityStatus(SeatAvailabilityStatus.BOOKED);
                booking.setTicketBookingStatus(TicketBookingStatus.CONFIRMED);

                if (show.getAvailableSeatCount() == 0) {
                        show.setBookingStatus(ShowBookingStatus.SOLD_OUT);
                }
        }

        public Double calculateAmount(List<BookingEntity> bookings) {

                Double total = (double) 0;

                for (BookingEntity booking : bookings) {

                        ShowSeatEntity seat = booking.getShowSeat();
                        ShowEntity show = seat.getShow();
                        SeatCategoryEntity category = seat.getShowSeatCategory();

                        ShowSeatPricingEntity pricing = pricingRepo.findByShowAndSeatCategory(show, category)
                                        .orElseThrow(() -> new IllegalStateException(
                                                        "Pricing not configured for showId="
                                                                        + show.getShowId()
                                                                        + ", seatCategory="
                                                                        + category.getName()));

                        total = total + (Double.valueOf(pricing.getPrice()));
                }

                return total;
        }

        public void deleteBookings(DeleteBookings request) {
                UserEntity user = userRepo.findByUserName(request.getUserName())
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

                seat.setShowSeatAvailabilityStatus(SeatAvailabilityStatus.AVAILABLE);
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
                                                seat.getShowSeatCategory().getId())
                                .orElseThrow(() -> new EntityNotFoundException(
                                                "Pricing not configured for show "
                                                                + show.getShowId()
                                                                + " and seat category "
                                                                + seat.getShowSeatCategory().getName()));

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
                                .seatCategory(seat.getShowSeatCategory().getName())
                                .price(pricing.getPrice().doubleValue())

                                // Show
                                .showId(show.getShowId())
                                .showName(show.getShowName())
                                .showDateTime(show.getShowDateTime())
                                .duration(show.getDurationMinutes())

                                .build();
        }

}
