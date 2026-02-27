package com.template.repository;

import com.template.Domain.User;

public interface UserRepository {
    void save(User user);
    User findByEmail(String email);
    boolean existsByEmail(String email);
}