package com.thiru.BookMyShow.userMgmt;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserSignupRequestDTO {

    @NotBlank
    private String name;

    @Email
    @NotBlank
    private String mailId;

    @NotBlank
    private String phoneNumber;

    @NotBlank
    private String role;

    private Role roleEnum;

    @NotBlank
    @Size(min = 6)
    private String password;
}
