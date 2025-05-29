package com.proj.springsecrest.payload.request;


import com.proj.springsecrest.enums.EGender;
import com.proj.springsecrest.enums.ERole;
import com.proj.springsecrest.helpers.ValidPassword;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CreateAdminDTO {

    @Email
    private String email;

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    @NotBlank
    @Pattern(regexp = "[0-9]{9,12}", message = "Your phone is not a valid tel we expect 2507***, or 07*** or 7***")
    private String telephone;

    private LocalDate dateOfBirth;

    @NotBlank
    private String adminCreationKey;

    @ValidPassword
    private String password;
}