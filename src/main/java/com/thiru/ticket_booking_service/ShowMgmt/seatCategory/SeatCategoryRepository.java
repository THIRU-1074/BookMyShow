package com.thiru.ticket_booking_service.ShowMgmt.seatCategory;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SeatCategoryRepository
        extends JpaRepository<SeatCategoryEntity, Long> {

    Optional<SeatCategoryEntity> findByName(String name);

    boolean existsByName(String name);
}
