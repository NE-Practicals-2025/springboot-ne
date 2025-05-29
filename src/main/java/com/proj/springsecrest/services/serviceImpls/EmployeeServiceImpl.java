package com.proj.springsecrest.services.serviceImpls;


import com.proj.springsecrest.enums.ERole;
import com.proj.springsecrest.enums.EUserStatus;
import com.proj.springsecrest.exceptions.BadRequestException;
import com.proj.springsecrest.exceptions.ResourceNotFoundException;
import com.proj.springsecrest.helpers.MailService;
import com.proj.springsecrest.helpers.Utility;
import com.proj.springsecrest.models.Employee;
import com.proj.springsecrest.payload.request.UpdateUserDTO;
import com.proj.springsecrest.repositories.IEmployeeRepository;
import com.proj.springsecrest.services.IEmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements IEmployeeService {

    private final IEmployeeRepository employeeRepository;
    private final MailService mailService;

//    private final IFileService fileService;
//    private final FileStorageService fileStorageService;

    @Override
    public Page<Employee> getAll(Pageable pageable) {
        return this.employeeRepository.findAll(pageable);
    }

    @Override
    public Employee getByCode(UUID id) {
        return this.employeeRepository.findByCode(id).orElseThrow(
                () -> new ResourceNotFoundException("Employee", "id", id.toString()));
    }

    @Override
    public Employee create(Employee user) {
        try {
            Optional<Employee> userOptional = this.employeeRepository.findByEmail(user.getEmail());
            Optional<Employee> userOptionPhone = this.employeeRepository.findByTelephone(user.getTelephone());

            if (userOptional.isPresent())
                throw new BadRequestException(String.format("User with email '%s' already exists", user.getEmail()));
            if (userOptionPhone.isPresent())
                throw new BadRequestException(String.format("User with phone '%s' already exists", user.getTelephone()));
            Employee newUser =  this.employeeRepository.save(user);
            this.mailService.registrationSuccessful(user.getEmail(), newUser.getFullName());
            return newUser;
        } catch (DataIntegrityViolationException ex) {
            String errorMessage = Utility.getConstraintViolationMessage(ex, user);
            throw new BadRequestException(errorMessage, ex);
        }
    }

    @Override
    public Employee save(Employee user) {
        try {
            return this.employeeRepository.save(user);
        } catch (DataIntegrityViolationException ex) {
            String errorMessage = Utility.getConstraintViolationMessage(ex, user);
            throw new BadRequestException(errorMessage, ex);
        }
    }


    @Override
    public Employee update(UUID id, UpdateUserDTO dto) {
        Employee user = this.employeeRepository.findByCode(id).orElseThrow(() -> new ResourceNotFoundException("User", "id", id.toString()));
        user.setEmail(dto.getEmail());
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setTelephone(dto.getTelephone());
        return this.employeeRepository.save(user);
    }

    @Override
    public boolean delete(UUID id) {
        this.employeeRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("User", "id", id));

        this.employeeRepository.deleteById(id);
        return true;
    }

    @Override
    public Page<Employee> getAllByRole(Pageable pageable, ERole role) {
        return this.employeeRepository.findByRoles(pageable, role);
    }

    @Override
    public Page<Employee> searchUser(Pageable pageable, String searchKey) {
        return this.employeeRepository.searchEmployee(pageable, searchKey);
    }

    @Override
    public Employee getLoggedInUser() {
        String email;
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof UserDetails) {
            email = ((UserDetails) principal).getUsername();
        } else {
            email = principal.toString();
        }

        return employeeRepository.findByEmail(email).orElseThrow(
                () -> new ResourceNotFoundException("User", "id", email));
    }

    @Override
    public Employee getByEmail(String email) {
        return this.employeeRepository.findByEmail(email).orElseThrow(
                () -> new ResourceNotFoundException("User", "id", email));
    }


    @Override
    public Employee changeStatus(UUID id, EUserStatus status) {
        Employee entity = this.employeeRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("User", "id", id.toString()));

        entity.setStatus(status);

        return this.employeeRepository.save(entity);
    }

    @Override
    public Optional<Employee> findByActivationCode(String activationCode) {
        return this.employeeRepository.findByActivationCode(activationCode);
    }
}
