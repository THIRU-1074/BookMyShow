package com.thiru.BookMyShow.searchBrowse;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import com.thiru.BookMyShow.ShowMgmt.auditorium.AuditoriumEntity;
import com.thiru.BookMyShow.ShowMgmt.event.EventEntity;
import com.thiru.BookMyShow.ShowMgmt.event.EventType;
import com.thiru.BookMyShow.ShowMgmt.show.Genre;
import com.thiru.BookMyShow.ShowMgmt.show.Language;
import com.thiru.BookMyShow.ShowMgmt.show.ShowEntity;
import com.thiru.BookMyShow.ShowMgmt.venue.VenueEntity;
import com.thiru.BookMyShow.testutil.BookingTestDataFactory;

@DataJpaTest
class SearchBrowseRepositoryTest {

        @Autowired
        private SearchBrowseRepository repository;

        @Autowired
        private TestEntityManager entityManager;

        @Test
        void browseEventsFiltersByCityGenreLanguageAndDate() {
                persistShowGraph("Bengaluru", Genre.ACTION, Language.ENGLISH,
                                LocalDateTime.of(2026, 1, 15, 18, 0));
                persistShowGraph("Chennai", Genre.DRAMA, Language.HINDI,
                                LocalDateTime.of(2026, 1, 15, 18, 0));

                var results = repository.browseEvents(
                                "Bengaluru",
                                Genre.ACTION,
                                Language.ENGLISH,
                                LocalDateTime.of(2026, 1, 15, 0, 0),
                                LocalDateTime.of(2026, 1, 16, 0, 0));

                assertThat(results).hasSize(1);
                assertThat(results.get(0).getEventName()).contains("Bengaluru");
        }

        private void persistShowGraph(
                        String city,
                        Genre genre,
                        Language language,
                        LocalDateTime showTime) {

                VenueEntity venue = BookingTestDataFactory.venue(null, city);
                entityManager.persist(venue);

                AuditoriumEntity auditorium = BookingTestDataFactory.auditorium(null, venue, null);
                entityManager.persist(auditorium);

                EventEntity event = BookingTestDataFactory.event(null, "Event-" + city, null);
                event.setEventType(EventType.MOVIE);
                entityManager.persist(event);

                ShowEntity show = BookingTestDataFactory.show(null, auditorium, event, 100L);
                show.setShowDateTime(showTime);
                show.setGenres(Set.of(genre));
                show.setLanguages(Set.of(language));
                show.setEvent(event);
                entityManager.persist(show);

                entityManager.flush();
        }
}
