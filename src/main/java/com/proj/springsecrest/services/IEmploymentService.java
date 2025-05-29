package com.proj.springsecrest.services;


import com.proj.springsecrest.enums.ERole;
import com.proj.springsecrest.enums.EUserStatus;
import com.proj.springsecrest.models.Employee;
import com.proj.springsecrest.models.Employment;
import com.proj.springsecrest.payload.request.UpdateEmploymentDTO;
import com.proj.springsecrest.payload.request.UpdateUserDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;


public interface IEmploymentService {

    Page<Employment> getAll(Pageable pageable);

    Employment getByCode(UUID id);

    Employment create(Employment user);

    Employment update(UUID id, UpdateEmploymentDTO dto);

    boolean delete(UUID id);

}
