package com.thiru.BookMyShow.bookingMgmt;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import java.util.List;

public interface BookingRepository
        extends JpaRepository<BookingEntity, Long>,
        JpaSpecificationExecutor<BookingEntity> {
    List<BookingEntity> findByUser_UserId(Long userId);
}
