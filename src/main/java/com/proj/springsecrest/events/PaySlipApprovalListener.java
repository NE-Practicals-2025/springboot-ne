package com.proj.springsecrest.events;

import com.proj.springsecrest.helpers.MailService;
import com.proj.springsecrest.models.Employee;
import com.proj.springsecrest.models.Message;
import com.proj.springsecrest.models.PaySlip;
import com.proj.springsecrest.repositories.IMessageRepository;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionalEventListener;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaySlipApprovalListener {
    private final IMessageRepository messageRepository;
    private final MailService emailService;

    @TransactionalEventListener
    public void handlePaySlipApprovedEvent(PaySlipApprovedEvent event) {
        PaySlip paySlip = event.getPaySlip();
        Employee employee = paySlip.getEmployee();

        // Create and save message
        String messageContent = String.format(
                "Dear %s, your salary for %d/%d from RCA amounting to %.2f RWF has been credited to your account %s successfully.",
                employee.getFirstName(),
                paySlip.getMonth(),
                paySlip.getYear(),
                paySlip.getNetSalary(),
                employee.getCode()
        );

        Message message = new Message();
        message.setEmployee(employee);
        message.setContent(messageContent);
        message.setMonthYear(paySlip.getMonth() + "/" + paySlip.getYear());
        message.setSentAt(LocalDateTime.now());
        messageRepository.save(message);

        // Send email
        try {
            emailService.sendSalaryNotification(
                    employee.getEmail(),
                    employee.getFirstName(),
                    paySlip.getMonth(),
                    paySlip.getYear(),
                    paySlip.getNetSalary(),
                    employee.getCode().toString()
            );
        } catch (MessagingException e) {
            log.error("Failed to send salary notification email to {}", employee.getEmail(), e);
        }
    }
}