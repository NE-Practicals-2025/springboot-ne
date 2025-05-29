package com.proj.springsecrest.models;
import org.springframework.context.ApplicationEvent;

public class PayslipPaidEvent extends ApplicationEvent {
    private final PaySlip payslip;

    public PayslipPaidEvent(Object source, PaySlip payslip) {
        super(source);
        this.payslip = payslip;
    }

    public PaySlip getPayslip() {
        return payslip;
    }
}