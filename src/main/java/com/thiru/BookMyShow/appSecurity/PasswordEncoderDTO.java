package com.thiru.BookMyShow.appSecurity;

public interface PasswordEncoderDTO {

    String encode(String rawPassword);

    boolean matches(String rawPassword, String encodedPassword);
}
