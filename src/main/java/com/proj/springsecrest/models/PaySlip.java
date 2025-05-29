package com.proj.springsecrest.models;

import com.proj.springsecrest.audits.TimestampAudit;
import com.proj.springsecrest.enums.EPaySlipStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Year;
import java.time.YearMonth;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "payslips")
public class PaySlip extends TimestampAudit {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    private Employee employee;

    private double houseAmount;
    private double transportAmount;
    private double employeeTaxedAmount;
    private double pensionAmount;
    private double medicalInsuranceAmount;
    private double otherTaxedAmount;
    private double grossSalary;
    private double netSalary;
    private int month;
    private int year;
    @Enumerated(EnumType.STRING)
    private EPaySlipStatus status;
}