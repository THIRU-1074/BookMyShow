package com.thiru.BookMyShow.searchBrowse;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import java.util.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;

import com.thiru.BookMyShow.ShowMgmt.event.*;
import com.thiru.BookMyShow.ShowMgmt.show.*;

@Repository
public interface SearchBrowseRepository extends JpaRepository<EventEntity, Long> {
     @Query("""
                   SELECT DISTINCT e
                   FROM EventEntity e
                   JOIN e.shows s
                   JOIN s.auditorium a
                   JOIN a.venue v
                   WHERE (:city IS NULL OR v.city = :city)
                     AND (:genre IS NULL OR :genre MEMBER OF s.genres)
                     AND (:language IS NULL OR :language MEMBER OF s.languages)
                     AND (
                          :startDateTime IS NULL OR
                          (s.showDateTime >= :startDateTime AND s.showDateTime < :endDateTime)
                     )
               """)
     List<EventEntity> browseEvents(
               @Param("city") String city,
               @Param("genre") Genre genre,
               @Param("language") Language language,
               @Param("startDateTime") LocalDateTime startDateTime,
               @Param("endDateTime") LocalDateTime endDateTime);

}
