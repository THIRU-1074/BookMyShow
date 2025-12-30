package com.thiru.ticket_booking_service.ShowMgmt.auditorium;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;

import com.thiru.ticket_booking_service.userMgmt.*;

public interface AuditoriumRepository extends JpaRepository<AuditoriumEntity, Long> {
    List<AuditoriumEntity> findByAdmin(UserEntity admin);

    List<AuditoriumEntity> findByVenue_VenueId(Long venueId);

    List<AuditoriumEntity> findByAdminAndVenue_VenueId(UserEntity admin, Long venueId);

}
