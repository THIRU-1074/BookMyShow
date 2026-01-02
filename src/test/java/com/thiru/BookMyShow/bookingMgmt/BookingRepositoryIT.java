package com.thiru.BookMyShow.bookingMgmt;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import com.thiru.BookMyShow.ShowMgmt.showSeat.SeatAvailabilityStatus;
import com.thiru.BookMyShow.testutil.BookingPersistenceHelper;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class BookingRepositoryIT {

    @Container
    static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("bookmyshow")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void overrideDataSourceProps(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void postgresContainerPersistsAndLoadsBookings() {
        BookingEntity persisted = BookingPersistenceHelper.persistBookingGraph(entityManager.getEntityManager());

        var stored = bookingRepository.findByUser_UserId(persisted.getUser().getUserId());

        assertThat(stored).hasSize(1);
        assertThat(stored.get(0).getTicketBookingStatus()).isEqualTo(TicketBookingStatus.PENDING_PAYMENT);
        assertThat(stored.get(0).getShowSeat().getShowSeatAvailabilityStatus())
                .isEqualTo(SeatAvailabilityStatus.AVAILABLE);
    }
}
