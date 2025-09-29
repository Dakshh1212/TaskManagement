package com.example.TaskManagement;

import com.example.TaskManagement.service.EmailService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SpringBootTest
public class EmailServiceTest {

    @Autowired
    private EmailService emailService;

    @Test
    void testSendEmail() {
        assertDoesNotThrow(() -> emailService.sendEmail(
                "test@example.com",
                "Test Subject",
                "Test Body"
        ));
    }
}
