package com.warren.clob.controllers;

import com.warren.clob.dto.AuthRequest;
import com.warren.clob.dto.AuthResponse;
import com.warren.clob.models.User;
import com.warren.clob.repos.UserRepo;
import com.warren.clob.security.JwtTokenProvider;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest loginRequest) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
        );
        User user = userRepo.findByEmail(loginRequest.getEmail())
                .orElseThrow(()->new RuntimeException("User not found"));
        String jwt = jwtTokenProvider.generateToken(user.getEmail(), user.getId());
        return ResponseEntity.ok(new AuthResponse(jwt, "Bearer", user.getId()));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        if (userRepo.findByEmail(user.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("Email already in use");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setCash(0);
        user.setBuyingPower(0);
        userRepo.save(user);
        return ResponseEntity.ok().body("User registered successfully");
    }
}
