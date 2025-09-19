package com.example.TaskManagement.controller;

import com.example.TaskManagement.model.Task;
import com.example.TaskManagement.model.User;
import com.example.TaskManagement.repository.TaskRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/tasks")
public class TaskViewController {

    @Autowired
    private TaskRepository taskRepository;

    private boolean isLoggedIn(HttpSession session) {
        return session.getAttribute("loggedInUser") != null;
    }

    private User getLoggedInUser(HttpSession session) {
        return (User) session.getAttribute("loggedInUser");
    }

    // Show all tasks of the logged-in user
    @GetMapping("/view")
    public String viewTasks(Model model, HttpSession session) {
        if (!isLoggedIn(session)) return "redirect:/login";

        User user = getLoggedInUser(session);
        List<Task> tasks = taskRepository.findByUser(user);

        model.addAttribute("tasks", tasks);
        model.addAttribute("task", new Task()); // 🆕 Add blank task for form
        return "tasks";
    }


    // Show form for new task
    @GetMapping("/new")
    public String createTaskForm(Model model, HttpSession session) {
        if (!isLoggedIn(session)) return "redirect:/login";

        model.addAttribute("task", new Task());
        return "create-task";
    }

    // Save new task with logged-in user
    @PostMapping("/save")
    public String saveTask(@ModelAttribute("task") Task task, HttpSession session) {
        if (!isLoggedIn(session)) return "redirect:/login";

        User user = getLoggedInUser(session);
        task.setUser(user);  // Associate task with user
        taskRepository.save(task);
        return "redirect:/tasks/view";
    }

    // Edit task (only if it belongs to logged-in user)
    @GetMapping("/edit/{id}")
    public String editTaskForm(@PathVariable Long id, Model model, HttpSession session) {
        if (!isLoggedIn(session)) return "redirect:/login";

        User user = getLoggedInUser(session);
        Task task = taskRepository.findById(id)
                .filter(t -> t.getUser().getId().equals(user.getId()))
                .orElseThrow(() -> new RuntimeException("Task not found or access denied"));
        model.addAttribute("task", task);
        return "edit-task";
    }

    // Update task (only if it belongs to logged-in user)
    @PostMapping("/update/{id}")
    public String updateTask(@PathVariable Long id, @ModelAttribute("task") Task task, HttpSession session) {
        if (!isLoggedIn(session)) return "redirect:/login";

        User user = getLoggedInUser(session);
        Task existingTask = taskRepository.findById(id)
                .filter(t -> t.getUser().getId().equals(user.getId()))
                .orElseThrow(() -> new RuntimeException("Task not found or access denied"));

        task.setId(id);
        task.setUser(user);
        taskRepository.save(task);
        return "redirect:/tasks/view";
    }

    // Delete task (only if it belongs to logged-in user)
    @GetMapping("/delete/{id}")
    public String deleteTask(@PathVariable Long id, HttpSession session) {
        if (!isLoggedIn(session)) return "redirect:/login";

        User user = getLoggedInUser(session);
        Task task = taskRepository.findById(id)
                .filter(t -> t.getUser().getId().equals(user.getId()))
                .orElseThrow(() -> new RuntimeException("Task not found or access denied"));

        taskRepository.delete(task);
        return "redirect:/tasks/view";
    }

    // Mark task as complete ✅ (only if it belongs to logged-in user)
    @GetMapping("/complete/{id}")
    public String completeTask(@PathVariable Long id, HttpSession session) {
        if (!isLoggedIn(session)) return "redirect:/login";

        User user = getLoggedInUser(session);
        Task task = taskRepository.findById(id)
                .filter(t -> t.getUser().getId().equals(user.getId()))
                .orElseThrow(() -> new RuntimeException("Task not found or access denied"));

        task.setCompleted(true);
        taskRepository.save(task);
        return "redirect:/tasks/view";

    }
}
