package com.thiru.BookMyShow.ShowMgmt.showSeatPricing;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
import com.thiru.BookMyShow.ShowMgmt.showSeatPricing.DTO.CreateShowSeatPricing;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.impl.DefaultClaims;

@WebMvcTest(controllers = ShowSeatPricingController.class)
@AutoConfigureMockMvc(addFilters = false)
class ShowSeatPricingControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockitoBean
    private ShowSeatPricingService pricingService;

    private Authentication authentication;

    @BeforeEach
    void setUp() {
        authentication = buildAuthentication("admin");
    }

    @Test
    void createShowSeatPricingReturnsId() throws Exception {
        CreateShowSeatPricing request = new CreateShowSeatPricing();
        request.setShowId(1L);
        request.setSeatCategoryId(2L);
        request.setPrice(150.0);
        request.setUserName("admin");

        when(pricingService.createPricing(org.mockito.ArgumentMatchers.any())).thenReturn(10L);

        mockMvc.perform(post("/showSeatPricing/createShowSeatPricing")
                .principal(authentication)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        ArgumentCaptor<CreateShowSeatPricing> captor = ArgumentCaptor.forClass(CreateShowSeatPricing.class);
        verify(pricingService).createPricing(captor.capture());
        assertThat(captor.getValue().getShowId()).isEqualTo(1L);
    }

    @Test
    void deleteShowSeatPricingPassesUser() throws Exception {
        mockMvc.perform(delete("/showSeatPricing/deleteShowSeatPricing/{id}", 5L)
                .principal(authentication))
                .andExpect(status().isNoContent());

        verify(pricingService).deletePricing(5L, "admin");
    }

    private Authentication buildAuthentication(String username) {
        Claims claims = new DefaultClaims(Map.of());
        claims.setSubject(username);
        return new TestingAuthenticationToken(claims, null);
    }
}
