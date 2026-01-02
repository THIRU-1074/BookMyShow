package com.thiru.BookMyShow.ShowMgmt.auditorium;

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
import com.thiru.BookMyShow.ShowMgmt.auditorium.DTO.AuditoriumReadResponse;
import com.thiru.BookMyShow.ShowMgmt.auditorium.DTO.createAuditorium;
import com.thiru.BookMyShow.ShowMgmt.auditorium.DTO.deleteAuditorium;
import com.thiru.BookMyShow.ShowMgmt.auditorium.DTO.readAuditorium;
import com.thiru.BookMyShow.ShowMgmt.auditorium.DTO.updateAuditorium;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.impl.DefaultClaims;

@WebMvcTest(controllers = AuditoriumController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuditoriumControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockitoBean
    private AuditoriumService auditoriumService;

    private Authentication authentication;

    @BeforeEach
    void setUp() {
        authentication = buildAuthentication("admin-user");
    }

    @Test
    void createAuditoriumInjectsAuthenticatedUser() throws Exception {
        createAuditorium request = new createAuditorium();
        request.setAuditoriumName("Hall 1");
        request.setVenueId(11L);

        mockMvc.perform(post("/auditorium/createAuditorium")
                .principal(authentication)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        ArgumentCaptor<createAuditorium> captor = ArgumentCaptor.forClass(createAuditorium.class);
        verify(auditoriumService).create(captor.capture());
        assertThat(captor.getValue().getUserName()).isEqualTo("admin-user");
    }

    @Test
    void updateAuditoriumBindsBody() throws Exception {
        updateAuditorium request = new updateAuditorium();
        request.setAuditoriumId(9L);
        request.setAuditoriumName("Stage");

        mockMvc.perform(patch("/auditorium/updateAuditorium")
                .principal(authentication)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNoContent());

        ArgumentCaptor<updateAuditorium> captor = ArgumentCaptor.forClass(updateAuditorium.class);
        verify(auditoriumService).update(captor.capture());
        assertThat(captor.getValue().getUserName()).isEqualTo("admin-user");
    }

    @Test
    void deleteAuditoriumDelegatesToService() throws Exception {
        deleteAuditorium request = new deleteAuditorium();
        request.setAuditoriumId(5L);

        mockMvc.perform(delete("/auditorium/deleteAuditorium")
                .principal(authentication)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNoContent());

        ArgumentCaptor<deleteAuditorium> captor = ArgumentCaptor.forClass(deleteAuditorium.class);
        verify(auditoriumService).delete(captor.capture());
        assertThat(captor.getValue().getUserName()).isEqualTo("admin-user");
    }

    @Test
    void readAuditoriumsPassesQueryParams() throws Exception {
        when(auditoriumService.read(org.mockito.ArgumentMatchers.any()))
                .thenReturn(List.of(AuditoriumReadResponse.builder().auditoriumId(1L).build()));

        mockMvc.perform(get("/auditorium/readAuditorium")
                .principal(authentication)
                .param("venueId", "22"))
                .andExpect(status().isOk());

        ArgumentCaptor<readAuditorium> captor = ArgumentCaptor.forClass(readAuditorium.class);
        verify(auditoriumService).read(captor.capture());
        assertThat(captor.getValue().getUserName()).isEqualTo("admin-user");
        assertThat(captor.getValue().getVenueId()).isEqualTo(22L);
    }

    private Authentication buildAuthentication(String username) {
        Claims claims = new DefaultClaims(Map.of());
        claims.setSubject(username);
        return new TestingAuthenticationToken(claims, null);
    }
}
