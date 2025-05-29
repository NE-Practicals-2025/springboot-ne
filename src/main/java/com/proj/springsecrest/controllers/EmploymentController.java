package com.proj.springsecrest.controllers;

import com.proj.springsecrest.enums.EEmployeementStatus;
import com.proj.springsecrest.enums.ERole;
import com.proj.springsecrest.enums.EUserStatus;
import com.proj.springsecrest.exceptions.BadRequestException;
import com.proj.springsecrest.helpers.Constants;
import com.proj.springsecrest.models.Employee;
import com.proj.springsecrest.models.Employment;
import com.proj.springsecrest.models.Role;
import com.proj.springsecrest.payload.request.CreateEmployeeDTO;
import com.proj.springsecrest.payload.request.CreateEmploymentDTO;
import com.proj.springsecrest.payload.request.UpdateEmploymentDTO;
import com.proj.springsecrest.payload.request.UpdateUserDTO;
import com.proj.springsecrest.payload.response.ApiResponse;
import com.proj.springsecrest.repositories.IEmployeeRepository;
import com.proj.springsecrest.repositories.IRoleRepository;
import com.proj.springsecrest.services.IEmployeeService;
import com.proj.springsecrest.services.IEmploymentService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Collections;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/employment")
public class EmploymentController {
    private final IEmploymentService employmentService;
    private final IEmployeeRepository employeeRepository;

    public EmploymentController(IEmploymentService employmentService, IEmployeeRepository employeeRepository) {
        this.employmentService = employmentService;
        this.employeeRepository = employeeRepository;
    }

    @PutMapping(path = "/update")
    public ResponseEntity<ApiResponse> update(@RequestBody UpdateEmploymentDTO dto, @Param("code") UUID id) {
        Employment updated = this.employmentService.update(id, dto);
        return ResponseEntity.ok(ApiResponse.success("Employment updated successfully", updated));
    }

    @GetMapping(path = "/all")
    public ResponseEntity<ApiResponse> getAllEmployments(
            @RequestParam(value = "page", defaultValue = Constants.DEFAULT_PAGE_NUMBER) int page,
            @RequestParam(value = "size", defaultValue = Constants.DEFAULT_PAGE_SIZE) int limit
    ) {
        Pageable pageable = (Pageable) PageRequest.of(page, limit, Sort.Direction.ASC, "code");
        return ResponseEntity.ok(ApiResponse.success("Employments fetched successfully", employmentService.getAll(pageable)));
    }


    @GetMapping(path = "/{id}")
    public ResponseEntity<ApiResponse> getById(@PathVariable(value = "id") UUID id) {
        return ResponseEntity.ok(ApiResponse.success("Employment fetched successfully", this.employmentService.getByCode(id)));
    }

//    @PreAuthorize("hasRole('MANAGER')")
    @PostMapping(path = "/register")
    public ResponseEntity<ApiResponse> register(@RequestBody @Valid CreateEmploymentDTO dto) {

        Employment employment = new Employment();
        Employee employee = employeeRepository.findByCode(dto.getEmployeeCode()).orElseThrow(
                () -> new BadRequestException(String.format("Employee with code %s not found", dto.getEmployeeCode())));

        employment.setStatus(dto.getStatus());
        employment.setJoiningDate(dto.getJoiningDate());
        employment.setDepartment(dto.getDepartment());
        employment.setBaseSalary(dto.getBaseSalary());
        employment.setPosition(dto.getPosition());
        employment.setEmployee(employee);
        Employment entity = this.employmentService.create(employment);
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().toString());
        return ResponseEntity.created(uri).body(ApiResponse.success("Employment created successfully", entity));
    }
}
