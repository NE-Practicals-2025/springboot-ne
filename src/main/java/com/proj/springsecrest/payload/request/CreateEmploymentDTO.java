package com.proj.springsecrest.payload.request;


import com.proj.springsecrest.enums.EEmployeementStatus;
import com.proj.springsecrest.enums.ERole;
import com.proj.springsecrest.helpers.ValidPassword;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.NonNull;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class CreateEmploymentDTO {

    @NotBlank
    private UUID employeeCode;

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