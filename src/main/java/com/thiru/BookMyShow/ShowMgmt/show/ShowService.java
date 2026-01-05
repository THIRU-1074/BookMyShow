package com.thiru.BookMyShow.ShowMgmt.show;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.cache.annotation.Caching;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.jpa.domain.Specification;
import java.util.*;
import jakarta.persistence.criteria.Predicate;

import com.thiru.BookMyShow.exception.*;
import com.thiru.BookMyShow.ShowMgmt.AuthorizationPolicy;
import com.thiru.BookMyShow.ShowMgmt.auditorium.AuditoriumEntity;
import com.thiru.BookMyShow.ShowMgmt.auditorium.AuditoriumRepository;
import com.thiru.BookMyShow.ShowMgmt.event.EventEntity;
import com.thiru.BookMyShow.ShowMgmt.event.EventRepository;
import com.thiru.BookMyShow.ShowMgmt.seat.SeatRepository;
import com.thiru.BookMyShow.ShowMgmt.show.DTO.*;
import com.thiru.BookMyShow.ShowMgmt.showSeat.ShowSeatService;
import com.thiru.BookMyShow.userMgmt.*;

@Service
@RequiredArgsConstructor
public class ShowService implements AuthorizationPolicy<ShowEntity, UserEntity> {
        private final ShowRepository showRepo;
        private final EventRepository eventRepo;
        private final AuditoriumRepository auditoriumRepo;
        private final UserRepository userRepo;
        private final ShowSeatService showSeatService;
        private final SeatRepository seatRepo;

        @Override
        public void canCreate(UserEntity ue) {
                if (ue.getRole().equals(Role.ADMIN))
                        return;
                throw new AccessDeniedException("Only Admin can create...!");
        }

        @Override
        public void canUpdate(ShowEntity se, UserEntity ue) {
                if (!ue.getRole().equals(Role.ADMIN))
                        throw new AccessDeniedException("Only Admin can update...!");
                if (se.getEvent().getAdmin().getUserId().equals(ue.getUserId()))
                        return;
                throw new AccessDeniedException("You could update your shows...!");
        }

        @Override
        public void canDelete(ShowEntity se, UserEntity ue) {
                if (!ue.getRole().equals(Role.ADMIN))
                        throw new AccessDeniedException("Only Admin can delete...!");
                if (se.getEvent().getAdmin().getUserId().equals(ue.getUserId()))
                        return;
                throw new AccessDeniedException("You could delete your shows...!");
        }

        @Override
        public void canRead(ShowEntity se, UserEntity ue) {
                return;
        }

        public void create(CreateShow show) {
                // 1️⃣ Validate Event existence
                String userName = show.getUserName();
                UserEntity ue = userRepo.findByUserName(userName)
                                .orElseThrow(() -> new ResponseStatusException(
                                                HttpStatus.NOT_FOUND,
                                                "User not found: " + userName));
                this.canCreate(ue);
                EventEntity event = eventRepo.findById(show.getEventId())
                                .orElseThrow(() -> new ResponseStatusException(
                                                HttpStatus.BAD_REQUEST,
                                                "Invalid eventId: " + show.getEventId()));

                // 2️⃣ Validate Auditorium existence
                AuditoriumEntity auditorium = auditoriumRepo.findById(show.getAuditoriumId())
                                .orElseThrow(() -> new ResponseStatusException(
                                                HttpStatus.BAD_REQUEST,
                                                "Invalid auditoriumId: " + show.getAuditoriumId()));
                Long seatCount = seatRepo.countByAuditorium(auditorium);
                // 3️⃣ Build ShowEntity (do NOT trust incoming object)
                ShowEntity showEntity = ShowEntity.builder()
                                .event(event)
                                .auditorium(auditorium)
                                .showName(show.getShowName())
                                .availableSeatCount(seatCount)
                                .bookingStatus(ShowBookingStatus.NOT_STARTED)
                                .showDateTime(show.getShowDateTime())
                                .durationMinutes(show.getDurationMinutes())
                                .genres(show.getGenres())
                                .languages(show.getLanguages())
                                .rating(0.0) // initial rating
                                .build();

                // 4️⃣ Persist
                showRepo.save(showEntity);

                // Creating ShowSeats
                showSeatService.createShowSeats(showEntity.getShowId());
        }

