package com.proj.springsecrest.controllers;

import com.proj.springsecrest.enums.ERole;
import com.proj.springsecrest.exceptions.BadRequestException;
import com.proj.springsecrest.helpers.Constants;
import com.proj.springsecrest.models.Role;
import com.proj.springsecrest.models.User;
import com.proj.springsecrest.payload.request.CreateUserDTO;
import com.proj.springsecrest.payload.request.UpdateUserDTO;
import com.proj.springsecrest.payload.response.ApiResponse;
import com.proj.springsecrest.repositories.IRoleRepository;
import com.proj.springsecrest.services.IUserService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Collections;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    private final IUserService userService;
    private final IRoleRepository roleRepository;
    private static final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    public UserController(IUserService userService, IRoleRepository roleRepository) {
        this.userService = userService;
        this.roleRepository = roleRepository;
    }

    @GetMapping(path = "/current-user")
    public ResponseEntity<ApiResponse> currentlyLoggedInUser() {
        return ResponseEntity.ok(ApiResponse.success("Currently logged in user fetched", userService.getLoggedInUser()));
    }

    @PutMapping(path = "/update")
    public ResponseEntity<ApiResponse> update(@RequestBody UpdateUserDTO dto) {
        User updated = this.userService.update(this.userService.getLoggedInUser().getId(), dto);
        return ResponseEntity.ok(ApiResponse.success("User updated successfully", updated));
    }

    @GetMapping(path = "/all")
    public ResponseEntity<ApiResponse> getAllUsers(
            @RequestParam(value = "page", defaultValue = Constants.DEFAULT_PAGE_NUMBER) int page,
            @RequestParam(value = "size", defaultValue = Constants.DEFAULT_PAGE_SIZE) int limit
    ) {
        Pageable pageable = (Pageable) PageRequest.of(page, limit, Sort.Direction.ASC, "id");
        return ResponseEntity.ok(ApiResponse.success("Users fetched successfully", userService.getAll(pageable)));
    }

    @GetMapping(path = "/all/{role}")
    public ResponseEntity<ApiResponse> getAllUsersByRole(
            @RequestParam(value = "page", defaultValue = Constants.DEFAULT_PAGE_NUMBER) int page,
            @RequestParam(value = "size", defaultValue = Constants.DEFAULT_PAGE_SIZE) int limit,
            @PathVariable(value = "role") ERole role
    ) {
        Pageable pageable = (Pageable) PageRequest.of(page, limit, Sort.Direction.ASC, "id");
        return ResponseEntity.ok(ApiResponse.success("Users fetched successfully", userService.getAllByRole(pageable, role)));
    }

    @GetMapping(path = "/search")
    public Page<User> searchUsers(
            @RequestParam(value = "page", defaultValue = Constants.DEFAULT_PAGE_NUMBER) int page,
            @RequestParam(value = "size", defaultValue = Constants.DEFAULT_PAGE_SIZE) int limit,
            @RequestParam(value = "searchKey") String searchKey
    ) {
        Pageable pageable = (Pageable) PageRequest.of(page, limit, Sort.Direction.ASC, "id");
        return userService.searchUser(pageable, searchKey);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<ApiResponse> getById(@PathVariable(value = "id") UUID id) {
        return ResponseEntity.ok(ApiResponse.success("User fetched successfully", this.userService.getById(id)));
    }

    @PostMapping(path = "/register")
    public ResponseEntity<ApiResponse> register(@RequestBody @Valid CreateUserDTO dto) {

        User user = new User();

        String encodedPassword = passwordEncoder.encode(dto.getPassword());
        Role role = roleRepository.findByName(dto.getRole()).orElseThrow(
                () -> new BadRequestException("User Role not set"));

        user.setEmail(dto.getEmail());
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setGender(dto.getGender());
        user.setTelephone(dto.getTelephone());
        user.setPassword(encodedPassword);
        user.setRoles(Collections.singleton(role));

        User entity = this.userService.create(user);
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().toString());
        return ResponseEntity.created(uri).body(ApiResponse.success("User created successfully", entity));
    }
}
