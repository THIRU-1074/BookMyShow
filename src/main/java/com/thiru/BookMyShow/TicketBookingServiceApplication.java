package com.thiru.BookMyShow;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties
public class TicketBookingServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(TicketBookingServiceApplication.class, args);
	}

}
