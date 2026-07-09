package com.warren.clob.controllers;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {
    @PostMapping("api/auth/login")
    public String login() {
        return "Login successful";
    }

    @PostMapping("api/auth/register")
    public String register() {
        return "Register successful";
    }
}
