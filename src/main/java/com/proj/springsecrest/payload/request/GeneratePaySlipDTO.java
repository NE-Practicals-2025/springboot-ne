package com.proj.springsecrest.payload.request;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.UUID;

@Data
public class GeneratePaySlipDTO {
    @NotNull
    private UUID employeeCode;

    @Min(1)
    @Max(12)
    private int month;

    @Min(2000)
    @Max(2150)
    private int year;
}
