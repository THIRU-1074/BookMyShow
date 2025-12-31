package com.thiru.BookMyShow.ShowMgmt.venue;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.data.jpa.domain.Specification;
import java.util.*;
import jakarta.persistence.criteria.Predicate;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;

import com.thiru.BookMyShow.exception.*;
import com.thiru.BookMyShow.ShowMgmt.AuthorizationPolicy;
import com.thiru.BookMyShow.ShowMgmt.venue.DTO.*;
import com.thiru.BookMyShow.userMgmt.*;

@Service
@RequiredArgsConstructor
public class VenueService implements AuthorizationPolicy<VenueEntity, UserEntity> {
    private final VenueRepository venueRepo;
    private final UserRepository userRepo;

    @Override
    public void canCreate(UserEntity ue) {
        if (ue.getRole().equals(Role.ADMIN))
            return;
        throw new AccessDeniedException("Only Admin can create...!");
    }

    @Override
    public void canUpdate(VenueEntity ve, UserEntity ue) {
        throw new AccessDeniedException("Cannot update Venue...!");
    }

    @Override
    public void canDelete(VenueEntity ve, UserEntity ue) {
        return;
    }

    @Override
    public void canRead(VenueEntity ve, UserEntity ue) {
        return;
    }

    public void create(CreateVenue venue) {
        String userName = venue.getUserName();
        // 1️⃣ Validate user
        UserEntity user = userRepo.findByUserName(userName)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "User not found: " + userName));
        this.canCreate(user);
        VenueEntity venueEntity = VenueEntity.builder()
                .city(venue.getCity())
                .pincode(venue.getPincode())
                .addressLine1(venue.getAddressLine1())
                .addressLine2(venue.getAddressLine2())
                .landmark(venue.getLandmark())
                .build();
        venueRepo.save(venueEntity);
    }

    public List<VenueReadResponse> read(ReadVenue request) {
        this.canRead(null, null);
        // 1️⃣ If venueId present → direct lookup
        if (request.getVenueId() != null) {
            VenueEntity venue = venueRepo.findById(request.getVenueId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Venue not found with id " + request.getVenueId()));

            return List.of(toResponse(venue));
        }

        // 2️⃣ Validate at least one search field exists
        if (isAllSearchFieldsNull(request)) {
            throw new IllegalArgumentException(
                    "At least one search parameter must be provided");
        }

        // 3️⃣ Dynamic AND-based search
        List<VenueEntity> venues = venueRepo.findAll(
                withFilters(request));

        if (venues.isEmpty()) {
            throw new ResourceNotFoundException("No venues found matching criteria");
        }

        return venues.stream()
                .map(this::toResponse)
                .toList();
    }

    private boolean isAllSearchFieldsNull(ReadVenue r) {
        return r.getCity() == null &&
                r.getPincode() == null &&
                r.getAddressLine1() == null &&
                r.getAddressLine2() == null &&
                r.getLandmark() == null;
    }

    private VenueReadResponse toResponse(VenueEntity e) {
        return VenueReadResponse.builder()
                .venueId(e.getVenueId())
                .city(e.getCity())
                .pincode(e.getPincode())
                .addressLine1(e.getAddressLine1())
                .addressLine2(e.getAddressLine2())
                .landmark(e.getLandmark())
                .build();
    }

    public static Specification<VenueEntity> withFilters(ReadVenue r) {

        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (r.getCity() != null)
                predicates.add(cb.equal(root.get("city"), r.getCity()));

            if (r.getPincode() != null)
                predicates.add(cb.equal(root.get("pincode"), r.getPincode()));

            if (r.getAddressLine1() != null)
                predicates.add(cb.like(
                        cb.lower(root.get("addressLine1")),
                        "%" + r.getAddressLine1().toLowerCase() + "%"));

            if (r.getAddressLine2() != null)
                predicates.add(cb.like(
                        cb.lower(root.get("addressLine2")),
                        "%" + r.getAddressLine2().toLowerCase() + "%"));

            if (r.getLandmark() != null)
                predicates.add(cb.like(
                        cb.lower(root.get("landmark")),
                        "%" + r.getLandmark().toLowerCase() + "%"));

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
