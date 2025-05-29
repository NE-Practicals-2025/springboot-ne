package com.proj.springsecrest.payload.request;

import com.proj.springsecrest.enums.EGender;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;

@Getter
public class DeductionDTO {
    @NotBlank
    private String deductionName;

    @NotBlank
    private Double percentage;
}
