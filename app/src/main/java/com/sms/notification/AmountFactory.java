package com.sms.notification;

import com.sms.notification.model.Amount;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.util.Currency;
import java.util.Locale;

/**
 * Created by ruslan on 11/9/17.
 */

public class AmountFactory {
    private DecimalFormatSymbols symbols = new DecimalFormatSymbols();
    private DecimalFormat format = new DecimalFormat("0.#");

    AmountFactory() {
        symbols.setDecimalSeparator(',');
        format.setDecimalFormatSymbols(symbols);
    }

    public Amount getAmount(String value) throws ParseException {
        Amount amount = new Amount();
        float f;
        int pos = value.indexOf(' ');
        if (pos > 0) {
            amount.currency = Currency.getInstance(value.substring(pos + 1).trim());
            f = format.parse(value.substring(0, pos)).floatValue();
        } else {
            amount.currency = Currency.getInstance(Locale.getDefault());
            f = format.parse(value).floatValue();
        }
        amount.amount = Math.round(f * (int)Math.pow(10, amount.currency.getDefaultFractionDigits()));
        return amount;
    }
}
