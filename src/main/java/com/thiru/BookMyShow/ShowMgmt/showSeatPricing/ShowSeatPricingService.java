package com.thiru.BookMyShow.ShowMgmt.showSeatPricing;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import java.util.*;

import com.thiru.BookMyShow.exception.*;
import com.thiru.BookMyShow.userMgmt.UserEntity;
import com.thiru.BookMyShow.ShowMgmt.AuthorizationPolicy;
import com.thiru.BookMyShow.ShowMgmt.seatCategory.SeatCategoryEntity;
import com.thiru.BookMyShow.ShowMgmt.seatCategory.SeatCategoryRepository;
import com.thiru.BookMyShow.ShowMgmt.show.*;
import com.thiru.BookMyShow.ShowMgmt.showSeatPricing.DTO.*;
import com.thiru.BookMyShow.userMgmt.*;

@Service
@RequiredArgsConstructor
public class ShowSeatPricingService implements AuthorizationPolicy<ShowSeatPricingEntity, UserEntity> {

        private final ShowSeatPricingRepository showSeatPricingRepo;
        private final ShowRepository showRepo;
        private final SeatCategoryRepository seatCategoryRepo;
        private final UserRepository userRepo;

        @Override
        public void canCreate(UserEntity ue) {
                if (ue.getRole().equals(Role.ADMIN))
                        return;
                throw new AccessDeniedException("Only Admin can create...!");
        }

        @Override
        public void canUpdate(ShowSeatPricingEntity se, UserEntity ue) {
                return;
        }

        @Override
        public void canDelete(ShowSeatPricingEntity se, UserEntity ue) {
                if (!ue.getRole().equals(Role.ADMIN))
                        throw new AccessDeniedException("Only Admin can update...!");
                if (se.getShow().getEvent().getAdmin().getUserId().equals(ue.getUserId()))
                        return;
                throw new AccessDeniedException("You could update your auditoriums...!");
        }

        @Override
        public void canRead(ShowSeatPricingEntity se, UserEntity ue) {
                return;
        }

        @Transactional
        public Long createPricing(CreateShowSeatPricing request) {
                String userName = request.getUserName();
                UserEntity ue = userRepo.findByName(userName)
                                .orElseThrow(() -> new ResponseStatusException(
                                                HttpStatus.NOT_FOUND,
                                                "User not found: " + userName));
                this.canCreate(ue);
                if (showSeatPricingRepo.existsByShow_ShowIdAndSeatCategory_Id(
                                request.getShowId(),
                                request.getSeatCategoryId())) {
                        throw new IllegalStateException(
                                        "Pricing already exists for this show and seat category");
                }

                ShowEntity show = showRepo.findById(request.getShowId())
                                .orElseThrow(() -> new EntityNotFoundException("Show not found"));

                SeatCategoryEntity seatCategory = seatCategoryRepo.findById(request.getSeatCategoryId())
                                .orElseThrow(() -> new EntityNotFoundException(
                                                "Seat category not found"));

                ShowSeatPricingEntity pricing = ShowSeatPricingEntity.builder()
                                .show(show)
                                .seatCategory(seatCategory)
                                .price(request.getPrice())
                                .build();

                return showSeatPricingRepo.save(pricing).getId();
        }

        @Transactional
        public void deletePricing(Long pricingId, String userName) {
                UserEntity ue = userRepo.findByName(userName)
                                .orElseThrow(() -> new ResponseStatusException(
                                                HttpStatus.NOT_FOUND,
                                                "User not found: " + userName));

                ShowSeatPricingEntity se = showSeatPricingRepo.findById(pricingId)
                                .orElseThrow(() -> new EntityNotFoundException("Pricing id not found"));
                this.canDelete(se, ue);
                showSeatPricingRepo.delete(se);
        }

        public PricingReadResponse readPricingByShow(ReadShowSeatPricing request) {
                canRead(null, null);
                if (request.getShowId() == null) {
                        throw new IllegalArgumentException("showId is required");
                }

                // Optional: validate show existence
                if (!showRepo.existsById(request.getShowId())) {
                        throw new EntityNotFoundException(
                                        "Show not found with id: " + request.getShowId());
                }

                List<ShowSeatPricingEntity> pricingList = showSeatPricingRepo.findByShow_ShowId(request.getShowId());

                Map<String, Double> categoryPricing = new HashMap<>();

                for (ShowSeatPricingEntity pricing : pricingList) {
                        categoryPricing.put(
                                        pricing.getSeatCategory().getName(),
                                        pricing.getPrice().doubleValue());
                }

                return PricingReadResponse.builder()
                                .categoryPricing(categoryPricing)
                                .build();
        }
}
