package com.sms.notification.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

/**
 * Created by ruslan on 11/7/17.
 */
@Entity(tableName = "cards")
public class Card {
    @PrimaryKey
    @NonNull
    public String code;
}
