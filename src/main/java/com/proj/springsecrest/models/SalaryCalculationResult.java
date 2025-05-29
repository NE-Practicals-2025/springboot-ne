package com.proj.springsecrest.models;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SalaryCalculationResult {
    private double housing;
    private double transport;
    private double grossSalary;
    private double tax;
    private double pension;
    private double medical;
    private double others;
    private double netSalary;
}