package com.example.webapp.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.webapp.domain.Resume;
import com.example.webapp.repository.ResumeRepository;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/resume")
public class ResumeController {

    @Autowired
    ResumeRepository resumeRepo;

    @PostMapping("/analyze")
    public Map<String, Object> analyzeResume(@RequestParam String skills,
                                              @RequestParam String education,
                                              @RequestParam String experience,
                                              HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        Long studentId = (Long) session.getAttribute("studentId");

        if (studentId == null) {
            result.put("error", "Not logged in");
            return result;
        }

        // Simple ATS scoring logic
        int score = 0;
        String suggestions = "";

        // Check skills
        String[] skillList = skills.split(",");
        score += Math.min(40, skillList.length * 5);
        if (skillList.length < 5) {
            suggestions += "Add more technical skills. ";
        }

        // Check education
        if (education != null && !education.trim().isEmpty()) {
            score += 20;
        } else {
            suggestions += "Add education details. ";
        }

        // Check experience
        if (experience != null && !experience.trim().isEmpty()) {
            score += 20;
            if (experience.length() < 50) {
                suggestions += "Elaborate on your experience with action verbs. ";
            }
        } else {
            suggestions += "Add project or work experience. ";
        }

        // Check for common keywords
        String combined = (skills + " " + education + " " + experience).toLowerCase();
        if (!combined.contains("project")) suggestions += "Add project descriptions. ";
        if (!combined.contains("team")) suggestions += "Mention teamwork experience. ";
        if (!combined.contains("develop") && !combined.contains("built") && !combined.contains("created")) {
            suggestions += "Use action verbs like developed, built, created. ";
        }
        score += 20; // base score

        score = Math.min(100, score);

        // Save resume
        Resume resume = new Resume();
        resume.setStudentId(studentId);
        resume.setFileName("manual-entry");
        resume.setSkills(skills);
        resume.setEducation(education);
        resume.setExperience(experience);
        resume.setAtsScore(score);
        resume.setSuggestions(suggestions);
        resumeRepo.save(resume);

        result.put("atsScore", score);
        result.put("suggestions", suggestions);
        result.put("skills", skills);
        result.put("education", education);
        result.put("experience", experience);

        return result;
    }

    @GetMapping("/history")
    public List<Resume> getResumeHistory(HttpSession session) {
        Long studentId = (Long) session.getAttribute("studentId");
        if (studentId == null) {
            return List.of();
        }
        return resumeRepo.findByStudentId(studentId);
    }
}
