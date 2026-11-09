package com.example.webapp.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.webapp.domain.JobApplication;
import com.example.webapp.domain.Resume;
import com.example.webapp.repository.JobApplicationRepository;
import com.example.webapp.repository.ResumeRepository;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @Autowired
    ResumeRepository resumeRepo;

    @Autowired
    JobApplicationRepository applicationRepo;

    @GetMapping
    public Map<String, Object> getDashboard(HttpSession session) {
        Map<String, Object> dashboard = new HashMap<>();
        Long studentId = (Long) session.getAttribute("studentId");
        String studentname = (String) session.getAttribute("studentname");

        if (studentId == null) {
            dashboard.put("error", "Not logged in");
            return dashboard;
        }

        dashboard.put("studentname", studentname);

        // Resume score
        List<Resume> resumes = resumeRepo.findByStudentId(studentId);
        int atsScore = 0;
        if (!resumes.isEmpty()) {
            atsScore = resumes.get(resumes.size() - 1).getAtsScore();
        }
        dashboard.put("atsScore", atsScore);

        // Application stats
        List<JobApplication> applications = applicationRepo.findByStudentId(studentId);
        int totalApplied = applications.size();
        int pending = 0;
        int interviews = 0;
        int offers = 0;
        int rejected = 0;
        for (JobApplication app : applications) {
            if ("Applied".equals(app.getStatus())) pending++;
            else if ("Interview".equals(app.getStatus())) interviews++;
            else if ("Offered".equals(app.getStatus())) offers++;
            else if ("Rejected".equals(app.getStatus())) rejected++;
        }
        dashboard.put("totalApplied", totalApplied);
        dashboard.put("pending", pending);
        dashboard.put("interviews", interviews);
        dashboard.put("offers", offers);
        dashboard.put("rejected", rejected);

        // Placement readiness (simple calculation)
        int readiness = Math.min(100, atsScore + (totalApplied * 5) + (interviews * 10) + (offers * 20));
        dashboard.put("readinessScore", readiness);

        return dashboard;
    }
}
