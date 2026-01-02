package com.thiru.BookMyShow.ShowMgmt.showSeat;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
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
import com.thiru.BookMyShow.ShowMgmt.showSeat.DTO.ReadShowSeat;
import com.thiru.BookMyShow.ShowMgmt.showSeat.DTO.ShowSeatReadResponse;
import com.thiru.BookMyShow.ShowMgmt.showSeat.DTO.UpdateShowSeats;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.impl.DefaultClaims;

@WebMvcTest(controllers = ShowSeatController.class)
@AutoConfigureMockMvc(addFilters = false)
class ShowSeatControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockitoBean
    private ShowSeatService showSeatService;

    private Authentication authentication;

    @BeforeEach
    void setUp() {
        authentication = buildAuthentication("admin");
    }

    @Test
    void updateShowSeatInjectsUser() throws Exception {
        UpdateShowSeats request = UpdateShowSeats.builder()
                .showId(1L)
                .seats(List.of())
                .build();

        mockMvc.perform(patch("/showSeat/updateShowSeat")
                .principal(authentication)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNoContent());

        ArgumentCaptor<UpdateShowSeats> captor = ArgumentCaptor.forClass(UpdateShowSeats.class);
        verify(showSeatService).update(captor.capture());
        assertThat(captor.getValue().getUserName()).isEqualTo("admin");
    }

    @Test
    void readShowSeatReturnsServiceResponse() throws Exception {
        when(showSeatService.read(org.mockito.ArgumentMatchers.any()))
                .thenReturn(List.of(ShowSeatReadResponse.builder().showSeatId(1L).build()));

        mockMvc.perform(get("/showSeat/readShowSeat")
                .principal(authentication)
                .param("showId", "1"))
                .andExpect(status().isOk());

        ArgumentCaptor<ReadShowSeat> captor = ArgumentCaptor.forClass(ReadShowSeat.class);
        verify(showSeatService).read(captor.capture());
        assertThat(captor.getValue().getShowId()).isEqualTo(1L);
    }

    private Authentication buildAuthentication(String username) {
        Claims claims = new DefaultClaims(Map.of());
        claims.setSubject(username);
        return new TestingAuthenticationToken(claims, null);
    }
}
