package com.thiru.BookMyShow.ShowMgmt.event;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import com.thiru.BookMyShow.testutil.BookingTestDataFactory;
import com.thiru.BookMyShow.userMgmt.Role;
import com.thiru.BookMyShow.userMgmt.UserEntity;

@DataJpaTest
class EventRepositoryTest {

    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private TestEntityManager entityManager;

    @Test
    void findByEventNameAndTypeFiltersCorrectly() {
        UserEntity admin = BookingTestDataFactory.user(null, "admin", Role.ADMIN);
        entityManager.persist(admin);

        EventEntity movie = BookingTestDataFactory.event(null, "Premiere", admin);
        movie.setEventType(EventType.MOVIE);
        movie.setAdmin(admin);
        EventEntity concert = BookingTestDataFactory.event(null, "Premiere", admin);
        concert.setEventType(EventType.CONCERT);
        concert.setAdmin(admin);
        entityManager.persist(movie);
        entityManager.persist(concert);
        entityManager.flush();

        List<EventEntity> events = eventRepository.findByEventNameAndEventType("Premiere", EventType.MOVIE);

        assertThat(events)
                .singleElement()
                .extracting(EventEntity::getEventType)
                .isEqualTo(EventType.MOVIE);
    }
}
