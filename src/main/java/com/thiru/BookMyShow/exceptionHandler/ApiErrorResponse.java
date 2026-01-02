package com.thiru.BookMyShow.exceptionHandler;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Builder
public class ApiErrorResponse {

    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String message;
    private String path;
}
