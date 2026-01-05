package com.thiru.BookMyShow.ShowMgmt.show;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import com.thiru.BookMyShow.ShowMgmt.auditorium.AuditoriumEntity;
import com.thiru.BookMyShow.ShowMgmt.event.EventEntity;
import com.thiru.BookMyShow.ShowMgmt.event.EventType;
import com.thiru.BookMyShow.ShowMgmt.show.ShowRepository;
import com.thiru.BookMyShow.ShowMgmt.venue.VenueEntity;
import com.thiru.BookMyShow.testutil.BookingTestDataFactory;
import com.thiru.BookMyShow.userMgmt.Role;
import com.thiru.BookMyShow.userMgmt.UserEntity;

@DataJpaTest
class ShowRepositoryTest {

    @Autowired
    private ShowRepository showRepository;
    @Autowired
    private TestEntityManager entityManager;

    @Test
    void findShowsForVenueAndDateRangeReturnsOrderedShowList() {
        UserEntity admin = BookingTestDataFactory.user(null, "admin", Role.ADMIN);
        entityManager.persist(admin);

        VenueEntity venue = BookingTestDataFactory.venue(null, "Bengaluru");
        entityManager.persist(venue);
        VenueEntity otherVenue = BookingTestDataFactory.venue(null, "Chennai");
        entityManager.persist(otherVenue);

        EventEntity event = BookingTestDataFactory.event(null, "Movie", admin);
        event.setEventType(EventType.MOVIE);
        event.setAdmin(admin);
        entityManager.persist(event);

        AuditoriumEntity auditorium = BookingTestDataFactory.auditorium(null, venue, admin);
        entityManager.persist(auditorium);
        AuditoriumEntity otherAuditorium = BookingTestDataFactory.auditorium(null, otherVenue, admin);
        entityManager.persist(otherAuditorium);

        entityManager.persist(BookingTestDataFactory.show(null, auditorium, event, 100L));
        entityManager.persist(BookingTestDataFactory.show(null, otherAuditorium, event, 100L));
        entityManager.flush();

        var shows = showRepository.findShowsForVenueAndDateRange(
                venue.getVenueId(),
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now().plusDays(2));

        assertThat(shows)
                .hasSize(1)
                .allMatch(show -> show.getAuditorium().getVenue().getVenueId().equals(venue.getVenueId()));
    }
}
