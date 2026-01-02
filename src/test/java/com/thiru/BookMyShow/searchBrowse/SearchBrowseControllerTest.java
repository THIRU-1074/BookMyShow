package com.thiru.BookMyShow.searchBrowse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
// THESE ARE NEW (Spring Boot 4.x)
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc; // Check exact IDE suggestion
import org.springframework.test.context.bean.override.mockito.MockitoBean; // Replaces @MockBean
import org.springframework.test.web.servlet.MockMvc;

import com.thiru.BookMyShow.ShowMgmt.event.DTO.EventReadResponse;
import com.thiru.BookMyShow.ShowMgmt.show.DTO.ShowReadResponse;
import com.thiru.BookMyShow.searchBrowse.DTO.BrowseEvent;
import com.thiru.BookMyShow.searchBrowse.DTO.BrowseEventResponse;
import com.thiru.BookMyShow.searchBrowse.DTO.ViewShowDetail;
import com.thiru.BookMyShow.searchBrowse.DTO.ViewShowDetailResponse;
import com.thiru.BookMyShow.searchBrowse.DTO.ViewShowGroup;
import com.thiru.BookMyShow.searchBrowse.DTO.ViewShowGroupResponse;

@WebMvcTest(controllers = SearchBrowseController.class)
@AutoConfigureMockMvc(addFilters = false)
class SearchBrowseControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockitoBean
        private SearchBrowseService searchBrowseService;

        @Test
        void browseEndpointBindsQueryParams() throws Exception {
                when(searchBrowseService.browse(org.mockito.ArgumentMatchers.any()))
                                .thenReturn(BrowseEventResponse.builder()
                                                .events(List.of(EventReadResponse.builder().eventId(1L)
                                                                .eventName("Movie").build()))
                                                .build());

                mockMvc.perform(get("/filter/browseEvent")
                                .param("city", "Bengaluru")
                                .param("genre", "ACTION")
                                .param("language", "ENGLISH")
                                .param("date", "2026-01-10"))
                                .andExpect(status().isOk());

                ArgumentCaptor<BrowseEvent> captor = ArgumentCaptor.forClass(BrowseEvent.class);
                verify(searchBrowseService).browse(captor.capture());
                assertThat(captor.getValue().getCity()).isEqualTo("Bengaluru");
                assertThat(captor.getValue().getDate()).isEqualTo(LocalDate.of(2026, 1, 10));
        }

        @Test
        void viewShowDetailEndpointPassesShowId() throws Exception {
                when(searchBrowseService.viewShow(org.mockito.ArgumentMatchers.any()))
                                .thenReturn(ViewShowDetailResponse.builder()
                                                .show(ShowReadResponse.builder().showId(5L).build())
                                                .build());

                mockMvc.perform(get("/filter/viewShowDetail").param("showId", "5"))
                                .andExpect(status().isOk());

                ArgumentCaptor<ViewShowDetail> captor = ArgumentCaptor.forClass(ViewShowDetail.class);
                verify(searchBrowseService).viewShow(captor.capture());
                assertThat(captor.getValue().getShowId()).isEqualTo(5L);
        }

        @Test
        void viewShowGroupEndpointBindsDateAndVenue() throws Exception {
                when(searchBrowseService.viewShowGroup(org.mockito.ArgumentMatchers.any()))
                                .thenReturn(ViewShowGroupResponse.builder().shows(List.of()).build());

                mockMvc.perform(get("/filter/viewShowGroup")
                                .param("venueId", "22")
                                .param("date", "2026-01-15"))
                                .andExpect(status().isOk());

                ArgumentCaptor<ViewShowGroup> captor = ArgumentCaptor.forClass(ViewShowGroup.class);
                verify(searchBrowseService).viewShowGroup(captor.capture());
                assertThat(captor.getValue().getVenueId()).isEqualTo(22L);
                assertThat(captor.getValue().getDate()).isEqualTo(LocalDate.of(2026, 1, 15));
        }
}
