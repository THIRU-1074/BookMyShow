package com.thiru.BookMyShow.ShowMgmt.showSeat;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ShowSeatRepository
                extends JpaRepository<ShowSeatEntity, Long>, JpaSpecificationExecutor<ShowSeatEntity> {

        boolean existsByShow_ShowId(Long showId);

        List<ShowSeatEntity> findByShow_ShowIdAndSeat_SeatIdIn(
                        Long showId,
                        Collection<Long> seatIds);

        Optional<ShowSeatEntity> findByShow_ShowIdAndSeat_SeatId(
                        Long showId,
                        Long seatId);
}
