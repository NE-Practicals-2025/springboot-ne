package com.proj.springsecrest.events;

import com.proj.springsecrest.models.PaySlip;
import org.springframework.context.ApplicationEvent;

public class PaySlipApprovedEvent extends ApplicationEvent {
    private final PaySlip paySlip;

    public PaySlipApprovedEvent(Object source, PaySlip paySlip) {
        super(source);
        this.paySlip = paySlip;
    }

    public PaySlip getPaySlip() {
        return paySlip;
    }
}