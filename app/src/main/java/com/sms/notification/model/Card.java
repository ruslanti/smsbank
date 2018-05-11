package com.sms.notification.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.util.Currency;
import java.util.Date;

/**
 * Created by ruslan on 11/7/17.
 */
@Entity(tableName = "cards")
public class Card {
    @PrimaryKey
    @NonNull
    public String code;

    public Currency currency;

    @Embedded(prefix = "sold_")
    public Amount available;

    @ColumnInfo(name = "sold_data")
    public Date data;

    public Card(@NonNull String code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return "Card{" +
                "code='" + code + '\'' +
                ", currency=" + currency +
                ", available=" + available +
                ", data=" + data +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Card card = (Card) o;

        if (!code.equals(card.code)) return false;
        return currency != null ? currency.equals(card.currency) : card.currency == null;
    }

    @Override
    public int hashCode() {
        int result = code.hashCode();
        result = 31 * result + (currency != null ? currency.hashCode() : 0);
        return result;
    }
}
