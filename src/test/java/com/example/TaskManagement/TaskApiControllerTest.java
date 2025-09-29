package com.example.TaskManagement;

import com.example.TaskManagement.model.Task;
import com.example.TaskManagement.model.User;
import com.example.TaskManagement.repository.TaskRepository;
import com.example.TaskManagement.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class TaskApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setup() {
        taskRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void testGetAllTasks() throws Exception {
        // Create a user
        User user = new User();
        user.setName("Test User");
        user.setEmail("test@example.com");
        user.setPassword("password");
        userRepository.save(user);

        // Create a task
        Task task = new Task();
        task.setTitle("JUnit Task");
        task.setDescription("Testing controller");
        task.setReminderDate(LocalDate.now());
        task.setUser(user);
        taskRepository.save(task);

        // Test GET /api/tasks with session attribute
        mockMvc.perform(get("/api/tasks")
                        .sessionAttr("loggedInUser", user))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$[0].title").value("JUnit Task"))
                .andExpect(jsonPath("$[0].description").value("Testing controller"));
    }
}
