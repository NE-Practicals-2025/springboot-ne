package com.proj.springsecrest.services.serviceImpls;


import com.proj.springsecrest.enums.ERole;
import com.proj.springsecrest.enums.EUserStatus;
import com.proj.springsecrest.exceptions.BadRequestException;
import com.proj.springsecrest.exceptions.ResourceNotFoundException;
import com.proj.springsecrest.helpers.MailService;
import com.proj.springsecrest.helpers.Utility;
import com.proj.springsecrest.models.Employee;
import com.proj.springsecrest.models.Employment;
import com.proj.springsecrest.payload.request.UpdateEmploymentDTO;
import com.proj.springsecrest.payload.request.UpdateUserDTO;
import com.proj.springsecrest.repositories.IEmployeeRepository;
import com.proj.springsecrest.repositories.IEmploymentRepository;
import com.proj.springsecrest.services.IEmployeeService;
import com.proj.springsecrest.services.IEmploymentService;
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
public class EmploymentServiceImpl implements IEmploymentService {

    private final IEmployeeRepository employeeRepository;
    private final IEmploymentRepository employmentRepository;
    private final MailService mailService;

    @Override
    public Page<Employment> getAll(Pageable pageable) {
        return this.employmentRepository.findAll(pageable);
    }

    @Override
    public Employment getByCode(UUID id) {
        return this.employmentRepository.findByCode(id).orElseThrow(
                () -> new ResourceNotFoundException("Employment", "id", id.toString()));
    }

    @Override
    public Employment create(Employment employment) {
        try {
            Optional<Employment> userOptional = this.employmentRepository.findByEmployeeCode(employment.getEmployee().getCode());

            if (userOptional.isPresent())
                throw new BadRequestException(String.format("Employment for employee '%s' already exists", employment.getEmployee().getCode()));
            Employment newEmployment =  this.employmentRepository.save(employment);
//            this.mailService.registrationSuccessful(user.getEmail(), newUser.getFullName());
            return newEmployment;
        } catch (DataIntegrityViolationException ex) {
            String errorMessage = Utility.getConstraintViolationMessage(ex, employment);
            throw new BadRequestException(errorMessage, ex);
        }
    }

    @Override
    public Employment update(UUID id, UpdateEmploymentDTO dto) {
        Employment employment = this.employmentRepository.findByCode(id).orElseThrow(() -> new ResourceNotFoundException("Employment", "id", id.toString()));
        Employee employee = this.employeeRepository.findByCode(id).orElseThrow(() -> new ResourceNotFoundException("Employee", "id", id.toString()));
        employment.setDepartment(dto.getDepartment());
        employment.setPosition(dto.getPosition());
        employment.setEmployee(employee);
        employment.setBaseSalary(dto.getBaseSalary());
        employment.setStatus(dto.getStatus());
        employment.setJoiningDate(dto.getJoiningDate());
        return this.employmentRepository.save(employment);
    }

    @Override
    public boolean delete(UUID id) {
        this.employmentRepository.findByCode(id).orElseThrow(() ->
                new ResourceNotFoundException("Employment", "id", id));

        this.employmentRepository.deleteById(id);
        return true;
    }
}
