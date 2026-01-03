package com.thiru.BookMyShow.notification;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import com.thiru.BookMyShow.bookingMgmt.DTO.BookingConfirmedEvent;
import com.thiru.BookMyShow.config.RabbitConfig;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BookingNotificationConsumer {
    private final EmailService emailService;
    private final SMService sMService;

    @RabbitListener(queues = RabbitConfig.BOOKING_CONFIRMED_QUEUE)
    public void handleBookingConfirmed(BookingConfirmedEvent event) {
        emailService.sendBookingConfirmedNotification(event);
        sMService.sendBookingConfirmedNotification(event);
    }
}
