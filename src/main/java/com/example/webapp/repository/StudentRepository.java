package com.example.webapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.webapp.domain.Student;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    Student findByStudentnameAndPassword(String studentname, String password);
    Student findByEmail(String email);
}
