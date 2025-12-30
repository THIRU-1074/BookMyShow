package com.thiru.BookMyShow.userMgmt;

import com.thiru.BookMyShow.appSecurity.AuthType;

import lombok.*;

@Getter
@Setter
public class UserLoginRequestDTO {

    private String userName;

    // Used only for BASIC auth
    private String password;

    // Used only for JWT auth
    private String accessToken;

    private AuthType authType;
}
