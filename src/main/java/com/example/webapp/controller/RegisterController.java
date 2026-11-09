package com.example.webapp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.webapp.domain.Student;
import com.example.webapp.repository.StudentRepository;


@Controller
public class RegisterController {
    @Autowired
    StudentRepository repo;

    @GetMapping("/signup")
    public String displaySignup() {
        return "register.html";
    }

    @PostMapping("/signup")
    public String signup(@ModelAttribute Student student) {
        repo.save(student);
        return "redirect:/index.html";
    }
}
