package com.thiru.BookMyShow.searchBrowse.DTO;

import com.thiru.BookMyShow.ShowMgmt.show.DTO.ShowReadResponse;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Setter
@Getter
public class ViewShowDetailResponse {
    ShowReadResponse show;
}
