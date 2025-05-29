package com.proj.springsecrest.services.serviceImpls;

import com.proj.springsecrest.enums.EUserStatus;
import com.proj.springsecrest.exceptions.AppException;
import com.proj.springsecrest.exceptions.BadRequestException;
import com.proj.springsecrest.exceptions.ResourceNotFoundException;
import com.proj.springsecrest.helpers.MailService;
import com.proj.springsecrest.helpers.Utility;
import com.proj.springsecrest.models.Employee;
import com.proj.springsecrest.payload.response.JwtAuthenticationResponse;
import com.proj.springsecrest.security.JwtTokenProvider;
import com.proj.springsecrest.services.IAuthService;
import com.proj.springsecrest.services.IEmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements IAuthService {

    private final IEmployeeService employeeService;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private static final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final MailService mailService;

    @Override
    public JwtAuthenticationResponse login(String email, String password) {
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(email, password));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = null;
        try {
            jwt = jwtTokenProvider.generateToken(authentication);
        } catch (Exception e) {
            throw new AppException("Error generating token", e);
        }
        Employee user = this.employeeService.getByEmail(email);
        return new JwtAuthenticationResponse(jwt, user);
    }

    @Override
    public void initiateResetPassword(String email){
        Employee user = this.employeeService.getByEmail(email);
        user.setActivationCode(Utility.randomUUID(6,0,'N'));
        user.setStatus(EUserStatus.ACTIVE);
        this.employeeService.save(user);
        mailService.sendResetPasswordMail(user.getEmail(), user.getFirstName() + " " + user.getLastName(), user.getActivationCode());
    }


    @Override
    public void resetPassword(String email, String passwordResetCode, String newPassword) {
        Employee user = this.employeeService.getByEmail(email);
        if (Utility.isCodeValid(user.getActivationCode(), passwordResetCode) &&
                (user.getStatus().equals(EUserStatus.ACTIVE)) || user.getStatus().equals(EUserStatus.ACTIVE)) {
            user.setPassword(passwordEncoder.encode(newPassword));
            user.setActivationCode(Utility.randomUUID(6, 0, 'N'));
            user.setStatus(EUserStatus.ACTIVE);
            this.employeeService.save(user);
            this.mailService.sendPasswordResetSuccessfully(user.getEmail(), user.getFullName());
        } else {
            throw new BadRequestException("Invalid code or account status");
        }
    }

    @Override
    public void initiateAccountVerification(String email) {
        Employee user = this.employeeService.getByEmail(email);
        if (user.getStatus() == EUserStatus.ACTIVE) {
            throw new BadRequestException("User is already verified");
        }
        String verificationCode;
        do {
            verificationCode = Utility.generateAuthCode();
        } while (this.employeeService.findByActivationCode(verificationCode).isPresent());
        LocalDateTime verificationCodeExpiresAt = LocalDateTime.now().plusHours(6);
        user.setActivationCode(verificationCode);
        user.setActivationCodeExpiresAt(verificationCodeExpiresAt);
        this.mailService.sendActivateAccountEmail(user.getEmail(), user.getFullName(), verificationCode);
        this.employeeService.save(user);
    }

    @Override
    public void verifyAccount(String verificationCode) {
        Optional<Employee> _user = this.employeeService.findByActivationCode(verificationCode);
        if (_user.isEmpty()) {
            throw new ResourceNotFoundException("User", verificationCode, verificationCode);
        }
        Employee user = _user.get();
        if (user.getActivationCodeExpiresAt().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Verification code is invalid or expired");
        }
        user.setStatus(EUserStatus.ACTIVE);
        user.setActivationCodeExpiresAt(null);
        user.setActivationCode(null);
        this.mailService.sendAccountVerifiedSuccessfullyEmail(user.getEmail(), user.getFullName());
        this.employeeService.save(user);
    }


}
