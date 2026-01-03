package com.thiru.BookMyShow.notification;

import org.springframework.stereotype.Service;

import com.thiru.BookMyShow.bookingMgmt.DTO.BookingConfirmedEvent;

@Service
public class EmailService implements NotificationService {
    public void sendBookingConfirmedNotification(BookingConfirmedEvent event) {
        // 📧 Simulate Email
        System.out.println("""
                📧 EMAIL SENT
                To: %s
                Booking ID: %d
                Seats: %s
                Amount: %.2f
                """.formatted(
                event.getEmail(),
                event.getBookingId(),
                event.getSeatNumbers(),
                event.getAmount()));
    }
}
