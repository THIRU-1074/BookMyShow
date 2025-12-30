package com.thiru.BookMyShow.bookingMgmt;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookingRepository extends JpaRepository<BookingEntity, Long> {

    List<BookingEntity> findByUser_UserId(Long userId);

}
