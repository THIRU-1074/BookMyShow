package com.thiru.BookMyShow.ShowMgmt.auditorium;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import com.thiru.BookMyShow.ShowMgmt.venue.VenueEntity;
import com.thiru.BookMyShow.testutil.BookingTestDataFactory;
import com.thiru.BookMyShow.userMgmt.Role;
import com.thiru.BookMyShow.userMgmt.UserEntity;

@DataJpaTest
class AuditoriumRepositoryTest {

        @Autowired
        private AuditoriumRepository auditoriumRepository;
        @Autowired
        private TestEntityManager entityManager;

        @Test
        void findByAdminAndVenueFiltersCorrectly() {
                UserEntity admin = BookingTestDataFactory.user(null, "admin", Role.ADMIN);
                entityManager.persist(admin);
                UserEntity otherAdmin = BookingTestDataFactory.user(null, "other", Role.ADMIN);
                entityManager.persist(otherAdmin);

                VenueEntity venue1 = BookingTestDataFactory.venue(null, "Bengaluru");
                VenueEntity venue2 = BookingTestDataFactory.venue(null, "Hyderabad");
                entityManager.persist(venue1);
                entityManager.persist(venue2);

                entityManager.persist(AuditoriumEntity.builder()
                                .auditoriumName("Hall A")
                                .admin(admin)
                                .venue(venue1)
                                .build());
                entityManager.persist(AuditoriumEntity.builder()
                                .auditoriumName("Hall B")
                                .admin(otherAdmin)
                                .venue(venue1)
                                .build());
                entityManager.persist(AuditoriumEntity.builder()
                                .auditoriumName("Hall C")
                                .admin(admin)
                                .venue(venue2)
                                .build());
                entityManager.flush();

                List<AuditoriumEntity> result = auditoriumRepository.findByAdminAndVenue_VenueId(admin,
                                venue1.getVenueId());

                assertThat(result)
                                .singleElement()
                                .extracting(AuditoriumEntity::getAuditoriumName)
                                .isEqualTo("Hall A");
        }
}
