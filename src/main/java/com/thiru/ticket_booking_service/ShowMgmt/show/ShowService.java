package com.thiru.ticket_booking_service.ShowMgmt.show;

import com.thiru.ticket_booking_service.ShowMgmt.AuthorizationPolicy;
import com.thiru.ticket_booking_service.ShowMgmt.auditorium.AuditoriumEntity;
import com.thiru.ticket_booking_service.ShowMgmt.auditorium.AuditoriumRepository;
import com.thiru.ticket_booking_service.ShowMgmt.event.EventEntity;
import com.thiru.ticket_booking_service.ShowMgmt.event.EventRepository;
import com.thiru.ticket_booking_service.ShowMgmt.show.DTO.*;
import com.thiru.ticket_booking_service.ShowMgmt.showSeat.ShowSeatService;
import com.thiru.ticket_booking_service.userMgmt.UserEntity;
import com.thiru.ticket_booking_service.userMgmt.*;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.data.jpa.domain.Specification;
import java.util.*;
import jakarta.persistence.criteria.Predicate;

@Service
@RequiredArgsConstructor
public class ShowService implements AuthorizationPolicy<ShowEntity, UserEntity> {
        private final ShowRepository showRepo;
        private final EventRepository eventRepo;
        private final AuditoriumRepository auditoriumRepo;
        private final UserRepository userRepo;
        private final ShowSeatService showSeatService;

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
                throw new AccessDeniedException("You could update your auditoriums...!");
        }

        @Override
        public void canDelete(ShowEntity se, UserEntity ue) {
                if (!ue.getRole().equals(Role.ADMIN))
                        throw new AccessDeniedException("Only Admin can update...!");
                if (se.getEvent().getAdmin().getUserId().equals(ue.getUserId()))
                        return;
                throw new AccessDeniedException("You could update your auditoriums...!");
        }

        @Override
        public void canRead(ShowEntity se, UserEntity ue) {
                return;
        }

        public void create(CreateShow show) {
                // 1️⃣ Validate Event existence
                String userName = show.getUserName();
                UserEntity ue = userRepo.findByName(userName)
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

                // 3️⃣ Build ShowEntity (do NOT trust incoming object)
                ShowEntity showEntity = ShowEntity.builder()
                                .event(event)
                                .auditorium(auditorium)
                                .showName(show.getShowName())
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

        public void update(UpdateShow request) {
                // 1️⃣ Load user
                UserEntity user = userRepo.findByName(request.getUserName())
                                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

                // 2️⃣ Load show
                ShowEntity show = showRepo.findById(request.getShowId())
                                .orElseThrow(() -> new ResourceNotFoundException("Show not found"));

                // 3️⃣ Authorization (already implemented by you)
                this.canUpdate(show, user);

                // 4️⃣ Partial updates (ONLY if non-null)

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

        public void delete(DeleteShow request) {
                String userName = request.getUserName();
                // 1️⃣ Validate user
                UserEntity user = userRepo.findByName(userName)
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

                return (root, query, cb) -> {
                        List<Predicate> predicates = new ArrayList<>();

                        // ownership / visibility filter
                        predicates.add(cb.equal(
                                        root.get("admin").get("userId"),
                                        user.getUserId()));

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
                UserEntity user = userRepo.findByName(request.getUserName())
                                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

                // 2️⃣ If showId → single read
                if (request.getShowId() != null) {

                        ShowEntity show = showRepo.findById(request.getShowId())
                                        .orElseThrow(() -> new ResourceNotFoundException("Show not found"));

                        canRead(show, user); // already implemented by you

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
                                .build();
        }
}
