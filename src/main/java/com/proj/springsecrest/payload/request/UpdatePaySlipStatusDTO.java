package com.proj.springsecrest.payload.request;

import com.proj.springsecrest.enums.EPaySlipStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdatePaySlipStatusDTO {
    @NotNull
    private String status;
}