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
import java.util.stream.Collectors;

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

    @GetMapping("/view")
    public String viewTasks(Model model, HttpSession session) {
        if (!isLoggedIn(session)) return "redirect:/login";

        User user = getLoggedInUser(session);
        List<Task> tasks = taskRepository.findByUser(user)
                .stream()
                .sorted((t1, t2) -> {
                    if (t1.getReminderDate() == null) return 1;
                    if (t2.getReminderDate() == null) return -1;
                    return t1.getReminderDate().compareTo(t2.getReminderDate());
                })
                .collect(Collectors.toList());

        model.addAttribute("tasks", tasks);
        model.addAttribute("task", new Task());
        return "tasks";
    }

    @GetMapping("/new")
    public String createTaskForm(Model model, HttpSession session) {
        if (!isLoggedIn(session)) return "redirect:/login";
        model.addAttribute("task", new Task());
        return "create-task";
    }

    @PostMapping("/save")
    public String saveTask(@ModelAttribute("task") Task task, HttpSession session) {
        if (!isLoggedIn(session)) return "redirect:/login";
        User user = getLoggedInUser(session);
        task.setUser(user);
        taskRepository.save(task);
        return "redirect:/tasks/view";
    }

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
