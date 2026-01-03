package com.thiru.BookMyShow.notification;

import org.springframework.stereotype.Service;

import com.thiru.BookMyShow.bookingMgmt.DTO.BookingConfirmedEvent;

@Service
public interface NotificationService {
    void sendBookingConfirmedNotification(BookingConfirmedEvent event);
}
