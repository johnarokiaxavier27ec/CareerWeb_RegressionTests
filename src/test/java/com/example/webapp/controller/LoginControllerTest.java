package com.example.webapp.controller;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import com.example.webapp.domain.Student;
import com.example.webapp.repository.StudentRepository;

@WebMvcTest(LoginController.class)
class LoginControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private StudentRepository repo;

    @Test
    void correctCredentials_redirectToDashboard() throws Exception {
        Student student = mock(Student.class);
        when(student.getStudentname()).thenReturn("JOHN");
        when(repo.findByStudentnameAndPassword("JOHN", "pass123")).thenReturn(student);
        mockMvc.perform(post("/signin")
                .param("studentname", "JOHN")
                .param("password", "pass123"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/dashboard.html"))
            .andExpect(request().sessionAttribute("studentname", "JOHN"));
    }


    @Test
    void wrongCredentials_redirectBackToLogin() throws Exception {
        when(repo.findByStudentnameAndPassword("JOHN", "wrong")).thenReturn(null);
        mockMvc.perform(post("/signin")
                .param("studentname", "JOHN")
                .param("password", "wrong"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/login.html"));
    }
}
