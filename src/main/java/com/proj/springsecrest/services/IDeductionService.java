package com.proj.springsecrest.services;


import com.proj.springsecrest.enums.ERole;
import com.proj.springsecrest.enums.EUserStatus;
import com.proj.springsecrest.models.Deduction;
import com.proj.springsecrest.models.Employee;
import com.proj.springsecrest.payload.request.DeductionDTO;
import com.proj.springsecrest.payload.request.UpdateUserDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;


public interface IDeductionService {

    Page<Deduction> getAll(Pageable pageable);
    Deduction create(Deduction user);
    Deduction update(UUID id, DeductionDTO dto);
    boolean delete(UUID id);
}
