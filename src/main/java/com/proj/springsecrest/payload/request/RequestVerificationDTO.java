package com.proj.springsecrest.payload.request;

import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class RequestVerificationDTO {

    @Email
    private String email;

}