        @Caching(evict = {
                        @CacheEvict(value = "showDetails", key = "#showId"),
                        @CacheEvict(value = "showGroups", allEntries = true)
        })
        public void update(UpdateShow request) {
                // 1️⃣ Load user
                UserEntity user = userRepo.findByUserName(request.getUserName())
                                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

                // 2️⃣ Load show
                ShowEntity show = showRepo.findById(request.getShowId())
                                .orElseThrow(() -> new ResourceNotFoundException("Show not found"));

                // 3️⃣ Authorization (already implemented by you)
                this.canUpdate(show, user);
                if (request.getBookingStatus() != null)
                        updateBookingStatus(show, request.getBookingStatus());
                // 4️⃣ Partial updates (ONLY if non-null)
                if (request.getBookedSeatCount() != null) {
                        if (request.getBookedSeatCount() > show.getAvailableSeatCount())
                                throw new InsufficientSeatsException(
                                                request.getBookedSeatCount(),
                                                show.getAvailableSeatCount());
                        else
                                show.setAvailableSeatCount(show.getAvailableSeatCount() - request.getBookedSeatCount());
                        if (show.getAvailableSeatCount() == 0)
                                show.setBookingStatus(ShowBookingStatus.SOLD_OUT);
                }
                if (request.getShowName() != null)
                        show.setShowName(request.getShowName());

                if (request.getShowDateTime() != null)
                        show.setShowDateTime(request.getShowDateTime());

                if (request.getDurationMinutes() != null)
                        show.setDurationMinutes(request.getDurationMinutes());

                if (request.getEventId() != null) {
                        EventEntity event = eventRepo.findById(request.getEventId())
                                        .orElseThrow(() -> new ResourceNotFoundException("Event not found"));
                        show.setEvent(event);
                }

                if (request.getAuditoriumId() != null) {
                        AuditoriumEntity auditorium = auditoriumRepo
                                        .findById(request.getAuditoriumId())
                                        .orElseThrow(() -> new ResourceNotFoundException("Auditorium not found"));
                        show.setAuditorium(auditorium);
                }

                if (request.getGenres() != null)
                        show.setGenres(request.getGenres());

                if (request.getLanguages() != null)
                        show.setLanguages(request.getLanguages());

                // 5️⃣ Save (transactional dirty checking also works)
                showRepo.save(show);
        }

        public void updateBookingStatus(
                        ShowEntity show,
                        ShowBookingStatus newStatus) {

                ShowBookingStatus currentStatus = show.getBookingStatus();

                // Rule 1: CANCELLED is terminal
                if (currentStatus == ShowBookingStatus.CANCELLED) {
                        throw new InvalidBookingStatusTransitionException(
                                        "Cancelled show cannot be modified. Delete and recreate the show.");
                }

                // Rule 2: Cannot transition TO NOT_STARTED
                if (newStatus == ShowBookingStatus.NOT_STARTED) {
                        throw new InvalidBookingStatusTransitionException(
                                        "Show cannot be moved back to NOT_STARTED state.");
                }

                // Rule 3: OPEN requires seat validation
                if (newStatus == ShowBookingStatus.OPEN) {

                        if (show.getAvailableSeatCount() <= 0) {
                                show.setBookingStatus(ShowBookingStatus.SOLD_OUT);
                                return;
                        }

                        show.setBookingStatus(ShowBookingStatus.OPEN);
                        return;
                }

                // Default valid transition
                show.setBookingStatus(newStatus);
        }

