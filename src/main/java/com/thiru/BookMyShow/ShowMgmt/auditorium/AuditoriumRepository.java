package com.thiru.BookMyShow.ShowMgmt.auditorium;

import org.springframework.data.jpa.repository.JpaRepository;

import com.thiru.BookMyShow.userMgmt.*;

import java.util.*;

public interface AuditoriumRepository extends JpaRepository<AuditoriumEntity, Long> {
    List<AuditoriumEntity> findByAdmin(UserEntity admin);

    List<AuditoriumEntity> findByVenue_VenueId(Long venueId);

    List<AuditoriumEntity> findByAdminAndVenue_VenueId(UserEntity admin, Long venueId);

}
