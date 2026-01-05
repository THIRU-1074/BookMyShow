package com.thiru.BookMyShow.ShowMgmt.auditorium;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.security.access.AccessDeniedException;
import java.util.*;

import com.thiru.BookMyShow.ShowMgmt.AuthorizationPolicy;
import com.thiru.BookMyShow.ShowMgmt.auditorium.DTO.*;
import com.thiru.BookMyShow.ShowMgmt.venue.VenueEntity;
import com.thiru.BookMyShow.ShowMgmt.venue.VenueRepository;
import com.thiru.BookMyShow.userMgmt.Role;
import com.thiru.BookMyShow.userMgmt.UserEntity;
import com.thiru.BookMyShow.userMgmt.UserRepository;

import jakarta.transaction.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AuditoriumService implements AuthorizationPolicy<AuditoriumEntity, UserEntity> {
    private final AuditoriumRepository auditoriumRepo;
    private final VenueRepository venueRepo;
    private final UserRepository userRepo;

    @Override
    public void canCreate(UserEntity ue) {
        if (ue.getRole().equals(Role.ADMIN))
            return;
        throw new AccessDeniedException("Only Admin can create...!");
    }

    @Override
    public void canUpdate(AuditoriumEntity ae, UserEntity ue) {
        if (!ue.getRole().equals(Role.ADMIN))
            throw new AccessDeniedException("Only Admin can update...!");
        if (ae.getAdmin().getUserId().equals(ue.getUserId()))
            return;
        throw new AccessDeniedException("You could update your auditoriums only...!");
    }

    @Override
    public void canDelete(AuditoriumEntity ae, UserEntity ue) {
        if (!ue.getRole().equals(Role.ADMIN))
            throw new AccessDeniedException("Only Admin can delete...!");
        if (ae.getAdmin().getUserId().equals(ue.getUserId()))
            return;
        throw new AccessDeniedException("You could delete your auditoriums only...!");
    }

    @Override
    public void canRead(AuditoriumEntity ae, UserEntity ue) {
        if (!ue.getRole().equals(Role.ADMIN))
            throw new AccessDeniedException("Only Admin can read...!");
        if (ae.getAdmin().getUserId().equals(ue.getUserId()))
            return;
        throw new AccessDeniedException("You could read your auditoriums only...!");
    }

    public void create(createAuditorium auditorium) {
        String userName = auditorium.getUserName();
        UserEntity ue = userRepo.findByUserName(userName)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "User not found: " + userName));
        this.canCreate(ue);

        Long venueId = auditorium.getVenueId();

        // 2️⃣ Validate venue existence
        VenueEntity venue = venueRepo.findById(venueId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Invalid venueId: " + venueId));
        // 3️⃣ Create fresh entity (do NOT reuse request object)
        AuditoriumEntity auditoriumEntity = AuditoriumEntity.builder()
                .auditoriumName(auditorium.getAuditoriumName())
                .venue(venue)
                .admin(ue)
                .build();

        // 4️⃣ Persist
        auditoriumRepo.save(auditoriumEntity);
    }

    public void update(updateAuditorium auditorium) {
        String userName = auditorium.getUserName();
        // 1️⃣ Validate user
        UserEntity user = userRepo.findByUserName(userName)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "User not found: " + userName));
        // 3️⃣ Fetch existing auditorium
        AuditoriumEntity existing = auditoriumRepo.findById(auditorium.getAuditoriumId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Auditorium not found: " + auditorium.getAuditoriumId()));
        this.canUpdate(existing, user);
        if (auditorium.getAuditoriumName() != null) {
            existing.setAuditoriumName(auditorium.getAuditoriumName());
        }
        // ❌ venueId update intentionally blocked
        // Changing venue breaks seat + show integrity

        // 6️⃣ Persist

        auditoriumRepo.save(existing);
    }

    public void delete(deleteAuditorium auditorium) {
        String userName = auditorium.getUserName();
        // 1️⃣ Validate user
        UserEntity user = userRepo.findByUserName(userName)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "User not found: " + userName));
        // 3️⃣ Fetch existing auditorium
        AuditoriumEntity existing = auditoriumRepo.findById(auditorium.getAuditoriumId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Auditorium not found: " + auditorium.getAuditoriumId()));
        this.canDelete(existing, user);
        auditoriumRepo.delete(existing);

    }

    public List<AuditoriumReadResponse> read(readAuditorium request) {

        if (request.getUserName() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Either userName must be provided");
        }

        UserEntity user = null;
        if (request.getUserName() != null) {
            user = userRepo.findByUserName(request.getUserName())
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND,
                            "User not found: " + request.getUserName()));
        }

        List<AuditoriumEntity> auditoriums = new ArrayList<AuditoriumEntity>();

        // 1️⃣ Search by BOTH
        if (user != null && request.getVenueId() != null) {
            auditoriums = auditoriumRepo
                    .findByAdminAndVenue_VenueId(user, request.getVenueId());

            // 2️⃣ Search by user only
        } else if (user != null) {
            auditoriums = auditoriumRepo.findByAdmin(user);

        }

        // 4️⃣ Authorization check per result
        for (AuditoriumEntity ae : auditoriums) {
            canRead(ae, user);
        }

        return auditoriums.stream()
                .map(this::toResponse)
                .toList();
    }

    private AuditoriumReadResponse toResponse(AuditoriumEntity entity) {
        return AuditoriumReadResponse.builder()
                .auditoriumId(entity.getAuditoriumId())
                .auditoriumName(entity.getAuditoriumName())
                .venueId(entity.getVenue().getVenueId())
                .build();
    }
}
