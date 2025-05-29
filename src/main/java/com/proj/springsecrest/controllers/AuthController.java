package com.proj.springsecrest.controllers;

import com.proj.springsecrest.annotations.RateLimit;
import com.proj.springsecrest.enums.ERole;
import com.proj.springsecrest.enums.EUserStatus;
import com.proj.springsecrest.exceptions.BadRequestException;
import com.proj.springsecrest.models.Employee;
import com.proj.springsecrest.models.Role;
import com.proj.springsecrest.payload.request.*;
import com.proj.springsecrest.payload.response.ApiResponse;
import com.proj.springsecrest.repositories.IEmployeeRepository;
import com.proj.springsecrest.repositories.IRoleRepository;
import com.proj.springsecrest.services.IEmployeeService;
import com.proj.springsecrest.services.serviceImpls.AuthServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Collections;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    @Value("${admin.creation.key}")
    private String adminCreationKey;
    private static final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final AuthServiceImpl authService;
    private final IRoleRepository roleRepository;
    private final IEmployeeService employeeService;

    public AuthController(AuthServiceImpl authService, IRoleRepository roleRepository, IEmployeeService employeeService) {
        this.authService = authService;
        this.roleRepository = roleRepository;
        this.employeeService = employeeService;
    }
    @PostMapping(path = "/login")
    @RateLimit(value = 10, durationInSeconds = 5) // 1 login per 5 seconds per user/IP
    public ResponseEntity<ApiResponse> login(@RequestBody LoginDTO loginDto){
        return ResponseEntity.ok(ApiResponse.success("Successfully Logged in", this.authService.login(loginDto.getEmail(), loginDto.getPassword())));
    }

    @PostMapping(path = "/admin/register")
    public ResponseEntity<ApiResponse> registerAdmin(@RequestBody @Valid CreateAdminDTO dto) {


        if(!adminCreationKey.equals(dto.getAdminCreationKey())){
            throw new BadRequestException("Admin creation key is incorrect");
        }
        Employee user = new Employee();

        String encodedPassword = passwordEncoder.encode(dto.getPassword());
        Role role = roleRepository.findByName(ERole.ROLE_ADMIN).orElseThrow(
                () -> new BadRequestException("Admin Role not set"));

        user.setEmail(dto.getEmail());
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setTelephone(dto.getTelephone());
        user.setPassword(encodedPassword);
        user.setDateOfBirth(dto.getDateOfBirth());
        user.setRoles(Collections.singleton(role));
        user.setStatus(EUserStatus.ACTIVE);
        Employee entity = this.employeeService.create(user);
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().toString());
        return ResponseEntity.created(uri).body(ApiResponse.success("Admmin created successfully", entity));
    }

    @PostMapping(path = "/manager/register")
    @PreAuthorize("hasRole(ROLE_ADMIN)")
    public ResponseEntity<ApiResponse> registerManager(@RequestBody @Valid CreateManagerDTO dto) {


        Employee user = new Employee();

        String encodedPassword = passwordEncoder.encode(dto.getPassword());
        Role role = roleRepository.findByName(ERole.ROLE_MANAGER).orElseThrow(
                () -> new BadRequestException("Manager Role not set"));

        user.setEmail(dto.getEmail());
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setTelephone(dto.getTelephone());
        user.setPassword(encodedPassword);
        user.setDateOfBirth(dto.getDateOfBirth());
        user.setRoles(Collections.singleton(role));
        user.setStatus(EUserStatus.ACTIVE);
        Employee entity = this.employeeService.create(user);
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().toString());
        return ResponseEntity.created(uri).body(ApiResponse.success("Manager created successfully", entity));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse> forgotPassword(@RequestBody ForgotPasswordDTO dto){
        this.authService.initiateResetPassword(dto.getEmail());
        return ResponseEntity.ok(ApiResponse.success("Reset email send successfully to " + dto.getEmail()));
    }

    @PostMapping(path = "/reset-password")
    public ResponseEntity<ApiResponse> resetPassword(@RequestBody @Valid ResetPasswordDTO dto) {
        this.authService.resetPassword(dto.getEmail(),dto.getPasswordResetCode(), dto.getNewPassword());
        return ResponseEntity.ok(ApiResponse.success("Password successfully reset"));
    }

    @PutMapping("/request-verification")
    private ResponseEntity<ApiResponse> requestVerification(@RequestBody @Valid RequestVerificationDTO dto) {
        this.authService.initiateAccountVerification(dto.getEmail());
        return ResponseEntity.ok(ApiResponse.success("Verification code sent to email, expiring in 6 hours"));
    }

    @PatchMapping("/verify-account/{verificationCode}")
    private ResponseEntity<ApiResponse> verifyAccount(
            @PathVariable("verificationCode") String verificationCode
    ) {
        this.authService.verifyAccount(verificationCode);
        return ResponseEntity.ok(ApiResponse.success("Account verified successfully, you can now login"));
    }
}
