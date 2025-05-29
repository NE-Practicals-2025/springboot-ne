package com.proj.springsecrest.services.serviceImpls;

import com.proj.springsecrest.enums.EEmployeementStatus;
import com.proj.springsecrest.enums.EPaySlipStatus;
import com.proj.springsecrest.events.PaySlipApprovedEvent;
import com.proj.springsecrest.exceptions.BadRequestException;
import com.proj.springsecrest.exceptions.ResourceNotFoundException;
import com.proj.springsecrest.models.*;
import com.proj.springsecrest.payload.request.GeneratePaySlipDTO;
import com.proj.springsecrest.payload.request.UpdatePaySlipStatusDTO;
import com.proj.springsecrest.repositories.*;
import com.proj.springsecrest.services.IPaySlipService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.Year;
import java.time.YearMonth;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaySlipServiceImpl implements IPaySlipService {

    private final IPaySlipRepository paySlipRepository;
    private final IEmployeeRepository employeeRepository;
    private final IEmploymentRepository employmentRepository;
    private final IDeductionRepository deductionRepository;

    private final ApplicationEventPublisher eventPublisher;
    @Override
    public Page<PaySlip> getAll(Pageable pageable) {
        return paySlipRepository.findAll(pageable);
    }

    @Override
    public PaySlip getByCode(UUID code) {
        return paySlipRepository.findById(code)
                .orElseThrow(() -> new ResourceNotFoundException("PaySlip", "id", code.toString()));
    }

    @Override
    @Transactional
    public PaySlip generatePaySlip(GeneratePaySlipDTO dto) {
        Employee employee = employeeRepository.findByCode(dto.getEmployeeCode())
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "code", dto.getEmployeeCode()));

        Employment employment = employmentRepository.findByEmployeeCode(dto.getEmployeeCode())
                .orElseThrow(() -> new ResourceNotFoundException("Employment", "employee code", dto.getEmployeeCode()));

        // Check if employee is active
        if (!employment.getStatus().equals(EEmployeementStatus.ACTIVE)) {
            throw new BadRequestException("Cannot generate payslip for inactive employee");
        }

        // Check if payslip already exists for this month/year
        if (paySlipRepository.existsByEmployeeAndMonthAndYear((employee), dto.getMonth(), dto.getYear())) {
            throw new BadRequestException(String.format(
                    "PaySlip already exists for employee %s for %d/%d",
                    employee.getFirstName(), dto.getMonth(), dto.getYear()));
        }

        // Calculate salary components
        SalaryCalculationResult calculation = calculateSalary(employment.getBaseSalary());

        // Create and save payslip
        PaySlip paySlip = new PaySlip();
        paySlip.setEmployee(employee);
        paySlip.setMonth(dto.getMonth());
        paySlip.setYear(dto.getYear());
        paySlip.setHouseAmount(calculation.getHousing());
        paySlip.setTransportAmount(calculation.getTransport());
        paySlip.setEmployeeTaxedAmount(calculation.getTax());
        paySlip.setPensionAmount(calculation.getPension());
        paySlip.setMedicalInsuranceAmount(calculation.getMedical());
        paySlip.setOtherTaxedAmount(calculation.getOthers());
        paySlip.setGrossSalary(calculation.getGrossSalary());
        paySlip.setNetSalary(calculation.getNetSalary());
        paySlip.setStatus(EPaySlipStatus.PENDING);
        paySlip.setCreatedAt(LocalDateTime.now());

        return paySlipRepository.save(paySlip);
    }

    @Override
    @Transactional
    public List<PaySlip> generatePayrollForAllActiveEmployees(int month, int year) {
        List<Employment> activeEmployments = employmentRepository.findByStatus(EEmployeementStatus.ACTIVE);

        return activeEmployments.stream()
                .map(employment -> {
                    GeneratePaySlipDTO dto = new GeneratePaySlipDTO();
                    dto.setEmployeeCode(employment.getEmployee().getCode());
                    dto.setMonth(month);
                    dto.setYear(year);

                    try {
                        return generatePaySlip(dto);
                    } catch (Exception e) {
                        log.error("Failed to generate payslip for employee {}: {}",
                                employment.getEmployee().getCode(), e.getMessage());
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public PaySlip getByEmployeeAndPeriod(UUID employeeCode, int month, int year) {
        return null;
    }

    private SalaryCalculationResult calculateSalary(double baseSalary) {
        // Get all active deductions
        List<Deduction> deductions = deductionRepository.findAll();

        // Calculate benefits (additions)
        double housing = baseSalary * (getDeductionPercentage(deductions, "Housing") / 100);
        double transport = baseSalary * (getDeductionPercentage(deductions, "Transport") / 100);
        double grossSalary = baseSalary + housing + transport;

        // Calculate deductions
        double tax = baseSalary * (getDeductionPercentage(deductions, "EmployeeTax") / 100);
        double pension = baseSalary * (getDeductionPercentage(deductions, "Pension") / 100);
        double medical = baseSalary * (getDeductionPercentage(deductions, "MedicalInsurance") / 100);
        double others = baseSalary * (getDeductionPercentage(deductions, "Others") / 100);

        double totalDeductions = tax + pension + medical + others;
        double netSalary = grossSalary - totalDeductions;

        return new SalaryCalculationResult(
                housing, transport, grossSalary,
                tax, pension, medical, others, netSalary
        );
    }

    private double getDeductionPercentage(List<Deduction> deductions, String name) {
        return deductions.stream()
                .filter(d -> d.getDeductionName().equalsIgnoreCase(name))
                .findFirst()
                .map(Deduction::getPercentage)
                .orElseThrow(() -> new IllegalStateException("Deduction " + name + " not found"));
    }
    @Override
    public Page<PaySlip> getByEmployee(UUID employeeCode, Pageable pageable) {
        return paySlipRepository.findByEmployeeCode(employeeCode, pageable);
    }

    @Override
    public Page<PaySlip> getByMonthAndYear(int month, int year, Pageable pageable) {
        return paySlipRepository.findByMonthAndYear(month, year, pageable);
    }

    @Override
    @Transactional
    public PaySlip approvePaySlip(UUID code) {
        PaySlip paySlip = paySlipRepository.findById(code)
                .orElseThrow(() -> new ResourceNotFoundException("PaySlip", "code", code.toString()));

        if (paySlip.getStatus() == EPaySlipStatus.PAID) {
            throw new BadRequestException("PaySlip is already approved");
        }

        paySlip.setStatus(EPaySlipStatus.PAID);
        PaySlip updatedPaySlip = paySlipRepository.save(paySlip);

        // Publish event
        eventPublisher.publishEvent(new PaySlipApprovedEvent(this, updatedPaySlip));

        return updatedPaySlip;
    }
    @Override
    public Page<PaySlip> getAllPending(Pageable pageable) {
        return paySlipRepository.findAllPending(pageable);
    }

    @Override
    public Page<PaySlip> getAllPaid(Pageable pageable) {
        return paySlipRepository.findAllPaid(pageable);
    }
}