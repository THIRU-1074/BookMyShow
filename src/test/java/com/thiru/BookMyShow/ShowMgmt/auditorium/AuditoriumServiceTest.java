package com.thiru.BookMyShow.ShowMgmt.auditorium;

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
import org.springframework.security.access.AccessDeniedException;

import com.thiru.BookMyShow.ShowMgmt.auditorium.DTO.AuditoriumReadResponse;
import com.thiru.BookMyShow.ShowMgmt.auditorium.DTO.createAuditorium;
import com.thiru.BookMyShow.ShowMgmt.auditorium.DTO.readAuditorium;
import com.thiru.BookMyShow.ShowMgmt.auditorium.DTO.updateAuditorium;
import com.thiru.BookMyShow.ShowMgmt.venue.VenueEntity;
import com.thiru.BookMyShow.ShowMgmt.venue.VenueRepository;
import com.thiru.BookMyShow.testutil.BookingTestDataFactory;
import com.thiru.BookMyShow.userMgmt.Role;
import com.thiru.BookMyShow.userMgmt.UserEntity;
import com.thiru.BookMyShow.userMgmt.UserRepository;

@ExtendWith(MockitoExtension.class)
class AuditoriumServiceTest {

    @Mock
    private AuditoriumRepository auditoriumRepository;
    @Mock
    private VenueRepository venueRepository;
    @Mock
    private UserRepository userRepository;

    private AuditoriumService service;

    @BeforeEach
    void setUp() {
        service = new AuditoriumService(auditoriumRepository, venueRepository, userRepository);
    }

    @Test
    void createAuditoriumPersistsWhenAdminUser() {
        createAuditorium request = new createAuditorium();
        request.setAuditoriumName("IMAX");
        request.setVenueId(55L);
        request.setUserName("admin-user");

        UserEntity admin = BookingTestDataFactory.user(1L, "admin-user", Role.ADMIN);
        VenueEntity venue = BookingTestDataFactory.venue(55L, "Bengaluru");

        when(userRepository.findByUserName("admin-user")).thenReturn(Optional.of(admin));
        when(venueRepository.findById(55L)).thenReturn(Optional.of(venue));
        when(auditoriumRepository.save(any(AuditoriumEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        service.create(request);

        ArgumentCaptor<AuditoriumEntity> captor = ArgumentCaptor.forClass(AuditoriumEntity.class);
        verify(auditoriumRepository).save(captor.capture());
        assertThat(captor.getValue().getAuditoriumName()).isEqualTo("IMAX");
        assertThat(captor.getValue().getVenue()).isEqualTo(venue);
    }

    @Test
    void updateAuditoriumThrowsWhenAdminDoesNotOwnResource() {
        updateAuditorium request = new updateAuditorium();
        request.setAuditoriumId(9L);
        request.setUserName("other-admin");
        request.setAuditoriumName("New Name");

        UserEntity otherAdmin = BookingTestDataFactory.user(2L, "other-admin", Role.ADMIN);
        UserEntity originalAdmin = BookingTestDataFactory.user(1L, "owner-admin", Role.ADMIN);
        AuditoriumEntity entity = AuditoriumEntity.builder()
                .auditoriumId(9L)
                .auditoriumName("Existing")
                .venue(BookingTestDataFactory.venue(1L, "City"))
                .admin(originalAdmin)
                .build();

        when(userRepository.findByUserName("other-admin")).thenReturn(Optional.of(otherAdmin));
        when(auditoriumRepository.findById(9L)).thenReturn(Optional.of(entity));

        assertThatThrownBy(() -> service.update(request))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void readAuditoriumsFiltersByVenueAndUser() {
        readAuditorium request = new readAuditorium();
        request.setUserName("admin-user");
        request.setVenueId(42L);

        UserEntity admin = BookingTestDataFactory.user(1L, "admin-user", Role.ADMIN);
        VenueEntity venue = BookingTestDataFactory.venue(42L, "Hyderabad");
        AuditoriumEntity auditorium = AuditoriumEntity.builder()
                .auditoriumId(99L)
                .auditoriumName("Stage 1")
                .venue(venue)
                .admin(admin)
                .build();

        when(userRepository.findByUserName("admin-user")).thenReturn(Optional.of(admin));
        when(auditoriumRepository.findByAdminAndVenue_VenueId(admin, 42L))
                .thenReturn(List.of(auditorium));

        List<AuditoriumReadResponse> result = service.read(request);

        assertThat(result)
                .singleElement()
                .extracting(AuditoriumReadResponse::getAuditoriumId)
                .isEqualTo(99L);
    }
}
