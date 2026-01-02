package com.thiru.BookMyShow.ShowMgmt.venue;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

import com.thiru.BookMyShow.ShowMgmt.venue.DTO.CreateVenue;
import com.thiru.BookMyShow.ShowMgmt.venue.DTO.ReadVenue;
import com.thiru.BookMyShow.ShowMgmt.venue.DTO.VenueReadResponse;
import com.thiru.BookMyShow.testutil.BookingTestDataFactory;
import com.thiru.BookMyShow.userMgmt.Role;
import com.thiru.BookMyShow.userMgmt.UserEntity;
import com.thiru.BookMyShow.userMgmt.UserRepository;

@ExtendWith(MockitoExtension.class)
class VenueServiceTest {

    @Mock
    private VenueRepository venueRepository;
    @Mock
    private UserRepository userRepository;

    private VenueService venueService;

    @BeforeEach
    void setUp() {
        venueService = new VenueService(venueRepository, userRepository);
    }

    @Test
    void createPersistsVenueWhenAdmin() {
        CreateVenue request = new CreateVenue();
        request.setUserName("admin");
        request.setCity("City");

        UserEntity admin = BookingTestDataFactory.user(1L, "admin", Role.ADMIN);
        when(userRepository.findByUserName("admin")).thenReturn(Optional.of(admin));

        venueService.create(request);

        ArgumentCaptor<VenueEntity> captor = ArgumentCaptor.forClass(VenueEntity.class);
        verify(venueRepository).save(captor.capture());
        assertThat(captor.getValue().getCity()).isEqualTo("City");
    }

    @Test
    void readWithVenueIdReturnsSingleResponse() {
        ReadVenue request = ReadVenue.builder().venueId(1L).build();
        VenueEntity venue = BookingTestDataFactory.venue(1L, "City");

        when(venueRepository.findById(1L)).thenReturn(Optional.of(venue));

        List<VenueReadResponse> response = venueService.read(request);

        assertThat(response).hasSize(1);
    }

    @Test
    void readWithoutFiltersThrowsIllegalArgument() {
        ReadVenue request = ReadVenue.builder().build();

        assertThatThrownBy(() -> venueService.read(request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void readWithFiltersAppliesSpecification() {
        ReadVenue request = ReadVenue.builder().city("City").build();
        when(venueRepository.findAll(any(Specification.class)))
                .thenReturn(List.of(BookingTestDataFactory.venue(1L, "City")));

        List<VenueReadResponse> response = venueService.read(request);

        assertThat(response).hasSize(1);
    }
}
