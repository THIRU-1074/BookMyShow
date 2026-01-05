package com.thiru.BookMyShow.ShowMgmt.seatCategory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
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
import com.thiru.BookMyShow.ShowMgmt.seatCategory.DTO.CreateSeatCategory;
import com.thiru.BookMyShow.ShowMgmt.seatCategory.DTO.SeatCategoryResponse;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.impl.DefaultClaims;

@WebMvcTest(controllers = SeatCategoryController.class)
@AutoConfigureMockMvc(addFilters = false)
class SeatCategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockitoBean
    private SeatCategoryService seatCategoryService;

    private Authentication authentication;

    @BeforeEach
    void setUp() {
        authentication = buildAuthentication("admin");
    }

    @Test
    void createCategoryInjectsUser() throws Exception {
        CreateSeatCategory request = new CreateSeatCategory();
        request.setName("VIP");
        request.setDescription("desc");

        when(seatCategoryService.create(org.mockito.ArgumentMatchers.any()))
                .thenReturn(SeatCategoryResponse.builder().id(1L).name("VIP").build());

        mockMvc.perform(post("/seatCategory/createCategory")
                .principal(authentication)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        ArgumentCaptor<CreateSeatCategory> captor = ArgumentCaptor.forClass(CreateSeatCategory.class);
        verify(seatCategoryService).create(captor.capture());
        assertThat(captor.getValue().getUserName()).isEqualTo("admin");
    }

    private Authentication buildAuthentication(String username) {
        Claims claims = new DefaultClaims(Map.of());
        claims.setSubject(username);
        return new TestingAuthenticationToken(claims, null);
    }
}
