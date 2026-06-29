package com.warren.clob.controllers;

import com.warren.clob.models.User;
import com.warren.clob.services.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
@AllArgsConstructor
public class UserController {
    final private UserService userService;

    @PostMapping("api/user/add")
    public ResponseEntity<User> createUser(@RequestBody User incomingUser) {
        User createdUser = userService.createUser(incomingUser);
        return ResponseEntity.ok(createdUser);
    }
}
