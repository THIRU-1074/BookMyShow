package com.thiru.BookMyShow.ShowMgmt.seat;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;

import com.thiru.BookMyShow.ShowMgmt.auditorium.*;

public interface SeatRepository extends JpaRepository<SeatEntity, Long> {
        Optional<SeatEntity> findBySeatIdAndAuditorium_AuditoriumId(
                        Long seatId,
                        Long auditoriumId);

        List<SeatEntity> findByAuditorium_AuditoriumId(Long auditoriumId);

        List<SeatEntity> findBySeatIdInAndAuditorium_AuditoriumId(
                        Collection<Long> seatIds,
                        Long auditoriumId);

        long countByAuditorium(AuditoriumEntity auditorium);
}
