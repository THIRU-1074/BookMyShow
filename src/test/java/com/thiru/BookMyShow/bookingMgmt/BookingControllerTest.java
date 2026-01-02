package com.thiru.BookMyShow.bookingMgmt;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Map;
// THESE ARE NEW (Spring Boot 4.x)
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc; // Check exact IDE suggestion
import org.springframework.test.context.bean.override.mockito.MockitoBean; // Replaces @MockBean
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thiru.BookMyShow.bookingMgmt.DTO.CreateBooking;
import com.thiru.BookMyShow.bookingMgmt.DTO.CreateBookings;
import com.thiru.BookMyShow.bookingMgmt.DTO.DeleteBookings;
import com.thiru.BookMyShow.bookingMgmt.DTO.ReadBookings;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.impl.DefaultClaims;

@WebMvcTest(controllers = BookingController.class)
@AutoConfigureMockMvc(addFilters = false)
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private BookingService bookingService;

    private Authentication authentication;

    @BeforeEach
    void init() {
        authentication = buildAuthentication("web-user");
    }

    @Test
    void createBookingsSetsUserFromToken() throws Exception {
        CreateBookings request = new CreateBookings();
        request.setBookings(List.of(createBooking(1L, 2L)));

        mockMvc.perform(post("/booking/createBookings")
                .principal(authentication)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        ArgumentCaptor<CreateBookings> captor = ArgumentCaptor.forClass(CreateBookings.class);
        verify(bookingService).createBookings(captor.capture());
        assertThat(captor.getValue().getUserName()).isEqualTo("web-user");
    }

    @Test
    void deleteSingleBookingDelegatesToService() throws Exception {
        mockMvc.perform(delete("/booking/{id}", 42L))
                .andExpect(status().isNoContent());

        verify(bookingService).deleteBooking(42L);
    }

    @Test
    void deleteBookingsPropagatesAuthenticatedUser() throws Exception {
        DeleteBookings request = new DeleteBookings();
        request.setBookingIds(new Long[] { 1L, 2L, 3L });

        mockMvc.perform(delete("/booking/deleteBookings")
                .principal(authentication)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNoContent());

        ArgumentCaptor<DeleteBookings> captor = ArgumentCaptor.forClass(DeleteBookings.class);
        verify(bookingService).deleteBookings(captor.capture());
        assertThat(captor.getValue().getUserName()).isEqualTo("web-user");
    }

    @Test
    void readBookingsUsesQueryParamsAndClaims() throws Exception {
        mockMvc.perform(get("/booking/readBookings")
                .principal(authentication)
                .param("showId", "99"))
                .andExpect(status().isCreated());

        ArgumentCaptor<ReadBookings> captor = ArgumentCaptor.forClass(ReadBookings.class);
        verify(bookingService).readBookings(captor.capture());
        assertThat(captor.getValue().getUserName()).isEqualTo("web-user");
        assertThat(captor.getValue().getShowId()).isEqualTo(99L);
    }

    private CreateBooking createBooking(long seatId, long showId) {
        CreateBooking booking = new CreateBooking();
        booking.setSeatId(seatId);
        booking.setShowId(showId);
        return booking;
    }

    private Authentication buildAuthentication(String username) {
        Claims claims = new DefaultClaims(Map.of());
        claims.setSubject(username);
        return new TestingAuthenticationToken(claims, null);
    }
}
