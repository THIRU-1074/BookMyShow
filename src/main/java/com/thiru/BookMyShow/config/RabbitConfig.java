package com.thiru.BookMyShow.config;

import org.springframework.amqp.core.Queue; // ✅ CORRECT
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.amqp.core.Binding; // ✅ CORRECT
import org.springframework.amqp.rabbit.connection.ConnectionFactory;

@Configuration
public class RabbitConfig {

    public static final String BOOKING_EXCHANGE = "booking.exchange";
    public static final String BOOKING_CONFIRMED_QUEUE = "booking.confirmed.queue";
    public static final String BOOKING_CONFIRMED_KEY = "booking.confirmed";

    @Bean
    public TopicExchange bookingExchange() {
        return new TopicExchange(BOOKING_EXCHANGE);
    }

    @Bean
    public Queue bookingConfirmedQueue() {
        return new Queue(BOOKING_CONFIRMED_QUEUE, true);
    }

    @Bean
    public Binding bookingConfirmedBinding() {
        return BindingBuilder
                .bind(bookingConfirmedQueue())
                .to(bookingExchange())
                .with(BOOKING_CONFIRMED_KEY);
    }

    @Bean
    public MessageConverter jacksonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(
            ConnectionFactory connectionFactory,
            MessageConverter messageConverter) {

        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter);
        return template;
    }

}
