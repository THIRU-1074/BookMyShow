package com.thiru.BookMyShow.ShowMgmt.showSeat;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.*;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.access.AccessDeniedException;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import com.thiru.BookMyShow.exception.*;
import com.thiru.BookMyShow.ShowMgmt.AuthorizationPolicy;
import com.thiru.BookMyShow.ShowMgmt.auditorium.*;
import com.thiru.BookMyShow.ShowMgmt.seat.*;
import com.thiru.BookMyShow.ShowMgmt.seatCategory.SeatCategoryEntity;
import com.thiru.BookMyShow.ShowMgmt.seatCategory.SeatCategoryRepository;
import com.thiru.BookMyShow.ShowMgmt.show.*;
import com.thiru.BookMyShow.ShowMgmt.showSeat.DTO.*;
import com.thiru.BookMyShow.userMgmt.*;

@Service
@RequiredArgsConstructor
@Transactional
public class ShowSeatService implements AuthorizationPolicy<ShowEntity, UserEntity> {

    private final ShowRepository showRepo;
    private final SeatRepository seatRepo;
    private final ShowSeatRepository showSeatRepo;
    private final UserRepository userRepo;
    private final SeatCategoryRepository seatCategoryRepo;

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
        throw new AccessDeniedException("You could update your shows seats only...!");
    }

    @Override
    public void canDelete(ShowEntity se, UserEntity ue) {
        if (!ue.getRole().equals(Role.ADMIN))
            throw new AccessDeniedException("Only Admin can delete...!");
        if (se.getEvent().getAdmin().getUserId().equals(ue.getUserId()))
            return;
        throw new AccessDeniedException("You could delete your shows seats only...!");
    }

    @Override
    public void canRead(ShowEntity se, UserEntity ue) {
        return;
    }

    public void createShowSeats(Long showId) {

        // 1️⃣ Load show
        ShowEntity show = showRepo.findById(showId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Show not found with id " + showId));

        // 2️⃣ Prevent duplicate creation
        if (showSeatRepo.existsByShow_ShowId(showId)) {
            throw new IllegalStateException(
                    "Show seats already created for showId " + showId);
        }

        // 3️⃣ Get auditorium from show
        AuditoriumEntity auditorium = show.getAuditorium();

        // 4️⃣ Fetch all seats of the auditorium
        List<SeatEntity> seats = seatRepo
                .findByAuditorium_AuditoriumId(
                        auditorium.getAuditoriumId());

        if (seats.isEmpty()) {
            throw new IllegalStateException(
                    "No seats found for auditorium "
                            + auditorium.getAuditoriumId());
        }

        // 5️⃣ Create ShowSeat entries
        SeatCategoryEntity seatCategory = seatCategoryRepo.findByName("REGULAR")
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Seat Category not dound...! " + showId));
        List<ShowSeatEntity> showSeats = seats.stream()
                .map(seat -> {
                    ShowSeatEntity ss = new ShowSeatEntity();
                    ss.setShow(show);
                    ss.setSeat(seat);
                    ss.setShowSeatCategory(seatCategory);
                    ss.setShowSeatAvailabilityStatus(SeatAvailabilityStatus.LOCKED);
                    return ss;
                })
                .toList();

        // 6️⃣ Bulk save
        showSeatRepo.saveAll(showSeats);
    }

    public void update(UpdateShowSeats request) {

        // 1️⃣ Load user
        UserEntity user = userRepo.findByUserName(request.getUserName())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // 2️⃣ Load show
        ShowEntity show = showRepo.findById(request.getShowId())
                .orElseThrow(() -> new ResourceNotFoundException("Show not found"));

        // 3️⃣ Authorization: show → event → admin must be ADMIN
        this.canUpdate(show, user);

        // 4️⃣ Iterate seat updates
        for (UpdateShowSeat seatUpdate : request.getSeats()) {

            ShowSeatEntity showSeat = showSeatRepo
                    .findByShow_ShowIdAndSeat_SeatId(
                            request.getShowId(),
                            seatUpdate.getSeatId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Seat " + seatUpdate.getSeatId()
                                    + " not found for show "
                                    + request.getShowId()));

            // 5️⃣ Partial updates (ONLY non-null fields)

            if (seatUpdate.getCategory() != null) {
                showSeat.setShowSeatCategory(seatUpdate.getCategory());
            }

            if (seatUpdate.getStatus() != null) {
                showSeat.setShowSeatAvailabilityStatus(seatUpdate.getStatus());
            }
        }

        // 6️⃣ No explicit save required (dirty checking)
    }

    public static Specification<ShowSeatEntity> withFilters(ReadShowSeat r) {

        return (root, query, cb) -> {

            List<Predicate> predicates = new ArrayList<>();

            // mandatory show filter
            predicates.add(cb.equal(
                    root.get("show").get("showId"),
                    r.getShowId()));

            if (r.getSeatId() != null) {
                predicates.add(cb.equal(
                        root.get("seat").get("seatId"),
                        r.getSeatId()));
            }

            if (r.getCategory() != null) {
                predicates.add(cb.equal(
                        root.get("category"),
                        r.getCategory()));
            }

            if (r.getStatus() != null) {
                predicates.add(cb.equal(
                        root.get("status"),
                        r.getStatus()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    public List<ShowSeatReadResponse> read(ReadShowSeat request) {

        List<ShowSeatEntity> showSeats = showSeatRepo.findAll(
                ShowSeatService.withFilters(request));

        if (showSeats.isEmpty()) {
            throw new ResourceNotFoundException(
                    "No show seats found matching criteria");
        }

        return showSeats.stream()
                .map(this::toResponse)
                .toList();
    }

    private ShowSeatReadResponse toResponse(ShowSeatEntity ss) {
        return ShowSeatReadResponse.builder()
                .showSeatId(ss.getShowSeatId())
                .seatId(ss.getSeat().getSeatId())
                .seatNo(ss.getSeat().getSeatNo())
                .category(ss.getShowSeatCategory())
                .status(ss.getShowSeatAvailabilityStatus())
                .build();
    }
}
