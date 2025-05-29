package com.proj.springsecrest.controllers;

import com.fasterxml.jackson.datatype.jsr310.util.DurationUnitConverter;
import com.proj.springsecrest.enums.ERole;
import com.proj.springsecrest.enums.EUserStatus;
import com.proj.springsecrest.exceptions.BadRequestException;
import com.proj.springsecrest.helpers.Constants;
import com.proj.springsecrest.models.Deduction;
import com.proj.springsecrest.models.Role;
import com.proj.springsecrest.models.Employee;
import com.proj.springsecrest.payload.request.CreateEmployeeDTO;
import com.proj.springsecrest.payload.request.DeductionDTO;
import com.proj.springsecrest.payload.request.UpdateUserDTO;
import com.proj.springsecrest.payload.response.ApiResponse;
import com.proj.springsecrest.repositories.IDeductionRepository;
import com.proj.springsecrest.repositories.IRoleRepository;
import com.proj.springsecrest.services.IDeductionService;
import com.proj.springsecrest.services.IEmployeeService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/deductions")
public class DeductionController {
    private final IDeductionService deductionService;
    private final IDeductionRepository deductionRepository;

    public DeductionController(IDeductionService deductionService, IDeductionRepository deductionRepository) {
        this.deductionService = deductionService;
        this.deductionRepository = deductionRepository;
    }

    @GetMapping(path = "/all")
    public ResponseEntity<ApiResponse> getAllDeductions(
            @RequestParam(value = "page", defaultValue = Constants.DEFAULT_PAGE_NUMBER) int page,
            @RequestParam(value = "size", defaultValue = Constants.DEFAULT_PAGE_SIZE) int limit
    ) {
        Pageable pageable = (Pageable) PageRequest.of(page, limit, Sort.Direction.ASC, "code");
        return ResponseEntity.ok(ApiResponse.success("Deductions fetched successfully", deductionService.getAll(pageable)));
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<ApiResponse> getById(@PathVariable(value = "id") UUID id) {
        return ResponseEntity.ok(ApiResponse.success("Deduction fetched successfully", this.deductionRepository.findByCode(id)));
    }

    @PostMapping(path = "/create")
    public ResponseEntity<ApiResponse> register(@RequestBody @Valid DeductionDTO dto) {

        Deduction deduction = new Deduction();

        Optional<Deduction> existingDeduction = deductionRepository.findByDeductionName(dto.getDeductionName());
        if(existingDeduction.isPresent()) {
            throw new BadRequestException("Deduction name already exists");
        }

//        if(!Objects.equals(dto.getDeductionName(), "EmployeeTax"))
        deduction.setDeductionName(dto.getDeductionName());
        deduction.setPercentage(dto.getPercentage());
        Deduction entity = this.deductionService.create(deduction);
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().toString());
        return ResponseEntity.created(uri).body(ApiResponse.success("Deduction created successfully", entity));
    }
}
