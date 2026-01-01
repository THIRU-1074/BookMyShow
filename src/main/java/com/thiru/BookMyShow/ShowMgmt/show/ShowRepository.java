package com.thiru.BookMyShow.ShowMgmt.show;

import java.util.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;

public interface ShowRepository extends JpaRepository<ShowEntity, Long>, JpaSpecificationExecutor<ShowEntity> {
  List<ShowEntity> findByEvent_EventId(Long eventId);

  Optional<ShowEntity> findByShowId(Long showId);

  @Query("""
          SELECT s
          FROM ShowEntity s
          JOIN s.auditorium a
          JOIN a.venue v
          WHERE v.venueId = :venueId
            AND s.showDateTime >= :start
            AND s.showDateTime < :end
          ORDER BY s.showDateTime
      """)
  List<ShowEntity> findShowsForVenueAndDateRange(
      @Param("venueId") Long venueId,
      @Param("start") LocalDateTime start,
      @Param("end") LocalDateTime end);
}