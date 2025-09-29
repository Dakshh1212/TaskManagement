package com.example.TaskManagement.scheduler;

import com.example.TaskManagement.model.Task;
import com.example.TaskManagement.repository.TaskRepository;
import com.example.TaskManagement.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class ReminderScheduler {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private EmailService emailService;

    // Runs every 1 minute
    @Scheduled(cron = "0 0/1 * * * ?")
    public void sendReminders() {
        LocalDate today = LocalDate.now();
        System.out.println("Scheduler triggered at: " + today);

        List<Task> tasks = taskRepository.findAll();
        System.out.println("Total tasks in DB: " + tasks.size());

        for (Task task : tasks) {
            if (task.getReminderDate() != null) {
                System.out.println("➡ Checking task: " + task.getTitle() +
                        " | Reminder Date: " + task.getReminderDate());

                if (task.getReminderDate().isEqual(today)) {
                    try {
                        System.out.println("📧 Sending reminder to: " + task.getUser().getEmail());
                        emailService.sendEmail(
                                task.getUser().getEmail(),
                                "Reminder: " + task.getTitle(),
                                "You have a task due today: " + task.getDescription()
                        );
                        System.out.println("Email sent for task: " + task.getTitle());
                    } catch (Exception e) {
                        System.out.println("Failed to send email for task: " + task.getTitle());
                        e.printStackTrace();
                    }
                } else {
                    System.out.println("Skipped (Reminder date not today).");
                }
            }
        }
    }
}
