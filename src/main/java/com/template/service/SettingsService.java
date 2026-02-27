package com.template.service;

import com.template.repository.SettingsRepository;
import com.template.repository.sqlite.SQLiteSettingsRepository;

import java.util.List;

public class SettingsService {

    private final SettingsRepository repo = new SQLiteSettingsRepository();

    public double getAtRiskThreshold() {
        return repo.getAtRiskThreshold();
    }

    public void setAtRiskThreshold(double value) {
        if (value < 0.0 || value > 4.0) {
            throw new IllegalArgumentException("Threshold must be between 0.0 and 4.0.");
        }
        repo.setAtRiskThreshold(value);
    }

    public List<String> getProgrammes() {
        return repo.getAllProgrammes();
    }

    public void addProgramme(String name) {

        if (name == null || name.isBlank())
            throw new IllegalArgumentException("Programme name is required.");

        String cleaned = name.trim();

        if (repo.programmeExists(cleaned))
            throw new IllegalArgumentException("Programme already exists.");

        repo.addProgramme(cleaned);
    }

    public void updateProgramme(String oldName, String newName) {

        if (newName == null || newName.isBlank())
            throw new IllegalArgumentException("Programme name is required.");

        String cleaned = newName.trim();

        if (!oldName.equalsIgnoreCase(cleaned) && repo.programmeExists(cleaned))
            throw new IllegalArgumentException("Programme already exists.");

        repo.updateProgramme(oldName, cleaned);
    }

    public void deleteProgramme(String name) {
        repo.deleteProgramme(name);
    }
}