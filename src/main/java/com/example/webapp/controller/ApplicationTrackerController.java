package com.example.webapp.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.webapp.domain.JobApplication;
import com.example.webapp.repository.JobApplicationRepository;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/applications")
public class ApplicationTrackerController {

    @Autowired
    JobApplicationRepository applicationRepo;

    @GetMapping
    public List<JobApplication> getApplications(HttpSession session) {
        Long studentId = (Long) session.getAttribute("studentId");
        if (studentId == null) {
            return List.of();
        }
        return applicationRepo.findByStudentId(studentId);
    }

    @PostMapping
    public Map<String, Object> addApplication(@RequestParam String companyName,
                                               @RequestParam String role,
                                               @RequestParam String status,
                                               @RequestParam String appliedDate,
                                               @RequestParam(required = false) String interviewDate,
                                               @RequestParam(required = false) String notes,
                                               HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        Long studentId = (Long) session.getAttribute("studentId");

        if (studentId == null) {
            result.put("error", "Not logged in");
            return result;
        }

        JobApplication app = new JobApplication();
        app.setStudentId(studentId);
        app.setCompanyName(companyName);
        app.setRole(role);
        app.setStatus(status);
        app.setAppliedDate(appliedDate);
        app.setInterviewDate(interviewDate != null ? interviewDate : "");
        app.setNotes(notes != null ? notes : "");
        applicationRepo.save(app);

        result.put("message", "Application added successfully");
        result.put("id", app.getId());
        return result;
    }

    @PutMapping("/{id}")
    public Map<String, Object> updateStatus(@PathVariable Long id,
                                             @RequestParam String status,
                                             HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        Long studentId = (Long) session.getAttribute("studentId");

        if (studentId == null) {
            result.put("error", "Not logged in");
            return result;
        }

        JobApplication app = applicationRepo.findById(id).orElse(null);
        if (app == null || !app.getStudentId().equals(studentId)) {
            result.put("error", "Application not found");
            return result;
        }

        app.setStatus(status);
        applicationRepo.save(app);
        result.put("message", "Status updated");
        return result;
    }

    @DeleteMapping("/{id}")
    public Map<String, Object> deleteApplication(@PathVariable Long id, HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        Long studentId = (Long) session.getAttribute("studentId");

        if (studentId == null) {
            result.put("error", "Not logged in");
            return result;
        }

        JobApplication app = applicationRepo.findById(id).orElse(null);
        if (app == null || !app.getStudentId().equals(studentId)) {
            result.put("error", "Application not found");
            return result;
        }

        applicationRepo.delete(app);
        result.put("message", "Application deleted");
        return result;
    }
}
