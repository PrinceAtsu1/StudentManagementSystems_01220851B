package com.template.repository.sqlite;

import com.template.repository.SettingsRepository;
import com.template.util.DatabaseUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class SQLiteSettingsRepository implements SettingsRepository {

    @Override
    public double getAtRiskThreshold() {

        String sql = "SELECT value FROM settings WHERE key = ?";

        try (Connection conn = DatabaseUtil.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, "at_risk_threshold");
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Double.parseDouble(rs.getString("value"));
                }
            }

        } catch (Exception ignored) {}

        return 2.0; // default
    }

    @Override
    public void setAtRiskThreshold(double value) {

        String sql = "INSERT OR REPLACE INTO settings(key, value) VALUES(?, ?)";

        try (Connection conn = DatabaseUtil.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, "at_risk_threshold");
            ps.setString(2, String.valueOf(value));
            ps.executeUpdate();

        } catch (Exception e) {
            throw new RuntimeException("Failed to save threshold.");
        }
    }

    @Override
    public List<String> getAllProgrammes() {

        List<String> list = new ArrayList<>();
        String sql = "SELECT name FROM programmes ORDER BY name ASC";

        try (Connection conn = DatabaseUtil.connect();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(rs.getString("name"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    @Override
    public void addProgramme(String name) {

        String sql = "INSERT INTO programmes(name) VALUES(?)";

        try (Connection conn = DatabaseUtil.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, name);
            ps.executeUpdate();

        } catch (Exception e) {
            throw new RuntimeException("Failed to add programme.");
        }
    }

    @Override
    public void updateProgramme(String oldName, String newName) {

        String sql = "UPDATE programmes SET name = ? WHERE name = ?";

        try (Connection conn = DatabaseUtil.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, newName);
            ps.setString(2, oldName);
            ps.executeUpdate();

        } catch (Exception e) {
            throw new RuntimeException("Failed to update programme.");
        }
    }

    @Override
    public void deleteProgramme(String name) {

        String sql = "DELETE FROM programmes WHERE name = ?";

        try (Connection conn = DatabaseUtil.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, name);
            ps.executeUpdate();

        } catch (Exception e) {
            throw new RuntimeException("Failed to delete programme.");
        }
    }

    @Override
    public boolean programmeExists(String name) {

        String sql = "SELECT 1 FROM programmes WHERE name = ? LIMIT 1";

        try (Connection conn = DatabaseUtil.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, name);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }

        } catch (Exception e) {
            return false;
        }
    }
}