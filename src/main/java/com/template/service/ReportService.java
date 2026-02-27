package com.template.service;

import com.template.Domain.ProgrammeSummaryRow;
import com.template.Domain.Student;
import com.template.Domain.TopPerformerRow;
import com.template.repository.StudentRepository;
import com.template.repository.sqlite.SQLiteStudentRepository;

import java.util.*;
import java.util.stream.Collectors;

public class ReportService {

    private final StudentRepository repo = new SQLiteStudentRepository();

    public List<Student> getAllStudents() {
        return repo.findAll();
    }

    public double averageGpa(List<Student> students) {
        if (students == null || students.isEmpty()) return 0.0;
        return students.stream().mapToDouble(Student::getGpa).average().orElse(0.0);
    }

    public List<TopPerformerRow> getTopPerformers(String programme, Integer level, int limit) {

        List<Student> all = repo.findAll();

        List<Student> filtered = all.stream()
                .filter(s -> programme == null || programme.isBlank() || programme.equalsIgnoreCase(s.getProgramme()))
                .filter(s -> level == null || level == 0 || level == s.getLevel())
                .sorted(Comparator.comparingDouble(Student::getGpa).reversed()
                        .thenComparing(Student::getFullName, String.CASE_INSENSITIVE_ORDER))
                .limit(limit)
                .toList();

        List<TopPerformerRow> rows = new ArrayList<>();
        for (int i = 0; i < filtered.size(); i++) {
            Student s = filtered.get(i);
            rows.add(new TopPerformerRow(
                    i + 1,
                    s.getStudentId(),
                    s.getFullName(),
                    s.getProgramme(),
                    s.getLevel(),
                    s.getGpa()
            ));
        }
        return rows;
    }

    public List<Student> getAtRiskStudents(double threshold) {
        return repo.findAll().stream()
                .filter(s -> s.getGpa() < threshold)
                .sorted(Comparator.comparingDouble(Student::getGpa))
                .toList();
    }

    public Map<String, Long> getGpaDistribution() {
        // Fixed bands (stable ordering)
        LinkedHashMap<String, Long> map = new LinkedHashMap<>();
        map.put("0.0-0.9", 0L);
        map.put("1.0-1.9", 0L);
        map.put("2.0-2.9", 0L);
        map.put("3.0-3.4", 0L);
        map.put("3.5-4.0", 0L);

        for (Student s : repo.findAll()) {
            double g = s.getGpa();
            String band;
            if (g < 1.0) band = "0.0-0.9";
            else if (g < 2.0) band = "1.0-1.9";
            else if (g < 3.0) band = "2.0-2.9";
            else if (g < 3.5) band = "3.0-3.4";
            else band = "3.5-4.0";

            map.put(band, map.get(band) + 1);
        }

        return map;
    }

    public List<ProgrammeSummaryRow> getProgrammeSummary() {

        List<Student> all = repo.findAll();

        Map<String, List<Student>> byProgramme = all.stream()
                .collect(Collectors.groupingBy(Student::getProgramme));

        List<ProgrammeSummaryRow> rows = new ArrayList<>();

        for (String programme : byProgramme.keySet()) {
            List<Student> students = byProgramme.get(programme);
            int count = students.size();
            double avg = students.stream().mapToDouble(Student::getGpa).average().orElse(0.0);

            rows.add(new ProgrammeSummaryRow(programme, count, avg));
        }

        rows.sort(Comparator.comparing(ProgrammeSummaryRow::getProgramme, String.CASE_INSENSITIVE_ORDER));
        return rows;
    }
}