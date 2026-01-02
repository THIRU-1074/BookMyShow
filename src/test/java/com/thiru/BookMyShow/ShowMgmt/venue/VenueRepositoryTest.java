package com.thiru.BookMyShow.ShowMgmt.venue;

import static org.assertj.core.api.Assertions.assertThat;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import com.thiru.BookMyShow.ShowMgmt.venue.DTO.ReadVenue;

@DataJpaTest
class VenueRepositoryTest {

        @Autowired
        private VenueRepository venueRepository;

        @Test
        void findAllWithSpecificationFiltersByCity() {
                VenueEntity bangalore = VenueEntity.builder()
                                .city("Bengaluru")
                                .pincode("560001")
                                .addressLine1("Main")
                                .build();
                VenueEntity chennai = VenueEntity.builder()
                                .city("Chennai")
                                .pincode("600001")
                                .addressLine1("Anna")
                                .build();

                venueRepository.saveAll(List.of(bangalore, chennai));

                ReadVenue filter = ReadVenue.builder().city("Bengaluru").build();
                List<VenueEntity> venues = venueRepository.findAll(VenueService.withFilters(filter));

                assertThat(venues)
                                .singleElement()
                                .extracting(VenueEntity::getCity)
                                .isEqualTo("Bengaluru");
        }
}
