package com.proj.springsecrest.payload.response;

import com.proj.springsecrest.models.Employee;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JwtAuthenticationResponse {
    private String accessToken;
    private String tokenType = "Bearer";
    private Employee employee;

    public JwtAuthenticationResponse(String accessToken, Employee employee) {
        this.accessToken = accessToken;
        this.employee = employee;
    }
}



