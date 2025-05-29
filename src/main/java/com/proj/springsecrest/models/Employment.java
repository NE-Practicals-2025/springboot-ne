package com.proj.springsecrest.models;

import com.proj.springsecrest.audits.TimestampAudit;
import com.proj.springsecrest.enums.EEmployeementStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "employments")
public class Employment extends TimestampAudit {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID code;

    @OneToOne
    @JoinColumn(name = "employee_id")
    private Employee employee;

    private String department;
    private String position;
    private double baseSalary;
    @Enumerated(EnumType.STRING)
    private EEmployeementStatus status;
    private LocalDate joiningDate;
}