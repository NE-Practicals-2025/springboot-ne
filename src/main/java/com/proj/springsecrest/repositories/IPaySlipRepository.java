package com.proj.springsecrest.repositories;

import com.proj.springsecrest.models.Employee;
import com.proj.springsecrest.models.PaySlip;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface IPaySlipRepository extends JpaRepository<PaySlip, UUID> {

    Optional<PaySlip> findById(UUID code);

    @Query("SELECT p FROM PaySlip p WHERE p.employee.code = :employeeCode")
    Page<PaySlip> findByEmployeeCode(@Param("employeeCode") UUID employeeCode, Pageable pageable);

    @Query("SELECT p FROM PaySlip p WHERE p.month = :month AND p.year = :year")
    Page<PaySlip> findByMonthAndYear(
            @Param("month") int month,
            @Param("year") int year,
            Pageable pageable);

    @Query("SELECT p FROM PaySlip p WHERE p.employee.code = :employeeCode AND p.month = :month AND p.year = :year")
    Optional<PaySlip> findByEmployeeAndMonthAndYear(
            @Param("employeeCode") UUID employeeCode,
            @Param("month") int month,
            @Param("year") int year);

    @Query("SELECT p FROM PaySlip p WHERE p.status = 'PENDING'")
    Page<PaySlip> findAllPending(Pageable pageable);

    @Query("SELECT p FROM PaySlip p WHERE p.status = 'PAID'")
    Page<PaySlip> findAllPaid(Pageable pageable);

    boolean existsByEmployeeAndMonthAndYear(Employee employee, int month, int year);
}