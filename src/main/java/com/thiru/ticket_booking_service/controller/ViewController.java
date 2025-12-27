package com.thiru.ticket_booking_service.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/")
public class ViewController {
    @GetMapping("/")
    public void viewHomePage(){
        
    }
    @GetMapping("/login")
    public String loginView() {
        return "Login page placeholder";
    }

    @GetMapping("/register")
    public String registerView() {
        return "Register page placeholder";
    }
}

