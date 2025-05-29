package com.proj.springsecrest.controllers;

import com.proj.springsecrest.enums.EPaySlipStatus;
import com.proj.springsecrest.exceptions.BadRequestException;
import com.proj.springsecrest.helpers.Constants;
import com.proj.springsecrest.models.PaySlip;
import com.proj.springsecrest.payload.request.GeneratePaySlipDTO;
import com.proj.springsecrest.payload.request.UpdatePaySlipStatusDTO;
import com.proj.springsecrest.payload.response.ApiResponse;
import com.proj.springsecrest.services.IPaySlipService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/payslips")
public class PaySlipController {

    private final IPaySlipService paySlipService;

    public PaySlipController(IPaySlipService paySlipService) {
        this.paySlipService = paySlipService;
    }

    @GetMapping
    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> getAllPaySlips(
            @RequestParam(value = "page", defaultValue = Constants.DEFAULT_PAGE_NUMBER) int page,
            @RequestParam(value = "size", defaultValue = Constants.DEFAULT_PAGE_SIZE) int limit) {

        Pageable pageable = PageRequest.of(page, limit, Sort.Direction.DESC, "createdAt");
        Page<PaySlip> paySlips = paySlipService.getAll(pageable);
        return ResponseEntity.ok(ApiResponse.success("PaySlips fetched successfully", paySlips));
    }

    @GetMapping("/{code}")
    @PreAuthorize("hasRole('EMPLOYEE') or hasRole('MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> getPaySlipByCode(@PathVariable UUID code) {
        PaySlip paySlip = paySlipService.getByCode(code);
        return ResponseEntity.ok(ApiResponse.success("PaySlip fetched successfully", paySlip));
    }

    @PostMapping("/generate")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<ApiResponse> generatePaySlip(@RequestBody @Valid GeneratePaySlipDTO dto) {
        PaySlip paySlip = paySlipService.generatePaySlip(dto);
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/v1/payslips/" + paySlip.getId()).toUriString());
        return ResponseEntity.created(uri).body(ApiResponse.success("PaySlip generated successfully", paySlip));
    }

    @GetMapping("/employee/{employeeCode}")
    @PreAuthorize("hasRole('EMPLOYEE') or hasRole('MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> getPaySlipsByEmployee(
            @PathVariable UUID employeeCode,
            @RequestParam(value = "page", defaultValue = Constants.DEFAULT_PAGE_NUMBER) int page,
            @RequestParam(value = "size", defaultValue = Constants.DEFAULT_PAGE_SIZE) int limit) {

        Pageable pageable = PageRequest.of(page, limit, Sort.Direction.DESC, "createdAt");
        Page<PaySlip> paySlips = paySlipService.getByEmployee(employeeCode, pageable);
        return ResponseEntity.ok(ApiResponse.success("Employee PaySlips fetched successfully", paySlips));
    }

    @GetMapping("/period")
    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> getPaySlipsByPeriod(
            @RequestParam int month,
            @RequestParam int year,
            @RequestParam(value = "page", defaultValue = Constants.DEFAULT_PAGE_NUMBER) int page,
            @RequestParam(value = "size", defaultValue = Constants.DEFAULT_PAGE_SIZE) int limit) {

        Pageable pageable = PageRequest.of(page, limit, Sort.Direction.DESC, "createdAt");
        Page<PaySlip> paySlips = paySlipService.getByMonthAndYear(month, year, pageable);
        return ResponseEntity.ok(ApiResponse.success("Period PaySlips fetched successfully", paySlips));
    }

    @PutMapping("/{code}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> approvePaySlip(
            @PathVariable UUID code) {
        PaySlip paySlip = paySlipService.approvePaySlip(code);
        return ResponseEntity.ok(ApiResponse.success("PaySlip approved successfully", paySlip));
    }

    @GetMapping("/pending")
    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> getPendingPaySlips(
            @RequestParam(value = "page", defaultValue = Constants.DEFAULT_PAGE_NUMBER) int page,
            @RequestParam(value = "size", defaultValue = Constants.DEFAULT_PAGE_SIZE) int limit) {

        Pageable pageable = PageRequest.of(page, limit, Sort.Direction.DESC, "createdAt");
        Page<PaySlip> paySlips = paySlipService.getAllPending(pageable);
        return ResponseEntity.ok(ApiResponse.success("Pending PaySlips fetched successfully", paySlips));
    }

    @GetMapping("/paid")
    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> getPaidPaySlips(
            @RequestParam(value = "page", defaultValue = Constants.DEFAULT_PAGE_NUMBER) int page,
            @RequestParam(value = "size", defaultValue = Constants.DEFAULT_PAGE_SIZE) int limit) {

        Pageable pageable = PageRequest.of(page, limit, Sort.Direction.DESC, "createdAt");
        Page<PaySlip> paySlips = paySlipService.getAllPaid(pageable);
        return ResponseEntity.ok(ApiResponse.success("Paid PaySlips fetched successfully", paySlips));
    }

    @PostMapping("/generate-payroll")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<ApiResponse> generatePayrollForAll(
            @RequestParam int month,
            @RequestParam int year) {

        List<PaySlip> generatedPaySlips = paySlipService.generatePayrollForAllActiveEmployees(month, year);
        return ResponseEntity.ok(ApiResponse.success(
                String.format("Generated %d payslips for %d/%d", generatedPaySlips.size(), month, year),
                generatedPaySlips));
    }

    @GetMapping("/employee/{employeeCode}/period")
    @PreAuthorize("hasRole('EMPLOYEE') or hasRole('MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> getEmployeePaySlipForPeriod(
            @PathVariable UUID employeeCode,
            @RequestParam int month,
            @RequestParam int year) {

        PaySlip paySlip = paySlipService.getByEmployeeAndPeriod(employeeCode, month, year);
        return ResponseEntity.ok(ApiResponse.success("PaySlip fetched successfully", paySlip));
    }

    @GetMapping("/period/all")
    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> getAllPaySlipsForPeriod(
            @RequestParam int month,
            @RequestParam int year,
            @RequestParam(value = "page", defaultValue = Constants.DEFAULT_PAGE_NUMBER) int page,
            @RequestParam(value = "size", defaultValue = Constants.DEFAULT_PAGE_SIZE) int limit) {

        Pageable pageable = PageRequest.of(page, limit, Sort.Direction.DESC, "createdAt");
        Page<PaySlip> paySlips = paySlipService.getByMonthAndYear(month, year, pageable);
        return ResponseEntity.ok(ApiResponse.success("PaySlips for period fetched successfully", paySlips));
    }
}