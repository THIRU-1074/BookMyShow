package com.thiru.BookMyShow.ShowMgmt.event;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
// THESE ARE NEW (Spring Boot 4.x)
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc; // Check exact IDE suggestion
import org.springframework.test.context.bean.override.mockito.MockitoBean; // Replaces @MockBean
import org.springframework.http.MediaType;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thiru.BookMyShow.ShowMgmt.event.DTO.CreateEvent;
import com.thiru.BookMyShow.ShowMgmt.event.DTO.DeleteEvent;
import com.thiru.BookMyShow.ShowMgmt.event.DTO.EventReadResponse;
import com.thiru.BookMyShow.ShowMgmt.event.DTO.ReadEvent;
import com.thiru.BookMyShow.ShowMgmt.event.DTO.UpdateEvent;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.impl.DefaultClaims;

@WebMvcTest(controllers = EventController.class)
@AutoConfigureMockMvc(addFilters = false)
class EventControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockitoBean
    private EventService eventService;

    private Authentication authentication;

    @BeforeEach
    void setUp() {
        authentication = buildAuthentication("admin");
    }

    @Test
    void createEventInjectsUser() throws Exception {
        CreateEvent request = new CreateEvent();
        request.setEventName("Premiere");

        mockMvc.perform(post("/event/createEvent")
                .principal(authentication)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        ArgumentCaptor<CreateEvent> captor = ArgumentCaptor.forClass(CreateEvent.class);
        verify(eventService).create(captor.capture());
        assertThat(captor.getValue().getUserName()).isEqualTo("admin");
    }

    @Test
    void updateEventDelegates() throws Exception {
        UpdateEvent request = new UpdateEvent();
        request.setEventId(1L);

        mockMvc.perform(patch("/event/updateevent")
                .principal(authentication)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNoContent());

        ArgumentCaptor<UpdateEvent> captor = ArgumentCaptor.forClass(UpdateEvent.class);
        verify(eventService).update(captor.capture());
        assertThat(captor.getValue().getUserName()).isEqualTo("admin");
    }

    @Test
    void deleteEventBindsBody() throws Exception {
        DeleteEvent request = new DeleteEvent();
        request.setEventId(5L);

        mockMvc.perform(delete("/event/deleteEvent")
                .principal(authentication)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNoContent());

        ArgumentCaptor<DeleteEvent> captor = ArgumentCaptor.forClass(DeleteEvent.class);
        verify(eventService).delete(captor.capture());
        assertThat(captor.getValue().getUserName()).isEqualTo("admin");
    }

    @Test
    void readEventUsesServiceResult() throws Exception {
        when(eventService.read(org.mockito.ArgumentMatchers.any()))
                .thenReturn(List.of(EventReadResponse.builder().eventId(1L).build()));

        mockMvc.perform(get("/event/readEvent")
                .principal(authentication)
                .param("eventId", "1"))
                .andExpect(status().isOk());

        ArgumentCaptor<ReadEvent> captor = ArgumentCaptor.forClass(ReadEvent.class);
        verify(eventService).read(captor.capture());
        assertThat(captor.getValue().getEventId()).isEqualTo(1L);
        assertThat(captor.getValue().getUserName()).isEqualTo("admin");
    }

    private Authentication buildAuthentication(String username) {
        Claims claims = new DefaultClaims(Map.of());
        claims.setSubject(username);
        return new TestingAuthenticationToken(claims, null);
    }
}
