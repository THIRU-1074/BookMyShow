package com.thiru.BookMyShow.ShowMgmt.seatCategory;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

@DataJpaTest
class SeatCategoryRepositoryTest {

    @Autowired
    private SeatCategoryRepository seatCategoryRepository;

    @Test
    void existsByNameReturnsTrueWhenPresent() {
        SeatCategoryEntity entity = SeatCategoryEntity.builder()
                .name("VIP")
                .description("desc")
                .build();
        seatCategoryRepository.save(entity);

        assertThat(seatCategoryRepository.existsByName("VIP")).isTrue();
    }
}
