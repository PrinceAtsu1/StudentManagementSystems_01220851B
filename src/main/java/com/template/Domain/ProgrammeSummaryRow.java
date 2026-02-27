package com.template.Domain;

public class ProgrammeSummaryRow {

    private String programme;
    private int totalStudents;
    private double avgGpa;

    public ProgrammeSummaryRow(String programme, int totalStudents, double avgGpa) {
        this.programme = programme;
        this.totalStudents = totalStudents;
        this.avgGpa = avgGpa;
    }

    public String getProgramme() { return programme; }
    public int getTotalStudents() { return totalStudents; }
    public double getAvgGpa() { return avgGpa; }
}