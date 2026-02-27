package com.template.repository;

import java.util.List;

public interface SettingsRepository {

    double getAtRiskThreshold();
    void setAtRiskThreshold(double value);

    List<String> getAllProgrammes();
    void addProgramme(String name);
    void updateProgramme(String oldName, String newName);
    void deleteProgramme(String name);
    boolean programmeExists(String name);
}