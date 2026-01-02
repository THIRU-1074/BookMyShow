package com.thiru.BookMyShow.ShowMgmt.seat;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
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
import com.thiru.BookMyShow.ShowMgmt.seat.DTO.CreateSeats;
import com.thiru.BookMyShow.ShowMgmt.seat.DTO.ReadSeats;
import com.thiru.BookMyShow.ShowMgmt.seat.DTO.SeatReadResponse;
import com.thiru.BookMyShow.ShowMgmt.seat.DTO.UpdateSeats;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.impl.DefaultClaims;

@WebMvcTest(controllers = SeatController.class)
@AutoConfigureMockMvc(addFilters = false)
class SeatControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockitoBean
    private SeatService seatService;

    private Authentication authentication;

    @BeforeEach
    void setUp() {
        authentication = buildAuthentication("admin-user");
    }

    @Test
    void createSeatInjectsAuthenticatedUser() throws Exception {
        CreateSeats request = new CreateSeats();
        request.setAuditoriumId(1L);
        com.thiru.BookMyShow.ShowMgmt.seat.DTO.CreateSeat seat = new com.thiru.BookMyShow.ShowMgmt.seat.DTO.CreateSeat();
        seat.setRow(1);
        seat.setCol(1);
        seat.setStance(1);
        request.setSeats(new com.thiru.BookMyShow.ShowMgmt.seat.DTO.CreateSeat[] { seat });

        mockMvc.perform(post("/seat/createseat")
                .principal(authentication)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        ArgumentCaptor<CreateSeats> captor = ArgumentCaptor.forClass(CreateSeats.class);
        verify(seatService).create(captor.capture());
        assertThat(captor.getValue().getUserName()).isEqualTo("admin-user");
    }

    @Test
    void updateSeatDelegatesToService() throws Exception {
        UpdateSeats request = new UpdateSeats();
        request.setAuditoriumId(1L);
        com.thiru.BookMyShow.ShowMgmt.seat.DTO.UpdateSeat seat = new com.thiru.BookMyShow.ShowMgmt.seat.DTO.UpdateSeat();
        seat.setSeatId(1L);
        request.setSeats(new com.thiru.BookMyShow.ShowMgmt.seat.DTO.UpdateSeat[] { seat });

        mockMvc.perform(patch("/seat/updateSeat")
                .principal(authentication)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNoContent());

        ArgumentCaptor<UpdateSeats> captor = ArgumentCaptor.forClass(UpdateSeats.class);
        verify(seatService).update(captor.capture());
        assertThat(captor.getValue().getUserName()).isEqualTo("admin-user");
    }

    @Test
    void readSeatReturnsServiceResponse() throws Exception {
        when(seatService.read(org.mockito.ArgumentMatchers.any()))
                .thenReturn(List.of(SeatReadResponse.builder().seatId(1L).seatNo("A1").build()));

        mockMvc.perform(get("/seat/readseat")
                .principal(authentication)
                .param("auditoriumId", "1"))
                .andExpect(status().isOk());

        ArgumentCaptor<ReadSeats> captor = ArgumentCaptor.forClass(ReadSeats.class);
        verify(seatService).read(captor.capture());
        assertThat(captor.getValue().getAuditoriumId()).isEqualTo(1L);
    }

    private Authentication buildAuthentication(String username) {
        Claims claims = new DefaultClaims(Map.of());
        claims.setSubject(username);
        return new TestingAuthenticationToken(claims, null);
    }
}
