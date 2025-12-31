package com.thiru.BookMyShow.ShowMgmt.showSeatPricing;

import java.util.*;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShowSeatPricingRepository
                extends JpaRepository<ShowSeatPricingEntity, Long> {

        boolean existsByShow_ShowIdAndSeatCategory_Id(
                        Long showId,
                        Long seatCategoryId);

        Optional<ShowSeatPricingEntity> findById(Long pricingId);

        List<ShowSeatPricingEntity> findByShow_ShowId(Long showId);

        Optional<ShowSeatPricingEntity> findByShow_ShowIdAndSeatCategory_Id(
                        Long showId,
                        Long seatCategoryId);
}
