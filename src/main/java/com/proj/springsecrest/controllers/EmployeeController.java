package com.proj.springsecrest.controllers;

import com.proj.springsecrest.enums.ERole;
import com.proj.springsecrest.enums.EUserStatus;
import com.proj.springsecrest.exceptions.BadRequestException;
import com.proj.springsecrest.helpers.Constants;
import com.proj.springsecrest.models.Role;
import com.proj.springsecrest.models.Employee;
import com.proj.springsecrest.payload.request.CreateEmployeeDTO;
import com.proj.springsecrest.payload.request.UpdateUserDTO;
import com.proj.springsecrest.payload.response.ApiResponse;
import com.proj.springsecrest.repositories.IRoleRepository;
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
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/employees")
public class EmployeeController {
    private final IEmployeeService employeeService;
    private final IRoleRepository roleRepository;

    private static final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    public EmployeeController(IEmployeeService userService, IRoleRepository roleRepository) {
        this.employeeService = userService;
        this.roleRepository = roleRepository;
    }

    @GetMapping(path = "/current-user")
    public ResponseEntity<ApiResponse> currentlyLoggedInUser() {
        return ResponseEntity.ok(ApiResponse.success("Currently logged in user fetched", employeeService.getLoggedInUser()));
    }

    @PutMapping(path = "/update")
    public ResponseEntity<ApiResponse> update(@RequestBody UpdateUserDTO dto) {
        Employee updated = this.employeeService.update(this.employeeService.getLoggedInUser().getCode(), dto);
        return ResponseEntity.ok(ApiResponse.success("Employee updated successfully", updated));
    }

    @GetMapping(path = "/all")
    public ResponseEntity<ApiResponse> getAllUsers(
            @RequestParam(value = "page", defaultValue = Constants.DEFAULT_PAGE_NUMBER) int page,
            @RequestParam(value = "size", defaultValue = Constants.DEFAULT_PAGE_SIZE) int limit
    ) {
        Pageable pageable = (Pageable) PageRequest.of(page, limit, Sort.Direction.ASC, "code");
        return ResponseEntity.ok(ApiResponse.success("Users fetched successfully", employeeService.getAll(pageable)));
    }

    @GetMapping(path = "/all/{role}")
    public ResponseEntity<ApiResponse> getAllUsersByRole(
            @RequestParam(value = "page", defaultValue = Constants.DEFAULT_PAGE_NUMBER) int page,
            @RequestParam(value = "size", defaultValue = Constants.DEFAULT_PAGE_SIZE) int limit,
            @PathVariable(value = "role") ERole role
    ) {
        Pageable pageable = (Pageable) PageRequest.of(page, limit, Sort.Direction.ASC, "id");
        return ResponseEntity.ok(ApiResponse.success("Users fetched successfully", employeeService.getAllByRole(pageable, role)));
    }

    @GetMapping(path = "/search")
    public Page<Employee> searchUsers(
            @RequestParam(value = "page", defaultValue = Constants.DEFAULT_PAGE_NUMBER) int page,
            @RequestParam(value = "size", defaultValue = Constants.DEFAULT_PAGE_SIZE) int limit,
            @RequestParam(value = "searchKey") String searchKey
    ) {
        Pageable pageable = (Pageable) PageRequest.of(page, limit, Sort.Direction.ASC, "id");
        return employeeService.searchUser(pageable, searchKey);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<ApiResponse> getById(@PathVariable(value = "id") UUID id) {
        return ResponseEntity.ok(ApiResponse.success("User fetched successfully", this.employeeService.getByCode(id)));
    }

    @PostMapping(path = "/register")
    public ResponseEntity<ApiResponse> register(@RequestBody @Valid CreateEmployeeDTO dto) {

        Employee user = new Employee();

        String encodedPassword = passwordEncoder.encode(dto.getPassword());
        Role role = roleRepository.findByName(ERole.ROLE_EMPLOYEE).orElseThrow(
                () -> new BadRequestException("Employee Role not set"));

        user.setEmail(dto.getEmail());
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setTelephone(dto.getTelephone());
        user.setPassword(encodedPassword);
        user.setDateOfBirth(dto.getDateOfBirth());
        user.setRoles(Collections.singleton(role));
        user.setStatus(EUserStatus.ACTIVE);
        Employee entity = this.employeeService.create(user);
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().toString());
        return ResponseEntity.created(uri).body(ApiResponse.success("User created successfully", entity));
    }
}