        public void delete(DeleteShow request) {
                String userName = request.getUserName();
                // 1️⃣ Validate user
                UserEntity user = userRepo.findByUserName(userName)
                                .orElseThrow(() -> new ResponseStatusException(
                                                HttpStatus.NOT_FOUND,
                                                "User not found: " + userName));
                // 3️⃣ Fetch existing auditorium
                ShowEntity existing = showRepo.findById(request.getShowId())
                                .orElseThrow(() -> new ResponseStatusException(
                                                HttpStatus.NOT_FOUND,
                                                "Auditorium not found: " + request.getShowId()));
                this.canDelete(existing, user);
                showRepo.delete(existing);
        }

        public static Specification<ShowEntity> withFilters(
                        ReadShow r, UserEntity user) {
                // 'cb' stands for criteria builder
                return (root, query, cb) -> {
                        List<Predicate> predicates = new ArrayList<>();

                        // ownership / visibility filter
                        predicates.add(cb.equal(
                                        root.get("event").get("admin"),
                                        user));

                        if (r.getEventId() != null)
                                predicates.add(cb.equal(
                                                root.get("event").get("eventId"),
                                                r.getEventId()));

                        if (r.getAuditoriumId() != null)
                                predicates.add(cb.equal(
                                                root.get("auditorium").get("auditoriumId"),
                                                r.getAuditoriumId()));

                        if (r.getFromDateTime() != null)
                                predicates.add(cb.greaterThanOrEqualTo(
                                                root.get("showDateTime"),
                                                r.getFromDateTime()));

                        if (r.getToDateTime() != null)
                                predicates.add(cb.lessThanOrEqualTo(
                                                root.get("showDateTime"),
                                                r.getToDateTime()));

                        if (r.getGenres() != null && !r.getGenres().isEmpty())
                                predicates.add(root.get("genres").in(r.getGenres()));

                        if (r.getLanguages() != null && !r.getLanguages().isEmpty())
                                predicates.add(root.get("languages").in(r.getLanguages()));

                        return cb.and(predicates.toArray(new Predicate[0]));
                };
        }

        public List<ShowReadResponse> read(ReadShow request) {
                this.canRead(null, null);
                // 1️⃣ Load user
                UserEntity user = userRepo.findByUserName(request.getUserName())
                                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

                // 2️⃣ If showId → single read
                if (request.getShowId() != null) {

                        ShowEntity show = showRepo.findById(request.getShowId())
                                        .orElseThrow(() -> new ResourceNotFoundException("Show not found"));

                        canRead(show, user);

                        return List.of(toResponse(show));
                }

                // 3️⃣ Validate filters
                if (isAllFiltersNull(request)) {
                        throw new IllegalArgumentException(
                                        "At least one filter must be provided");
                }

                // 4️⃣ AND-based dynamic filtering
                List<ShowEntity> shows = showRepo.findAll(
                                withFilters(request, user));

                if (shows.isEmpty()) {
                        throw new ResourceNotFoundException(
                                        "No shows found matching criteria");
                }

                return shows.stream()
                                .map(this::toResponse)
                                .toList();
        }

        private boolean isAllFiltersNull(ReadShow r) {
                return r.getEventId() == null &&
                                r.getAuditoriumId() == null &&
                                r.getFromDateTime() == null &&
                                r.getToDateTime() == null &&
                                (r.getGenres() == null || r.getGenres().isEmpty()) &&
                                (r.getLanguages() == null || r.getLanguages().isEmpty());
        }

        private ShowReadResponse toResponse(ShowEntity s) {
                return ShowReadResponse.builder()
                                .showId(s.getShowId())
                                .showName(s.getShowName())
                                .showDateTime(s.getShowDateTime())
                                .durationMinutes(s.getDurationMinutes())
                                .eventId(s.getEvent().getEventId())
                                .auditoriumId(s.getAuditorium().getAuditoriumId())
                                .genres(s.getGenres())
                                .languages(s.getLanguages())
                                .availableSeatCount(s.getAvailableSeatCount())
                                .build();
        }
}
