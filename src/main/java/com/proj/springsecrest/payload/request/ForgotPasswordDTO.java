package com.proj.springsecrest.payload.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class ForgotPasswordDTO {
    @NotBlank
    private String email;
}
