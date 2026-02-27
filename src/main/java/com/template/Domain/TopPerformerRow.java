package com.template.Domain;

public class TopPerformerRow {

    private int rank;
    private String studentId;
    private String fullName;
    private String programme;
    private int level;
    private double gpa;

    public TopPerformerRow(int rank, String studentId, String fullName, String programme, int level, double gpa) {
        this.rank = rank;
        this.studentId = studentId;
        this.fullName = fullName;
        this.programme = programme;
        this.level = level;
        this.gpa = gpa;
    }

    public int getRank() { return rank; }
    public String getStudentId() { return studentId; }
    public String getFullName() { return fullName; }
    public String getProgramme() { return programme; }
    public int getLevel() { return level; }
    public double getGpa() { return gpa; }
}