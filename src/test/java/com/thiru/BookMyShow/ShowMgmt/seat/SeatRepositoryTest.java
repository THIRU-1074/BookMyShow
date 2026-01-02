package com.thiru.BookMyShow.ShowMgmt.seat;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import com.thiru.BookMyShow.ShowMgmt.auditorium.AuditoriumEntity;
import com.thiru.BookMyShow.testutil.BookingTestDataFactory;
import com.thiru.BookMyShow.userMgmt.Role;
import com.thiru.BookMyShow.userMgmt.UserEntity;

@DataJpaTest
class SeatRepositoryTest {

        @Autowired
        private SeatRepository seatRepository;
        @Autowired
        private TestEntityManager entityManager;

        @Test
        void findBySeatIdsAndAuditoriumReturnsOnlyMatches() {
                UserEntity admin = BookingTestDataFactory.user(null, "admin", Role.ADMIN);
                entityManager.persist(admin);
                AuditoriumEntity auditorium = BookingTestDataFactory.auditorium(null,
                                BookingTestDataFactory.venue(null, "City"), admin);
                entityManager.persist(auditorium);

                SeatEntity seat1 = SeatEntity.builder()
                                .seatNo("A1")
                                .row(1)
                                .col(1)
                                .stance(1)
                                .auditorium(auditorium)
                                .build();
                SeatEntity seat2 = SeatEntity.builder()
                                .seatNo("A2")
                                .row(1)
                                .col(2)
                                .stance(1)
                                .auditorium(auditorium)
                                .build();
                entityManager.persist(seat1);
                entityManager.persist(seat2);
                entityManager.flush();

                List<SeatEntity> seats = seatRepository.findBySeatIdInAndAuditorium_AuditoriumId(
                                List.of(seat1.getSeatId()),
                                auditorium.getAuditoriumId());

                assertThat(seats)
                                .singleElement()
                                .extracting(SeatEntity::getSeatNo)
                                .isEqualTo("A1");
        }
}
