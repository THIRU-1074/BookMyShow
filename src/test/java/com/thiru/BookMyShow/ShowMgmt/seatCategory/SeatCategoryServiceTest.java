package com.thiru.BookMyShow.ShowMgmt.seatCategory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import com.thiru.BookMyShow.ShowMgmt.seatCategory.DTO.CreateSeatCategoryRequest;
import com.thiru.BookMyShow.ShowMgmt.seatCategory.DTO.SeatCategoryResponse;
import com.thiru.BookMyShow.testutil.BookingTestDataFactory;
import com.thiru.BookMyShow.userMgmt.Role;
import com.thiru.BookMyShow.userMgmt.UserEntity;
import com.thiru.BookMyShow.userMgmt.UserRepository;

@ExtendWith(MockitoExtension.class)
class SeatCategoryServiceTest {

    @Mock
    private SeatCategoryRepository seatCategoryRepository;
    @Mock
    private UserRepository userRepository;

    private SeatCategoryService seatCategoryService;

    @BeforeEach
    void setUp() {
        seatCategoryService = new SeatCategoryService(seatCategoryRepository, userRepository);
    }

    @Test
    void createPersistsUpperCaseName() {
        CreateSeatCategoryRequest request = new CreateSeatCategoryRequest();
        request.setName("vip");
        request.setDescription("desc");
        request.setUserName("admin");

        UserEntity admin = BookingTestDataFactory.user(1L, "admin", Role.ADMIN);
        when(userRepository.findByUserName("admin")).thenReturn(Optional.of(admin));
        when(seatCategoryRepository.save(org.mockito.ArgumentMatchers.any()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        SeatCategoryResponse response = seatCategoryService.create(request);

        ArgumentCaptor<SeatCategoryEntity> captor = ArgumentCaptor.forClass(SeatCategoryEntity.class);
        verify(seatCategoryRepository).save(captor.capture());
        assertThat(captor.getValue().getName()).isEqualTo("VIP");
        assertThat(response.getName()).isEqualTo("VIP");
    }

    @Test
    void createThrowsWhenCategoryExists() {
        CreateSeatCategoryRequest request = new CreateSeatCategoryRequest();
        request.setName("VIP");
        request.setUserName("admin");

        UserEntity admin = BookingTestDataFactory.user(1L, "admin", Role.ADMIN);
        when(userRepository.findByUserName("admin")).thenReturn(Optional.of(admin));
        when(seatCategoryRepository.existsByName("VIP")).thenReturn(true);

        assertThatThrownBy(() -> seatCategoryService.create(request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void nonAdminCannotCreate() {
        CreateSeatCategoryRequest request = new CreateSeatCategoryRequest();
        request.setName("VIP");
        request.setUserName("user");

        UserEntity regular = BookingTestDataFactory.user(1L, "user", Role.USER);
        when(userRepository.findByUserName("user")).thenReturn(Optional.of(regular));

        assertThatThrownBy(() -> seatCategoryService.create(request))
                .isInstanceOf(AccessDeniedException.class);
    }
}
