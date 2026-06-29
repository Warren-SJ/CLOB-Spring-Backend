package com.warren.clob.services;

import com.warren.clob.models.User;
import com.warren.clob.repos.UserRepo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserService {

    final private UserRepo userRepo;

    void addUser(User user) {
        userRepo.save(user);
    }

    void deleteById(long id) {
        userRepo.deleteById(id);
    }
}
