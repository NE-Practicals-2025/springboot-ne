package com.proj.springsecrest.helpers;

import com.proj.springsecrest.exceptions.AppException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender mailSender;

    private final SpringTemplateEngine templateEngine;

    @Value("${app.frontend.reset-password}")
    private String resetPasswordUrl;

    @Value("${app.frontend.support-email}")
    private String supportEmail;

    /**
     * Generic method to send emails with Thymeleaf templates
     *
     * @param to Recipient email address
     * @param subject Email subject
     * @param templateName Thymeleaf template name (without extension)
     * @param templateVariables Map of variables to be used in the template
     * @throws MessagingException if there's an error sending the email
     */
    public void sendEmail(String to, String subject, String templateName,
                          Map<String, Object> templateVariables) throws MessagingException {

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        // Add common variables to all templates
        Context context = new Context();
        context.setVariable("supportEmail", supportEmail);
        context.setVariable("currentYear", LocalDate.now().getYear());

        // Add custom variables
        if (templateVariables != null) {
            templateVariables.forEach(context::setVariable);
        }

        String htmlContent = templateEngine.process(templateName, context);

        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlContent, true);

        mailSender.send(message);
    }

    /**
     * Convenience method for sending salary notification emails
     *
     * @param to Recipient email address
     * @param firstName Employee first name
     * @param month Month of payment
     * @param year Year of payment
     * @param netSalary Net salary amount
     * @param employeeCode Employee ID
     * @throws MessagingException if there's an error sending the email
     */
    public void sendSalaryNotification(String to, String firstName, int month, int year,
                                       double netSalary, String employeeCode) throws MessagingException {
        Map<String, Object> variables = new HashMap<>();
        variables.put("firstName", firstName);
        variables.put("month", month);
        variables.put("year", year);
        variables.put("netSalary", netSalary);
        variables.put("employeeCode", employeeCode);

        String subject = String.format("Salary Payment Notification - %d/%d", month, year);

        sendEmail(to, subject, "salary-notification", variables);
    }
    public void sendResetPasswordMail(String to, String fullName, String resetCode) {
        try {
            MimeMessage message = this.mailSender.createMimeMessage();

            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            Context context = new Context();
            context.setVariable("fullName", fullName);
            context.setVariable("resetCode", resetCode);
            context.setVariable("resetUrl", resetPasswordUrl);
            context.setVariable("supportEmail", supportEmail);
            context.setVariable("currentYear", LocalDate.now().getYear());

            String htmlContent = templateEngine.process("forgot-password-email", context);

            helper.setTo(to);
            helper.setSubject("Password Reset Request");
            helper.setText(htmlContent, true);

            this.mailSender.send(message);
        } catch (MessagingException e) {
            throw new AppException("Error sending email", e);
        }
    }

    public void sendActivateAccountEmail(String to, String fullName, String verificationCode) {
        try {
            MimeMessage message = this.mailSender.createMimeMessage();

            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            Context context = new Context();
            context.setVariable("fullName", fullName);
            context.setVariable("verificationCode", verificationCode);
            context.setVariable("resetUrl", resetPasswordUrl);
            context.setVariable("supportEmail", supportEmail);
            context.setVariable("currentYear", LocalDate.now().getYear());

            String htmlContent = templateEngine.process("verify-account-email", context);

            helper.setTo(to);
            helper.setSubject("Account activation Request");
            helper.setText(htmlContent, true);

            this.mailSender.send(message);
        } catch (MessagingException e) {
            throw new AppException("Error sending email", e);
        }
    }

    public void sendAccountVerifiedSuccessfullyEmail(String to, String fullName) {
        try {
            MimeMessage message = this.mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            Context context = new Context();
            context.setVariable("fullName", fullName);
            context.setVariable("supportEmail", supportEmail);
            context.setVariable("currentYear", LocalDate.now().getYear());

            String htmlContent = templateEngine.process("account-verification-successful", context);

            helper.setTo(to);
            helper.setSubject("Account Verification Successful");
            helper.setText(htmlContent, true);

            this.mailSender.send(message);
        } catch (MessagingException e) {
            throw new AppException("Error sending message", e);
        }
    }

    public void sendPasswordResetSuccessfully(String to, String fullName) {
        try {
            MimeMessage message = this.mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            Context context = new Context();
            context.setVariable("fullName", fullName);
            context.setVariable("supportEmail", supportEmail);
            context.setVariable("currentYear", LocalDate.now().getYear());

            String htmlContent = templateEngine.process("password-reset-successful", context);

            helper.setTo(to);
            helper.setSubject("Account Rejected");
            helper.setText(htmlContent, true);

            this.mailSender.send(message);
        } catch (MessagingException e) {
            throw new AppException("Error sending message", e);
        }
    }
    public void registrationSuccessful(String to, String fullName) {
        try {
            MimeMessage message = this.mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            Context context = new Context();
            context.setVariable("fullName", fullName);
            context.setVariable("supportEmail", supportEmail);
            context.setVariable("currentYear", LocalDate.now().getYear());

            String htmlContent = templateEngine.process("registration-success-email", context);

            helper.setTo(to);
            helper.setSubject("Registration Success");
            helper.setText(htmlContent, true);

            this.mailSender.send(message);
        } catch (MessagingException e) {
            throw new AppException("Error sending message", e);
        }
    }
}