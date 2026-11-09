package com.example.webapp.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/skillgap")
public class SkillGapController {

    // Predefined skill requirements for common roles
    private static final Map<String, List<String>> ROLE_SKILLS = new HashMap<>();

    static {
        ROLE_SKILLS.put("Microsoft SDE", Arrays.asList(
            "Java", "C++", "Data Structures", "Algorithms", "System Design",
            "SQL", "Azure", "REST API", "Git", "OOP"
        ));
        ROLE_SKILLS.put("Google SDE", Arrays.asList(
            "Python", "Java", "C++", "Data Structures", "Algorithms",
            "System Design", "Distributed Systems", "Linux", "Git", "OOP"
        ));
        ROLE_SKILLS.put("Amazon SDE", Arrays.asList(
            "Java", "Python", "Data Structures", "Algorithms", "System Design",
            "AWS", "Microservices", "SQL", "REST API", "Leadership Principles"
        ));
        ROLE_SKILLS.put("TCS Digital", Arrays.asList(
            "Java", "Python", "SQL", "Data Structures", "DBMS",
            "Networking", "OOP", "Agile", "Git", "Cloud Basics"
        ));
        ROLE_SKILLS.put("Infosys Power Programmer", Arrays.asList(
            "Java", "Python", "Data Structures", "Algorithms", "SQL",
            "DBMS", "OOP", "Linux", "Git", "Problem Solving"
        ));
        ROLE_SKILLS.put("Wipro Elite", Arrays.asList(
            "Java", "SQL", "Data Structures", "OOP", "DBMS",
            "Networking", "Operating Systems", "Git", "HTML/CSS", "JavaScript"
        ));
    }

    @PostMapping("/analyze")
    public Map<String, Object> analyzeSkillGap(@RequestParam String targetRole,
                                                @RequestParam String currentSkills,
                                                HttpSession session) {
        Map<String, Object> result = new HashMap<>();

        if (session.getAttribute("studentId") == null) {
            result.put("error", "Not logged in");
            return result;
        }

        List<String> requiredSkills = ROLE_SKILLS.getOrDefault(targetRole,
            Arrays.asList("Java", "Data Structures", "SQL", "OOP", "Git"));

        String[] userSkills = currentSkills.toLowerCase().split(",");
        List<String> userSkillList = new ArrayList<>();
        for (String s : userSkills) {
            userSkillList.add(s.trim().toLowerCase());
        }

        List<String> missingSkills = new ArrayList<>();
        List<String> matchedSkills = new ArrayList<>();

        for (String required : requiredSkills) {
            boolean found = false;
            for (String userSkill : userSkillList) {
                if (userSkill.contains(required.toLowerCase()) || required.toLowerCase().contains(userSkill)) {
                    found = true;
                    break;
                }
            }
            if (found) {
                matchedSkills.add(required);
            } else {
                missingSkills.add(required);
            }
        }

        int matchPercent = (matchedSkills.size() * 100) / requiredSkills.size();
        int estimatedWeeks = missingSkills.size() * 2; // 2 weeks per skill

        result.put("targetRole", targetRole);
        result.put("requiredSkills", requiredSkills);
        result.put("matchedSkills", matchedSkills);
        result.put("missingSkills", missingSkills);
        result.put("matchPercent", matchPercent);
        result.put("estimatedWeeks", estimatedWeeks);

        return result;
    }

    @PostMapping("/roles")
    public List<String> getAvailableRoles() {
        return new ArrayList<>(ROLE_SKILLS.keySet());
    }
}
