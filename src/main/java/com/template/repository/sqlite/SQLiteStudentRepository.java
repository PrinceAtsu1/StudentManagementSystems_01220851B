package com.template.repository.sqlite;

import com.template.Domain.Student;
import com.template.repository.StudentRepository;
import com.template.util.DatabaseUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class SQLiteStudentRepository implements StudentRepository {

    @Override
    public void save(Student s) {

        String sql = """
            INSERT INTO students(student_id, full_name, programme, level, gpa, email, phone, date_added, status)
            VALUES(?,?,?,?,?,?,?,?,?)
        """;

        try (Connection conn = DatabaseUtil.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, s.getStudentId());
            ps.setString(2, s.getFullName());
            ps.setString(3, s.getProgramme());
            ps.setInt(4, s.getLevel());
            ps.setDouble(5, s.getGpa());
            ps.setString(6, s.getEmail());
            ps.setString(7, s.getPhone());
            ps.setString(8, s.getDateAdded() == null ? null : s.getDateAdded().toString());
            ps.setString(9, s.getStatus());

            ps.executeUpdate();

        } catch (Exception e) {
            throw new RuntimeException("Failed to save student.");
        }
    }

    @Override
    public void update(Student s) {

        String sql = """
            UPDATE students
            SET full_name=?,
                programme=?,
                level=?,
                gpa=?,
                email=?,
                phone=?,
                date_added=?,
                status=?
            WHERE student_id=?
        """;

        try (Connection conn = DatabaseUtil.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, s.getFullName());
            ps.setString(2, s.getProgramme());
            ps.setInt(3, s.getLevel());
            ps.setDouble(4, s.getGpa());
            ps.setString(5, s.getEmail());
            ps.setString(6, s.getPhone());
            ps.setString(7, s.getDateAdded() == null ? null : s.getDateAdded().toString());
            ps.setString(8, s.getStatus());
            ps.setString(9, s.getStudentId());

            ps.executeUpdate();

        } catch (Exception e) {
            throw new RuntimeException("Failed to update student.");
        }
    }

    @Override
    public List<Student> findAll() {

        List<Student> list = new ArrayList<>();
        String sql = "SELECT * FROM students";

        try (Connection conn = DatabaseUtil.connect();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(map(rs));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    @Override
    public void delete(String studentId) {

        String sql = "DELETE FROM students WHERE student_id=?";

        try (Connection conn = DatabaseUtil.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, studentId);
            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean existsById(String studentId) {

        String sql = "SELECT 1 FROM students WHERE student_id=? LIMIT 1";

        try (Connection conn = DatabaseUtil.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }

        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public List<Student> search(String keyword, String programme, Integer level, String status) {

        List<Student> list = new ArrayList<>();

        StringBuilder sql = new StringBuilder("SELECT * FROM students WHERE 1=1 ");

        if (keyword != null && !keyword.isBlank()) {
            sql.append("AND (student_id LIKE ? OR full_name LIKE ?) ");
        }
        if (programme != null && !programme.isBlank()) {
            sql.append("AND programme=? ");
        }
        if (level != null) {
            sql.append("AND level=? ");
        }
        if (status != null && !status.isBlank()) {
            sql.append("AND status=? ");
        }

        try (Connection conn = DatabaseUtil.connect();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            int i = 1;

            if (keyword != null && !keyword.isBlank()) {
                ps.setString(i++, "%" + keyword + "%");
                ps.setString(i++, "%" + keyword + "%");
            }
            if (programme != null && !programme.isBlank()) {
                ps.setString(i++, programme);
            }
            if (level != null) {
                ps.setInt(i++, level);
            }
            if (status != null && !status.isBlank()) {
                ps.setString(i++, status);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(map(rs));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    private Student map(ResultSet rs) throws Exception {

        Student s = new Student();

        s.setStudentId(rs.getString("student_id"));
        s.setFullName(rs.getString("full_name"));
        s.setProgramme(rs.getString("programme"));
        s.setLevel(rs.getInt("level"));
        s.setGpa(rs.getDouble("gpa"));
        s.setEmail(rs.getString("email"));
        s.setPhone(rs.getString("phone"));

        String dateStr = rs.getString("date_added");
        if (dateStr != null && !dateStr.isBlank()) {
            s.setDateAdded(LocalDate.parse(dateStr));
        }

        s.setStatus(rs.getString("status"));

        return s;
    }
}