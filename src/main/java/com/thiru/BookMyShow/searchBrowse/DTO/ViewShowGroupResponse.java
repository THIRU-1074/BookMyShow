package com.thiru.BookMyShow.searchBrowse.DTO;

import java.util.*;
import lombok.*;

@Getter
@Setter
@Builder
public class ViewShowGroupResponse {
    private List<ViewShowDetailResponse> shows;
}
