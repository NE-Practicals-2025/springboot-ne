package com.proj.springsecrest.services;

import com.proj.springsecrest.models.PaySlip;
import com.proj.springsecrest.payload.request.GeneratePaySlipDTO;
import com.proj.springsecrest.payload.request.UpdatePaySlipStatusDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface IPaySlipService {

    Page<PaySlip> getAll(Pageable pageable);

    PaySlip getByCode(UUID code);

    PaySlip generatePaySlip(GeneratePaySlipDTO dto);

    Page<PaySlip> getByEmployee(UUID employeeCode, Pageable pageable);

    Page<PaySlip> getByMonthAndYear(int month, int year, Pageable pageable);

    PaySlip approvePaySlip(UUID code);

    Page<PaySlip> getAllPending(Pageable pageable);

    Page<PaySlip> getAllPaid(Pageable pageable);

    List<PaySlip> generatePayrollForAllActiveEmployees(int month, int year);

    PaySlip getByEmployeeAndPeriod(UUID employeeCode, int month, int year);
}