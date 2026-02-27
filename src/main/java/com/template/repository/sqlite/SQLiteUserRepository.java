package com.template.repository.sqlite;

import com.template.Domain.User;
import com.template.repository.UserRepository;
import com.template.util.DatabaseUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class SQLiteUserRepository implements UserRepository {

    @Override
    public void save(User user) {
        String sql = """
            INSERT INTO users(email, full_name, password_hash, date_created)
            VALUES(?,?,?,?)
        """;

        try (Connection conn = DatabaseUtil.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, user.getEmail());
            ps.setString(2, user.getFullName());
            ps.setString(3, user.getPasswordHash());
            ps.setString(4, user.getDateCreated().toString());

            ps.executeUpdate();

        } catch (Exception e) {
            throw new RuntimeException("Account creation failed (email may already exist).");
        }
    }

    @Override
    public User findByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = ?";

        try (Connection conn = DatabaseUtil.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;

                User u = new User();
                u.setEmail(rs.getString("email"));
                u.setFullName(rs.getString("full_name"));
                u.setPasswordHash(rs.getString("password_hash"));
                u.setDateCreated(java.time.LocalDate.parse(rs.getString("date_created")));
                return u;
            }

        } catch (Exception e) {
            throw new RuntimeException("Database error while loading user.");
        }
    }

    @Override
    public boolean existsByEmail(String email) {
        String sql = "SELECT 1 FROM users WHERE email = ? LIMIT 1";

        try (Connection conn = DatabaseUtil.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }

        } catch (Exception e) {
            return false;
        }
    }
}