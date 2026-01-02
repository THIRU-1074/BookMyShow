package com.thiru.BookMyShow.ShowMgmt.showSeatPricing;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import com.thiru.BookMyShow.ShowMgmt.seatCategory.SeatCategoryEntity;
import com.thiru.BookMyShow.ShowMgmt.show.ShowEntity;
import com.thiru.BookMyShow.testutil.BookingTestDataFactory;
import com.thiru.BookMyShow.userMgmt.Role;
import com.thiru.BookMyShow.userMgmt.UserEntity;

@DataJpaTest
class ShowSeatPricingRepositoryTest {

    @Autowired
    private ShowSeatPricingRepository repository;
    @Autowired
    private TestEntityManager entityManager;

    @Test
    void findByShowReturnsPricingEntries() {
        UserEntity admin = BookingTestDataFactory.user(null, "admin", Role.ADMIN);
        entityManager.persist(admin);

        ShowEntity show = BookingTestDataFactory.show(null, null,
                BookingTestDataFactory.event(null, "Event", admin), 10L);
        show.getEvent().setAdmin(admin);
        entityManager.persist(show);

        SeatCategoryEntity vip = BookingTestDataFactory.seatCategory(null, "VIP");
        entityManager.persist(vip);

        ShowSeatPricingEntity pricing = BookingTestDataFactory.pricing(null, show, vip, 350.0);
        entityManager.persist(pricing);
        entityManager.flush();

        List<ShowSeatPricingEntity> results = repository.findByShow_ShowId(show.getShowId());

        assertThat(results)
                .singleElement()
                .extracting(ShowSeatPricingEntity::getPrice)
                .isEqualTo(350.0);
    }
}
