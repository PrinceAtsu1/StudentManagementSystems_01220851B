package com.template.service;

import com.template.Domain.User;
import com.template.repository.UserRepository;
import com.template.repository.sqlite.SQLiteUserRepository;
import com.template.util.PasswordUtil;

import java.time.LocalDate;

public class AuthService {

    private final UserRepository userRepo = new SQLiteUserRepository();

    public void register(String fullName, String email, String password, String confirmPassword) {

        if (fullName == null || fullName.isBlank()) {
            throw new IllegalArgumentException("Full name is required.");
        }

        if (email == null || !email.contains("@") || !email.contains(".")) {
            throw new IllegalArgumentException("Enter a valid email address.");
        }

        if (password == null || password.length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters.");
        }

        if (!password.equals(confirmPassword)) {
            throw new IllegalArgumentException("Passwords do not match.");
        }

        if (userRepo.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already exists. Please sign in.");
        }

        User u = new User();
        u.setFullName(fullName.trim());
        u.setEmail(email.trim().toLowerCase());
        u.setPasswordHash(PasswordUtil.hash(password));
        u.setDateCreated(LocalDate.now());

        userRepo.save(u);
    }

    public void login(String email, String password) {

        if (email == null || email.isBlank() || password == null || password.isBlank()) {
            throw new IllegalArgumentException("Email and password are required.");
        }

        User u = userRepo.findByEmail(email.trim().toLowerCase());
        if (u == null) {
            throw new IllegalArgumentException("Account not found. Please sign up first.");
        }

        String hash = PasswordUtil.hash(password);
        if (!hash.equals(u.getPasswordHash())) {
            throw new IllegalArgumentException("Incorrect password.");
        }
    }
}