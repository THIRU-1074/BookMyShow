package com.thiru.BookMyShow.ShowMgmt.seatCategory;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import com.thiru.BookMyShow.ShowMgmt.seatCategory.DTO.*;
import com.thiru.BookMyShow.userMgmt.*;

@Service
@RequiredArgsConstructor
@Transactional
public class SeatCategoryService {

    private final SeatCategoryRepository seatCategoryRepo;
    private final UserRepository userRepo;

    public void canCreate(UserEntity ue) {
        if (ue.getRole().equals(Role.ADMIN))
            return;
        throw new AccessDeniedException("Only Admin can create...!");
    }

    public SeatCategoryResponse create(CreateSeatCategoryRequest request) {
        UserEntity ue = userRepo.findByUserName(request.getUserName())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "User not found: " + request.getUserName()));
        this.canCreate(ue);
        // prevent duplicates
        if (seatCategoryRepo.existsByName(request.getName())) {
            throw new IllegalArgumentException(
                    "Seat category already exists: " + request.getName());
        }

        SeatCategoryEntity category = SeatCategoryEntity.builder()
                .name(request.getName().toUpperCase()) // normalize
                .description(request.getDescription())
                .build();

        SeatCategoryEntity saved = seatCategoryRepo.save(category);

        return SeatCategoryResponse.builder()
                .id(saved.getId())
                .name(saved.getName())
                .description(saved.getDescription())
                .build();
    }
}
