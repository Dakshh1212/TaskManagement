package com.example.TaskManagement;

import com.example.TaskManagement.controller.AuthController;
import com.example.TaskManagement.model.User;
import com.example.TaskManagement.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AuthControllerTest {

    @InjectMocks
    private AuthController authController;

    @Mock
    private UserRepository userRepository;

    @Mock
    private HttpSession session;

    @Mock
    private Model model;

    @Mock
    private BindingResult bindingResult;

    private User testUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");
        testUser.setPassword("password");
    }

    @Test
    void testSignupForm() {
        String viewName = authController.signupForm(model);
        assertEquals("signup", viewName);
        verify(model, times(1)).addAttribute(eq("user"), any(User.class));
    }

    @Test
    void testSignupSubmit_WithErrors() {
        when(bindingResult.hasErrors()).thenReturn(true);

        String viewName = authController.signupSubmit(testUser, bindingResult, model);

        assertEquals("signup", viewName);
        verify(userRepository, never()).save(any());
    }

    @Test
    void testSignupSubmit_EmailExists() {
        when(bindingResult.hasErrors()).thenReturn(false);
        when(userRepository.findByEmail(testUser.getEmail())).thenReturn(testUser);

        String viewName = authController.signupSubmit(testUser, bindingResult, model);

        assertEquals("signup", viewName);
        verify(model).addAttribute(eq("error"), anyString());
        verify(userRepository, never()).save(any());
    }

    @Test
    void testSignupSubmit_Success() {
        when(bindingResult.hasErrors()).thenReturn(false);
        when(userRepository.findByEmail(testUser.getEmail())).thenReturn(null);

        String viewName = authController.signupSubmit(testUser, bindingResult, model);

        assertEquals("redirect:/login", viewName);
        verify(userRepository, times(1)).save(testUser);
    }

    @Test
    void testLoginSubmit_Success() {
        when(userRepository.findByEmail(testUser.getEmail())).thenReturn(testUser);

        String viewName = authController.loginSubmit(testUser.getEmail(), "password", session, model);

        assertEquals("redirect:/tasks/view", viewName);
        verify(session, times(1)).setAttribute("loggedInUser", testUser);
    }

    @Test
    void testLoginSubmit_InvalidCredentials() {
        when(userRepository.findByEmail(testUser.getEmail())).thenReturn(testUser);

        String viewName = authController.loginSubmit(testUser.getEmail(), "wrongpassword", session, model);

        assertEquals("login", viewName);
        verify(model).addAttribute(eq("error"), anyString());
        verify(session, never()).setAttribute(anyString(), any());
    }

    @Test
    void testLoginSubmit_UserNotFound() {
        when(userRepository.findByEmail(testUser.getEmail())).thenReturn(null);

        String viewName = authController.loginSubmit(testUser.getEmail(), "password", session, model);

        assertEquals("login", viewName);
        verify(model).addAttribute(eq("error"), anyString());
        verify(session, never()).setAttribute(anyString(), any());
    }

    @Test
    void testLogout() {
        String viewName = authController.logout(session);
        assertEquals("redirect:/login", viewName);
        verify(session, times(1)).invalidate();
    }
}
