package com.thiru.ticket_booking_service.bookingMgmt;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookingRepository extends JpaRepository<BookingEntity, Long> {

    List<BookingEntity> findByUser_UserId(Long userId);

    List<BookingEntity> findByEventId(Long eventId);

    boolean existsByShowIdAndSeatNo(Long showId, String seatNo);
}
