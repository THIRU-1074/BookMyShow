package com.thiru.BookMyShow.ShowMgmt.seatCategory;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thiru.BookMyShow.ShowMgmt.seatCategory.DTO.*;

@Service
@RequiredArgsConstructor
@Transactional
public class SeatCategoryService {

    private final SeatCategoryRepository seatCategoryRepository;

    public SeatCategoryResponse create(CreateSeatCategoryRequest request) {

        // prevent duplicates
        if (seatCategoryRepository.existsByName(request.getName())) {
            throw new IllegalArgumentException(
                    "Seat category already exists: " + request.getName());
        }

        SeatCategoryEntity category = SeatCategoryEntity.builder()
                .name(request.getName().toUpperCase()) // normalize
                .description(request.getDescription())
                .build();

        SeatCategoryEntity saved = seatCategoryRepository.save(category);

        return SeatCategoryResponse.builder()
                .id(saved.getId())
                .name(saved.getName())
                .description(saved.getDescription())
                .build();
    }
}
