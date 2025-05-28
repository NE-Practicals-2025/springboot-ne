package com.proj.springsecrest.controllers;

import com.proj.springsecrest.annotations.RateLimit;
import com.proj.springsecrest.payload.request.ForgotPasswordDTO;
import com.proj.springsecrest.payload.request.LoginDTO;
import com.proj.springsecrest.payload.request.RequestVerificationDTO;
import com.proj.springsecrest.payload.request.ResetPasswordDTO;
import com.proj.springsecrest.payload.response.ApiResponse;
import com.proj.springsecrest.services.serviceImpls.AuthServiceImpl;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthServiceImpl authService;

    public AuthController(AuthServiceImpl authService) {
        this.authService = authService;
    }
    @PostMapping(path = "/login")
    @RateLimit(value = 1, durationInSeconds = 5) // 1 login per 5 seconds per user/IP
    public ResponseEntity<ApiResponse> login(@RequestBody LoginDTO loginDto){
        return ResponseEntity.ok(ApiResponse.success("Successfully Logged in", this.authService.login(loginDto.getEmail(), loginDto.getPassword())));
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
