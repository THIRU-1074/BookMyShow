package com.thiru.BookMyShow.userMgmt.DTO;

import com.thiru.BookMyShow.appSecurity.AuthType;

import lombok.*;

@Getter
@Setter
public class UserLoginRequestDTO {

    private String userName;

    // In Bearer its RefreshToken
    private String credential;

    private AuthType authType;
}
