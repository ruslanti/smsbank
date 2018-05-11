package com.sms.notification.model;

import android.arch.persistence.room.Ignore;

import java.text.DecimalFormat;
import java.util.Currency;

/**
 * Created by ruslan on 11/9/17.
 */

public class Amount {
    public int amount;
    public Currency currency;

    public Amount() {
    }
    @Ignore
    public Amount(int amount, Currency currency) {
        this.amount = amount;
        this.currency = currency;
    }

    @Override
    public String toString() {
        DecimalFormat df = new DecimalFormat("0");
        df.setMaximumFractionDigits(2);
        return df.format((float)amount / (int)Math.pow(10, currency.getDefaultFractionDigits())) + " " + currency;
    }
}
