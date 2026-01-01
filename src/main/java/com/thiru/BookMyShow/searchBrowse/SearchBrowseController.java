package com.thiru.BookMyShow.searchBrowse;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.*;
import lombok.RequiredArgsConstructor;

import com.thiru.BookMyShow.searchBrowse.DTO.*;

@RestController
@RequestMapping("/filter")
@RequiredArgsConstructor
public class SearchBrowseController {
    private final SearchBrowseService searchBrowseService;

    @GetMapping("/browseEvent")
    public ResponseEntity<?> browse(
            @ModelAttribute BrowseEvent request) {

        return ResponseEntity.ok(searchBrowseService.browse(request));
    }

    @GetMapping("/viewShowDetail")
    public ResponseEntity<?> viewShow(
            @ModelAttribute ViewShowDetail request) {

        return ResponseEntity.ok(searchBrowseService.viewShow(request));
    }

    @GetMapping("/viewShowGroup")
    public ResponseEntity<?> viewShowGroup(
            @ModelAttribute ViewShowGroup request) {

        return ResponseEntity.ok(searchBrowseService.viewShowGroup(request));
    }
}
