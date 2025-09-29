package com.example.TaskManagement.controller;

import com.example.TaskManagement.model.Task;
import com.example.TaskManagement.model.User;
import com.example.TaskManagement.repository.TaskRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskApiController {

    @Autowired
    private TaskRepository taskRepository;

    // Get all tasks for logged-in user
    @GetMapping
    public List<Task> getAllTasks(HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            throw new RuntimeException("Unauthorized");
        }
        return taskRepository.findByUser(loggedInUser);
    }

    // Get a specific task for logged-in user
    @GetMapping("/{id}")
    public Task getTaskById(@PathVariable Long id, HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            throw new RuntimeException("Unauthorized");
        }
        Task task = taskRepository.findById(id).orElse(null);
        if (task != null && task.getUser().getId().equals(loggedInUser.getId())) {
            return task;
        }
        throw new RuntimeException("Unauthorized");
    }

    // Create a new task for logged-in user
    @PostMapping
    public Task createTask(@RequestBody Task task, HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            throw new RuntimeException("Unauthorized");
        }
        task.setUser(loggedInUser);
        return taskRepository.save(task);
    }

    // Update a task only if it belongs to logged-in user
    @PutMapping("/{id}")
    public Task updateTask(@PathVariable Long id, @RequestBody Task updatedTask, HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            throw new RuntimeException("Unauthorized");
        }

        Task task = taskRepository.findById(id).orElse(null);
        if (task != null && task.getUser().getId().equals(loggedInUser.getId())) {
            task.setTitle(updatedTask.getTitle());
            task.setDescription(updatedTask.getDescription());
            task.setPriority(updatedTask.getPriority());
            task.setDueDate(updatedTask.getDueDate());
            task.setCompleted(updatedTask.isCompleted());
            return taskRepository.save(task);
        }
        throw new RuntimeException("Unauthorized");
    }

    // Delete a task only if it belongs to logged-in user
    @DeleteMapping("/{id}")
    public String deleteTask(@PathVariable Long id, HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            throw new RuntimeException("Unauthorized");
        }

        Task task = taskRepository.findById(id).orElse(null);
        if (task != null && task.getUser().getId().equals(loggedInUser.getId())) {
            taskRepository.delete(task);
            return "Deleted";
        }
        throw new RuntimeException("Unauthorized");
    }
}
