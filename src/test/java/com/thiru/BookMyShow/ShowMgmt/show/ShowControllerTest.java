package com.thiru.BookMyShow.ShowMgmt.show;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import com.thiru.BookMyShow.ShowMgmt.show.DTO.CreateShow;
import com.thiru.BookMyShow.ShowMgmt.show.DTO.DeleteShow;
import com.thiru.BookMyShow.ShowMgmt.show.DTO.ReadShow;
import com.thiru.BookMyShow.ShowMgmt.show.DTO.ShowReadResponse;
import com.thiru.BookMyShow.ShowMgmt.show.DTO.UpdateShow;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.impl.DefaultClaims;

@WebMvcTest(controllers = ShowController.class)
@AutoConfigureMockMvc(addFilters = false)
class ShowControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockitoBean
    private ShowService showService;

    private Authentication authentication;

    @BeforeEach
    void setUp() {
        authentication = buildAuthentication("admin");
    }

    @Test
    void createShowInjectsUser() throws Exception {
        CreateShow request = new CreateShow();
        request.setAuditoriumId(1L);
        request.setEventId(2L);
        request.setShowName("Evening");
        request.setShowDateTime(LocalDateTime.now());
        request.setDurationMinutes(100);
        request.setGenres(Set.of(Genre.ACTION));
        request.setLanguages(Set.of(Language.ENGLISH));

        mockMvc.perform(post("/show/createShow")
                .principal(authentication)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        ArgumentCaptor<CreateShow> captor = ArgumentCaptor.forClass(CreateShow.class);
        verify(showService).create(captor.capture());
        assertThat(captor.getValue().getUserName()).isEqualTo("admin");
    }

    @Test
    void updateShowDelegatesToService() throws Exception {
        UpdateShow request = new UpdateShow();
        request.setShowId(4L);

        mockMvc.perform(patch("/show/updateShow")
                .principal(authentication)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNoContent());

        ArgumentCaptor<UpdateShow> captor = ArgumentCaptor.forClass(UpdateShow.class);
        verify(showService).update(captor.capture());
        assertThat(captor.getValue().getUserName()).isEqualTo("admin");
    }

    @Test
    void deleteShowBindsRequestBody() throws Exception {
        DeleteShow request = new DeleteShow();
        request.setShowId(9L);

        mockMvc.perform(delete("/show/deleteShow")
                .principal(authentication)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNoContent());

        ArgumentCaptor<DeleteShow> captor = ArgumentCaptor.forClass(DeleteShow.class);
        verify(showService).delete(captor.capture());
        assertThat(captor.getValue().getUserName()).isEqualTo("admin");
    }

    @Test
    void readShowReturnsServiceResponse() throws Exception {
        when(showService.read(org.mockito.ArgumentMatchers.any()))
                .thenReturn(List.of(ShowReadResponse.builder().showId(1L).build()));

        mockMvc.perform(get("/show/readShow")
                .principal(authentication)
                .param("eventId", "5"))
                .andExpect(status().isOk());

        ArgumentCaptor<ReadShow> captor = ArgumentCaptor.forClass(ReadShow.class);
        verify(showService).read(captor.capture());
        assertThat(captor.getValue().getUserName()).isEqualTo("admin");
        assertThat(captor.getValue().getEventId()).isEqualTo(5L);
    }

    private Authentication buildAuthentication(String username) {
        Claims claims = new DefaultClaims(Map.of());
        claims.setSubject(username);
        return new TestingAuthenticationToken(claims, null);
    }
}
