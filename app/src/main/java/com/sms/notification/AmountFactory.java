package com.sms.notification;

import com.sms.notification.model.Amount;

import java.text.ParseException;
import java.util.Currency;
import java.util.Locale;

/**
 * Created by ruslan on 11/9/17.
 */

public class AmountFactory {

    AmountFactory() {
    }

    public Amount getAmount(String value) throws ParseException {
        Amount amount = new Amount();
        float f;
        int pos = value.indexOf(' ');
        if (pos > 0) {
            amount.currency = Currency.getInstance(value.substring(pos + 1).trim());
            f = Float.parseFloat(value.substring(0, pos).replace(',','.'));
        } else {
            amount.currency = Currency.getInstance(Locale.getDefault());
            f = Float.parseFloat(value.replace(',','.'));
        }
        amount.amount = Math.round(f * (int)Math.pow(10, amount.currency.getDefaultFractionDigits()));
        return amount;
    }
}
