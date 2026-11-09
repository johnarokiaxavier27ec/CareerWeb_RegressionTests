package com.example.webapp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.webapp.domain.Student;
import com.example.webapp.repository.StudentRepository;

import jakarta.servlet.http.HttpSession;


@Controller
public class LoginController {
    @Autowired
    StudentRepository repo;

    @GetMapping("/signin")
    public String showLoginPage() {
        return "login.html";
    }

    @PostMapping("/signin")
    public String login(@RequestParam String studentname, @RequestParam String password, HttpSession session) {
        Student entity = repo.findByStudentnameAndPassword(studentname, password);
        if (entity == null) {
            return "redirect:/login.html";
        }
        session.setAttribute("studentId", entity.getId());
        session.setAttribute("studentname", entity.getStudentname());
        return "redirect:/dashboard.html";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/index.html";
    }
}
