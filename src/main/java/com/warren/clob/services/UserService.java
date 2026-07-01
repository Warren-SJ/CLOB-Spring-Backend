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

    public Integer findBuyingPowerById(Long id) {
        User user = userRepo.findById(id).orElse(null);
        return user != null ? user.getBuyingPower() : null;
    }

    public User findById(long id) {
        return userRepo.findById(id).orElse(null);
    }

    public void save(User existingUser) {
        userRepo.save(existingUser);
    }

    public void addCash(Long userId, int amount) {
        User user = userRepo.findById(userId).orElse(null);
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        user.setCash(user.getCash() + amount);
        user.setBuyingPower(user.getBuyingPower() + amount);
        userRepo.save(user);
    }
}
