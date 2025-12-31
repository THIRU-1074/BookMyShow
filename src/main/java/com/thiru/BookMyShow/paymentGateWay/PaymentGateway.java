package com.thiru.BookMyShow.paymentGateWay;

import org.springframework.stereotype.Service;

import com.thiru.BookMyShow.userMgmt.*;

@Service
public class PaymentGateway {

    public boolean charge(UserEntity user, int seats, Double amount) {
        // dummy logic
        return Math.random() > 0.3; // 70% success
    }
}
