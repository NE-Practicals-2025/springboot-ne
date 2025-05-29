package com.proj.springsecrest.helpers;

import com.proj.springsecrest.models.Employee;
import com.proj.springsecrest.models.Message;
import com.proj.springsecrest.models.PaySlip;
import com.proj.springsecrest.models.PayslipPaidEvent;
import com.proj.springsecrest.repositories.IMessageRepository;
import jakarta.mail.MessagingException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionalEventListener;

@Service
public class PayslipStatusChangeListener {
    private final IMessageRepository messageRepository;
    private final MailService emailService;

    public PayslipStatusChangeListener(IMessageRepository messageRepository,
                                       MailService emailService) {
        this.messageRepository = messageRepository;
        this.emailService = emailService;
    }

    @TransactionalEventListener
    public void handlePayslipPaidEvent(PayslipPaidEvent event) {
        PaySlip payslip = event.getPayslip();
        Employee employee = payslip.getEmployee();

        // Save message to database
        Message message = new Message();
        message.setEmployee(employee);
        message.setContent(buildMessageContent(employee, payslip));
        message.setMonthYear(payslip.getMonth() + "/" + payslip.getYear());
        messageRepository.save(message);

        // Send email notification using the generic service
        try {
            emailService.sendSalaryNotification(
                    employee.getEmail(),
                    employee.getFirstName(),
                    payslip.getMonth(),
                    payslip.getYear(),
                    payslip.getNetSalary(),
                    employee.getCode().toString()
            );
        } catch (MessagingException e) {
            // Handle email sending failure (log it, retry, etc.)
            throw new RuntimeException("Failed to send salary notification email", e);
        }
    }

    private String buildMessageContent(Employee employee, PaySlip payslip) {
        return String.format(
                "Dear %s, your salary for %d/%d from RCA amounting to %.2f RWF has been credited to your account %s successfully.",
                employee.getFirstName(),
                payslip.getMonth(),
                payslip.getYear(),
                payslip.getNetSalary(),
                employee.getCode()
        );
    }
}