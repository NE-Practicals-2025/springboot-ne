package com.proj.springsecrest.payload.request;


import com.proj.springsecrest.enums.EEmployeementStatus;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UpdateEmploymentDTO {

    @NotBlank
    private String department;

    @NotBlank
    private EEmployeementStatus status;

    @NotBlank
    private String position;

    @NotBlank
    private Double baseSalary;

    @NotBlank
    private LocalDate joiningDate;
}