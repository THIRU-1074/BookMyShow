package com.thiru.BookMyShow.ShowMgmt.venue;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
import com.thiru.BookMyShow.ShowMgmt.venue.DTO.CreateVenue;
import com.thiru.BookMyShow.ShowMgmt.venue.DTO.ReadVenue;
import com.thiru.BookMyShow.ShowMgmt.venue.DTO.VenueReadResponse;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.impl.DefaultClaims;

@WebMvcTest(controllers = VenueController.class)
@AutoConfigureMockMvc(addFilters = false)
class VenueControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockitoBean
    private VenueService venueService;

    private Authentication authentication;

    @BeforeEach
    void setUp() {
        authentication = buildAuthentication("admin");
    }

    @Test
    void createVenueInjectsUser() throws Exception {
        CreateVenue request = new CreateVenue();
        request.setCity("City");
        request.setPincode("560001");
        request.setAddressLine1("Main");
        request.setAddressLine2("Block");
        request.setLandmark("Landmark");

        mockMvc.perform(post("/venue/createVenue")
                .principal(authentication)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        ArgumentCaptor<CreateVenue> captor = ArgumentCaptor.forClass(CreateVenue.class);
        verify(venueService).create(captor.capture());
        assertThat(captor.getValue().getUserName()).isEqualTo("admin");
    }

    @Test
    void readVenueReturnsServiceResponse() throws Exception {
        when(venueService.read(org.mockito.ArgumentMatchers.any()))
                .thenReturn(List.of(VenueReadResponse.builder().venueId(1L).build()));

        mockMvc.perform(get("/venue/readvenue").principal(authentication).param("city", "City"))
                .andExpect(status().isOk());

        ArgumentCaptor<ReadVenue> captor = ArgumentCaptor.forClass(ReadVenue.class);
        verify(venueService).read(captor.capture());
        assertThat(captor.getValue().getCity()).isEqualTo("City");
    }

    private Authentication buildAuthentication(String username) {
        Claims claims = new DefaultClaims(Map.of());
        claims.setSubject(username);
        return new TestingAuthenticationToken(claims, null);
    }
}
