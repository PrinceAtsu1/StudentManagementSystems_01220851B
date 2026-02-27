package com.template.repository;

import com.template.Domain.Student;

import java.util.List;

public interface StudentRepository {

    void save(Student student);

    void update(Student student);

    List<Student> findAll();

    void delete(String studentId);

    List<Student> search(String keyword, String programme, Integer level, String status);

    boolean existsById(String studentId);
}