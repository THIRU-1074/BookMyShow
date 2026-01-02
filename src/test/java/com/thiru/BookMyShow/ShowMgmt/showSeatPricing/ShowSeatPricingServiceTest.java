package com.thiru.BookMyShow.ShowMgmt.showSeatPricing;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.thiru.BookMyShow.ShowMgmt.seatCategory.SeatCategoryEntity;
import com.thiru.BookMyShow.ShowMgmt.seatCategory.SeatCategoryRepository;
import com.thiru.BookMyShow.ShowMgmt.show.ShowEntity;
import com.thiru.BookMyShow.ShowMgmt.show.ShowRepository;
import com.thiru.BookMyShow.ShowMgmt.showSeatPricing.DTO.CreateShowSeatPricing;
import com.thiru.BookMyShow.ShowMgmt.showSeatPricing.DTO.PricingReadResponse;
import com.thiru.BookMyShow.ShowMgmt.showSeatPricing.DTO.ReadShowSeatPricing;
import com.thiru.BookMyShow.testutil.BookingTestDataFactory;
import com.thiru.BookMyShow.userMgmt.Role;
import com.thiru.BookMyShow.userMgmt.UserEntity;
import com.thiru.BookMyShow.userMgmt.UserRepository;

@ExtendWith(MockitoExtension.class)
class ShowSeatPricingServiceTest {

    @Mock
    private ShowSeatPricingRepository showSeatPricingRepository;
    @Mock
    private ShowRepository showRepository;
    @Mock
    private SeatCategoryRepository seatCategoryRepository;
    @Mock
    private UserRepository userRepository;

    private ShowSeatPricingService service;

    @BeforeEach
    void setUp() {
        service = new ShowSeatPricingService(
                showSeatPricingRepository,
                showRepository,
                seatCategoryRepository,
                userRepository);
    }

    @Test
    void createPricingPersistsEntity() {
        CreateShowSeatPricing request = new CreateShowSeatPricing();
        request.setShowId(1L);
        request.setSeatCategoryId(2L);
        request.setPrice(250.0);
        request.setUserName("admin");

        UserEntity admin = BookingTestDataFactory.user(1L, "admin", Role.ADMIN);
        ShowEntity show = BookingTestDataFactory.show(1L, null, BookingTestDataFactory.event(2L, "Event", admin), 10L);
        show.getEvent().setAdmin(admin);
        SeatCategoryEntity category = BookingTestDataFactory.seatCategory(2L, "VIP");

        when(userRepository.findByUserName("admin")).thenReturn(Optional.of(admin));
        when(showSeatPricingRepository.existsByShow_ShowIdAndSeatCategory_Id(1L, 2L)).thenReturn(false);
        when(showRepository.findById(1L)).thenReturn(Optional.of(show));
        when(seatCategoryRepository.findById(2L)).thenReturn(Optional.of(category));
        when(showSeatPricingRepository.save(any(ShowSeatPricingEntity.class)))
                .thenAnswer(invocation -> {
                    ShowSeatPricingEntity entity = invocation.getArgument(0);
                    entity.setShowSeatPricingId(100L);
                    return entity;
                });

        Long id = service.createPricing(request);

        assertThat(id).isEqualTo(100L);
        ArgumentCaptor<ShowSeatPricingEntity> captor = ArgumentCaptor.forClass(ShowSeatPricingEntity.class);
        verify(showSeatPricingRepository).save(captor.capture());
        assertThat(captor.getValue().getPrice()).isEqualTo(250.0);
    }

    @Test
    void deletePricingValidatesOwnership() {
        UserEntity admin = BookingTestDataFactory.user(1L, "admin", Role.ADMIN);
        ShowEntity show = BookingTestDataFactory.show(1L, null, BookingTestDataFactory.event(2L, "Event", admin), 10L);
        show.getEvent().setAdmin(admin);
        SeatCategoryEntity category = BookingTestDataFactory.seatCategory(2L, "VIP");
        ShowSeatPricingEntity pricing = BookingTestDataFactory.pricing(5L, show, category, 300.0);

        when(userRepository.findByUserName("admin")).thenReturn(Optional.of(admin));
        when(showSeatPricingRepository.findById(5L)).thenReturn(Optional.of(pricing));

        service.deletePricing(5L, "admin");

        verify(showSeatPricingRepository).delete(pricing);
    }

    @Test
    void readPricingByShowBuildsCategoryMap() {
        ReadShowSeatPricing request = new ReadShowSeatPricing();
        request.setShowId(1L);

        when(showRepository.existsById(1L)).thenReturn(true);
        SeatCategoryEntity vip = BookingTestDataFactory.seatCategory(2L, "VIP");
        ShowSeatPricingEntity pricing = BookingTestDataFactory.pricing(5L, null, vip, 450.0);
        when(showSeatPricingRepository.findByShow_ShowId(1L)).thenReturn(List.of(pricing));

        PricingReadResponse response = service.readPricingByShow(request);

        assertThat(response.getCategoryPricing())
                .containsEntry("VIP", 450.0);
    }
}
