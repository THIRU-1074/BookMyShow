package com.thiru.BookMyShow.ShowMgmt.showSeat;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;

public interface ShowSeatRepository
                extends JpaRepository<ShowSeatEntity, Long>, JpaSpecificationExecutor<ShowSeatEntity> {

        boolean existsByShow_ShowId(Long showId);

        List<ShowSeatEntity> findByShow_ShowIdAndSeat_SeatIdIn(
                        Long showId,
                        Collection<Long> seatIds);

        Optional<ShowSeatEntity> findByShow_ShowIdAndSeat_SeatId(
                        Long showId,
                        Long seatId);

        @Lock(LockModeType.PESSIMISTIC_WRITE)
        @Query("""
                            SELECT s FROM ShowSeatEntity s
                            WHERE s.id = :seatId
                        """)
        Optional<ShowSeatEntity> lockById(@Param("seatId") Long seatId);
}
