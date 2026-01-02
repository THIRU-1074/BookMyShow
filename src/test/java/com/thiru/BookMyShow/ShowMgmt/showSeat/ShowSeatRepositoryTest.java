package com.thiru.BookMyShow.ShowMgmt.showSeat;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import com.thiru.BookMyShow.ShowMgmt.auditorium.AuditoriumEntity;
import com.thiru.BookMyShow.ShowMgmt.seat.SeatEntity;
import com.thiru.BookMyShow.ShowMgmt.seatCategory.SeatCategoryEntity;
import com.thiru.BookMyShow.ShowMgmt.show.ShowEntity;
import com.thiru.BookMyShow.testutil.BookingTestDataFactory;
import com.thiru.BookMyShow.userMgmt.Role;
import com.thiru.BookMyShow.userMgmt.UserEntity;

@DataJpaTest
class ShowSeatRepositoryTest {

    @Autowired
    private ShowSeatRepository showSeatRepository;
    @Autowired
    private TestEntityManager entityManager;

    @Test
    void existsByShowIdDetectsPersistedSeats() {
        UserEntity admin = BookingTestDataFactory.user(null, "admin", Role.ADMIN);
        entityManager.persist(admin);
        var venue = BookingTestDataFactory.venue(null, "City");
        entityManager.persist(venue);
        AuditoriumEntity auditorium = BookingTestDataFactory.auditorium(null, venue, admin);
        entityManager.persist(auditorium);
        ShowEntity show = BookingTestDataFactory.show(null, auditorium,
                BookingTestDataFactory.event(null, "Event", admin), 10L);
        show.getEvent().setAdmin(admin);
        entityManager.persist(show);
        SeatEntity seat = BookingTestDataFactory.seat(null, auditorium, "A1");
        entityManager.persist(seat);
        SeatCategoryEntity category = BookingTestDataFactory.seatCategory(null, "REGULAR");
        entityManager.persist(category);
        ShowSeatEntity showSeat = BookingTestDataFactory.showSeat(null, show, seat, category,
                SeatAvailabilityStatus.AVAILABLE);
        entityManager.persist(showSeat);
        entityManager.flush();

        assertThat(showSeatRepository.existsByShow_ShowId(show.getShowId())).isTrue();
    }
}
