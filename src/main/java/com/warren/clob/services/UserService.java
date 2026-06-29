package com.warren.clob.services;

import com.warren.clob.models.User;
import com.warren.clob.repos.UserRepo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserService {

    final private UserRepo userRepo;

    public User createUser(User user) {
        if(user.getName() == null || user.getName().isEmpty()) {
            throw new IllegalArgumentException("User name cannot be null or empty");
        }
        if(user.getEmail() == null || user.getEmail().isEmpty()) {
            throw new IllegalArgumentException("User email cannot be null or empty");
        }
        if(user.getContact() == null || user.getContact().isEmpty()) {
            throw new IllegalArgumentException("User contact cannot be null or empty");
        }
        user.setCash(0);
        user.setBuyingPower(0);
        userRepo.save(user);
        return user;
    }

    void deleteById(long id) {
        userRepo.deleteById(id);
    }
}
