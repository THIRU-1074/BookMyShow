package com.thiru.BookMyShow.notification;

import org.springframework.stereotype.Service;

import com.thiru.BookMyShow.bookingMgmt.DTO.BookingConfirmedEvent;

@Service
public class SMService implements NotificationService {
    @Override
    public void sendBookingConfirmedNotification(BookingConfirmedEvent event) {
        // 📱 Simulate SMS (optional)
        System.out.println("""
                📱 SMS SENT
                Hi %s, your booking %d is confirmed!
                """.formatted(
                event.getUserName(),
                event.getBookingId()));
    }
}
