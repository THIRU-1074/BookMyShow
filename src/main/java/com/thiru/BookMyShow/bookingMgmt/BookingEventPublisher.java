package com.thiru.BookMyShow.bookingMgmt;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import com.thiru.BookMyShow.bookingMgmt.DTO.BookingConfirmedEvent;
import com.thiru.BookMyShow.config.*;

@Service
public class BookingEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public BookingEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publishBookingConfirmed(BookingConfirmedEvent event) {
        rabbitTemplate.convertAndSend(
                RabbitConfig.BOOKING_EXCHANGE,
                RabbitConfig.BOOKING_CONFIRMED_KEY,
                event);
    }
}
