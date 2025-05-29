package com.proj.springsecrest.services.serviceImpls;


import com.proj.springsecrest.enums.ERole;
import com.proj.springsecrest.enums.EUserStatus;
import com.proj.springsecrest.exceptions.BadRequestException;
import com.proj.springsecrest.exceptions.ResourceNotFoundException;
import com.proj.springsecrest.helpers.MailService;
import com.proj.springsecrest.helpers.Utility;
import com.proj.springsecrest.models.Deduction;
import com.proj.springsecrest.models.Employee;
import com.proj.springsecrest.payload.request.DeductionDTO;
import com.proj.springsecrest.payload.request.UpdateUserDTO;
import com.proj.springsecrest.repositories.IDeductionRepository;
import com.proj.springsecrest.repositories.IEmployeeRepository;
import com.proj.springsecrest.services.IDeductionService;
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
public class DeductionServiceImpl implements IDeductionService {

    private final IDeductionRepository deductionRepository;
    private final MailService mailService;

//    private final IFileService fileService;
//    private final FileStorageService fileStorageService;

    @Override
    public Page<Deduction> getAll(Pageable pageable) {
        return this.deductionRepository.findAll(pageable);
    }

    @Override
    public Deduction create(Deduction deduction) {
        try {
            Optional<Deduction> userOptional = this.deductionRepository.findByDeductionName(deduction.getDeductionName());

            if (userOptional.isPresent())
                throw new BadRequestException(String.format("Deduction with name '%s' already exists", deduction.getDeductionName()));
            return this.deductionRepository.save(deduction);
        } catch (DataIntegrityViolationException ex) {
            String errorMessage = Utility.getConstraintViolationMessage(ex, deduction);
            throw new BadRequestException(errorMessage, ex);
        }
    }

    @Override
    public Deduction update(UUID id, DeductionDTO dto) {
        Deduction deduction = this.deductionRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Deduction", "id", id.toString()));
        deduction.setDeductionName(dto.getDeductionName());
        deduction.setPercentage(dto.getPercentage());
        return this.deductionRepository.save(deduction);
    }

    @Override
    public boolean delete(UUID id) {
        this.deductionRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Deduction", "id", id));

        this.deductionRepository.deleteById(id);
        return true;
    }
}
