package com.proj.springsecrest.models;

import com.proj.springsecrest.audits.TimestampAudit;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "deductions")
public class Deduction extends TimestampAudit {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID code;
    private String deductionName;
    private double percentage;
}