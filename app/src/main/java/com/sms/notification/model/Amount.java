package com.sms.notification.model;

import java.text.DecimalFormat;
import java.util.Currency;

/**
 * Created by ruslan on 11/9/17.
 */

public class Amount {
    public int amount;
    public Currency currency;

    @Override
    public String toString() {
        DecimalFormat df = new DecimalFormat("0");
        df.setMaximumFractionDigits(2);
        return df.format((float)amount / (int)Math.pow(10, currency.getDefaultFractionDigits())) + " " + currency;
    }
}
