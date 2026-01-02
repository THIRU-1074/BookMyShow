package com.thiru.BookMyShow.ShowMgmt.event;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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

import com.thiru.BookMyShow.ShowMgmt.event.DTO.CreateEvent;
import com.thiru.BookMyShow.ShowMgmt.event.DTO.DeleteEvent;
import com.thiru.BookMyShow.ShowMgmt.event.DTO.EventReadResponse;
import com.thiru.BookMyShow.ShowMgmt.event.DTO.ReadEvent;
import com.thiru.BookMyShow.ShowMgmt.event.DTO.UpdateEvent;
import com.thiru.BookMyShow.testutil.BookingTestDataFactory;
import com.thiru.BookMyShow.userMgmt.Role;
import com.thiru.BookMyShow.userMgmt.UserEntity;
import com.thiru.BookMyShow.userMgmt.UserRepository;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {

    @Mock
    private EventRepository eventRepository;
    @Mock
    private UserRepository userRepository;

    private EventService eventService;

    @BeforeEach
    void setUp() {
        eventService = new EventService(eventRepository, userRepository);
    }

    @Test
    void createPersistsEventWhenAdmin() {
        CreateEvent request = new CreateEvent();
        request.setUserName("admin");
        request.setEventName("Premiere");
        request.setEventType(EventType.MOVIE);

        UserEntity admin = BookingTestDataFactory.user(1L, "admin", Role.ADMIN);
        when(userRepository.findByUserName("admin")).thenReturn(Optional.of(admin));

        eventService.create(request);

        ArgumentCaptor<EventEntity> captor = ArgumentCaptor.forClass(EventEntity.class);
        verify(eventRepository).save(captor.capture());
        assertThat(captor.getValue().getEventName()).isEqualTo("Premiere");
    }

    @Test
    void updateThrowsWhenDifferentAdmin() {
        UpdateEvent request = new UpdateEvent();
        request.setUserName("other");
        request.setEventId(5L);
        request.setEventName("Updated");

        UserEntity otherAdmin = BookingTestDataFactory.user(2L, "other", Role.ADMIN);
        UserEntity owner = BookingTestDataFactory.user(1L, "owner", Role.ADMIN);
        EventEntity event = BookingTestDataFactory.event(5L, "Old", owner);
        event.setAdmin(owner);

        when(userRepository.findByUserName("other")).thenReturn(Optional.of(otherAdmin));
        when(eventRepository.findById(5L)).thenReturn(Optional.of(event));

        assertThatThrownBy(() -> eventService.update(request))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void deleteRemovesEventWhenOwned() {
        DeleteEvent request = new DeleteEvent();
        request.setUserName("admin");
        request.setEventId(6L);

        UserEntity admin = BookingTestDataFactory.user(1L, "admin", Role.ADMIN);
        EventEntity event = BookingTestDataFactory.event(6L, "Live", admin);
        event.setAdmin(admin);

        when(userRepository.findByUserName("admin")).thenReturn(Optional.of(admin));
        when(eventRepository.findById(6L)).thenReturn(Optional.of(event));

        eventService.delete(request);

        verify(eventRepository).delete(event);
    }

    @Test
    void readByEventIdReturnsProjection() {
        ReadEvent request = new ReadEvent();
        request.setEventId(1L);
        request.setUserName("admin");

        UserEntity admin = BookingTestDataFactory.user(1L, "admin", Role.ADMIN);
        EventEntity event = BookingTestDataFactory.event(1L, "Movie", admin);

        when(eventRepository.findByEventId(1L)).thenReturn(event);
        when(userRepository.findByUserName("admin")).thenReturn(Optional.of(admin));

        List<EventReadResponse> result = eventService.read(request);

        assertThat(result)
                .singleElement()
                .extracting(EventReadResponse::getEventId)
                .isEqualTo(1L);
    }
}
