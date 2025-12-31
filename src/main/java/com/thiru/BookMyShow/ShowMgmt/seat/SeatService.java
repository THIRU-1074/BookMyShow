package com.thiru.BookMyShow.ShowMgmt.seat;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.security.access.AccessDeniedException;
import java.util.*;
import jakarta.transaction.Transactional;

import com.thiru.BookMyShow.exception.*;
import com.thiru.BookMyShow.ShowMgmt.AuthorizationPolicy;
import com.thiru.BookMyShow.ShowMgmt.auditorium.AuditoriumEntity;
import com.thiru.BookMyShow.ShowMgmt.auditorium.AuditoriumRepository;
import com.thiru.BookMyShow.ShowMgmt.seat.DTO.*;
import com.thiru.BookMyShow.userMgmt.*;

@Service
@RequiredArgsConstructor
@Transactional
public class SeatService implements AuthorizationPolicy<SeatEntity, UserEntity> {
        private final AuditoriumRepository auditoriumRepo;
        private final SeatRepository seatRepo;

        @Override
        public void canCreate(UserEntity ue) {
                if (ue.getRole().equals(Role.ADMIN))
                        return;
                throw new AccessDeniedException("Only Admin can create...!");
        }

        @Override
        public void canUpdate(SeatEntity se, UserEntity ue) {
                if (!ue.getRole().equals(Role.ADMIN))
                        throw new AccessDeniedException("Only Admin can update...!");
                if (se.getAuditorium().getAdmin().getUserId().equals(ue.getUserId()))
                        return;
                throw new AccessDeniedException("You could update your auditorium's seats...!");
        }

        @Override
        public void canDelete(SeatEntity se, UserEntity ue) {
                if (!ue.getRole().equals(Role.ADMIN))
                        throw new AccessDeniedException("Only Admin can delete...!");
                if (se.getAuditorium().getAdmin().getUserId().equals(ue.getUserId()))
                        return;
                throw new AccessDeniedException("You could only delete your auditorium's seats...!");
        }

        @Override
        public void canRead(SeatEntity se, UserEntity ue) {
                return;
        }

        public void create(CreateSeats request) {

                // 2️⃣ Validate auditorium existence
                AuditoriumEntity auditorium = auditoriumRepo
                                .findById(request.getAuditoriumId())
                                .orElseThrow(() -> new ResponseStatusException(
                                                HttpStatus.BAD_REQUEST,
                                                "Invalid auditoriumId: " + request.getAuditoriumId()));

                // 4️⃣ Map DTO → Entity (SAFE construction)
                List<SeatEntity> entities = Arrays.stream(request.getSeats())
                                .map(seat -> SeatEntity.builder()
                                                .seatNo(seat.getSeatNo())
                                                .row(seat.getRow())
                                                .col(seat.getCol())
                                                .stance(seat.getStance())
                                                .auditorium(auditorium)
                                                .build())
                                .toList();

                // 5️⃣ Persist in bulk
                seatRepo.saveAll(entities);
        }

        public void update(UpdateSeats request) {

                Long auditoriumId = request.getAuditoriumId();

                for (UpdateSeat seatUpdate : request.getSeats()) {

                        SeatEntity seat = seatRepo
                                        .findBySeatIdAndAuditorium_AuditoriumId(
                                                        seatUpdate.getSeatId(),
                                                        auditoriumId)
                                        .orElseThrow(() -> new ResourceNotFoundException(
                                                        "Seat not found for seatId "
                                                                        + seatUpdate.getSeatId()
                                                                        + " in auditorium "
                                                                        + auditoriumId));

                        // ✅ Only editable field
                        if (seatUpdate.getSeatNo() != null &&
                                        !seatUpdate.getSeatNo().equals(seat.getSeatNo())) {

                                seat.setSeatNo(seatUpdate.getSeatNo());
                        }
                }

                // No explicit save needed — JPA dirty checking will persist changes
        }

        public List<SeatReadResponse> read(ReadSeats request) {

                if (request.getAuditoriumId() == null) {
                        throw new IllegalArgumentException("auditoriumId is required");
                }

                List<SeatEntity> seats;

                // 1️⃣ Prefer seatIds if provided
                if (request.getSeats() != null && request.getSeats().length > 0) {

                        List<Long> seatIds = Arrays.stream(request.getSeats())
                                        .map(ReadSeat::getSeatId)
                                        .filter(Objects::nonNull)
                                        .toList();

                        if (seatIds.isEmpty()) {
                                throw new IllegalArgumentException("seatIds cannot be empty");
                        }

                        seats = seatRepo.findBySeatIdInAndAuditorium_AuditoriumId(
                                        seatIds,
                                        request.getAuditoriumId());

                } else {
                        // 2️⃣ Otherwise read all seats of the auditorium
                        seats = seatRepo.findByAuditorium_AuditoriumId(
                                        request.getAuditoriumId());
                }

                if (seats.isEmpty()) {
                        throw new ResourceNotFoundException("No seats found");
                }

                return seats.stream()
                                .map(this::toResponse)
                                .toList();
        }

        private SeatReadResponse toResponse(SeatEntity seat) {
                return SeatReadResponse.builder()
                                .seatId(seat.getSeatId())
                                .seatNo(seat.getSeatNo())
                                .row(seat.getRow())
                                .col(seat.getCol())
                                .stance(seat.getStance())
                                .build();
        }
}
