package com.thiru.BookMyShow.ShowMgmt.showSeatPricing.DTO;

import java.util.*;
import lombok.*;

@Getter
@Setter
@Builder
public class PricingReadResponse {
    private Map<String, Double> categoryPricing;
}
