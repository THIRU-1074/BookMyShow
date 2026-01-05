package com.thiru.BookMyShow.ShowMgmt.seatCategory;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.access.AccessDeniedException;

import java.util.List;

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

    public SeatCategoryResponse create(CreateSeatCategory request) {
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

    public List<SeatCategoryResponse> read(ReadSeatCategory request) {

        List<SeatCategoryEntity> categories;

        if (request.getId() != null) {

            SeatCategoryEntity category = seatCategoryRepo.findById(request.getId())
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND,
                            "Seat category not found: " + request.getId()));

            categories = List.of(category);

        } else if (request.getName() != null) {

            SeatCategoryEntity category = seatCategoryRepo.findByName(
                    request.getName().toUpperCase()).orElseThrow(
                            () -> new ResponseStatusException(
                                    HttpStatus.NOT_FOUND,
                                    "Seat category not found: " + request.getId()));
            categories = List.of(category);

        } else {

            categories = seatCategoryRepo.findAll();
        }

        return categories.stream()
                .map(cat -> SeatCategoryResponse.builder()
                        .id(cat.getId())
                        .name(cat.getName())
                        .description(cat.getDescription())
                        .build())
                .toList();
    }

}
