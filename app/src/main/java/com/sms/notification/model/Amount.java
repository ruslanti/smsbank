package com.sms.notification.model;

import java.util.Currency;

/**
 * Created by ruslan on 11/9/17.
 */

public class Amount {
    public int amount;
    public Currency currency;

    @Override
    public String toString() {
        return "Amount{" +
                "amount=" + amount +
                ", currency=" + currency +
                '}';
    }
}
