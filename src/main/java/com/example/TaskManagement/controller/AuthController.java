package com.example.TaskManagement.controller;

import com.example.TaskManagement.model.User;
import com.example.TaskManagement.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    // Show signup page
    @GetMapping("/signup")
    public String signupForm(Model model) {
        model.addAttribute("user", new User());
        return "signup";
    }

    // Handle signup with validation
    @PostMapping("/signup")
    public String signupSubmit(@Valid @ModelAttribute("user") User user,
                               BindingResult result,
                               Model model) {

        // If validation fails → return to signup form with errors
        if (result.hasErrors()) {
            return "signup";
        }

        // Check if email already exists
        if (userRepository.findByEmail(user.getEmail()) != null) {
            model.addAttribute("error", "Email already registered. Try logging in.");
            return "signup";
        }

        // Save new user
        userRepository.save(user);
        return "redirect:/login";
    }

    // Show login page
    @GetMapping("/login")
    public String loginForm() {
        return "login";
    }

    // Handle login
    @PostMapping("/login")
    public String loginSubmit(@RequestParam String email,
                              @RequestParam String password,
                              HttpSession session,
                              Model model) {

        User user = userRepository.findByEmail(email);

        if (user != null && user.getPassword().equals(password)) {
            session.setAttribute("loggedInUser", user);
            return "redirect:/tasks/view";
        }

        model.addAttribute("error", "Invalid email or password");
        return "login";
    }

    // Logout
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}
