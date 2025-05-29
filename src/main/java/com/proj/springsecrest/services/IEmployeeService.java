package com.proj.springsecrest.services;


import com.proj.springsecrest.enums.ERole;
import com.proj.springsecrest.enums.EUserStatus;
import com.proj.springsecrest.models.Employee;
import com.proj.springsecrest.payload.request.UpdateUserDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;


public interface IEmployeeService {

    Page<Employee> getAll(Pageable pageable);

    Employee getByCode(UUID id);

    Employee create(Employee user);
    Employee save(Employee user);

    Employee update(UUID id, UpdateUserDTO dto);

    boolean delete(UUID id);

    Page<Employee> getAllByRole(Pageable pageable, ERole role);

    Page<Employee> searchUser(Pageable pageable, String searchKey);

    Employee getLoggedInUser();

    Employee getByEmail(String email);

    Employee changeStatus(UUID id, EUserStatus status);

    Optional<Employee> findByActivationCode(String verificationCode);
}
