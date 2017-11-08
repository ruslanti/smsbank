package com.sms.notification.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

import java.util.Date;

/**
 * Created by ruslan on 11/7/17.
 */
@Entity(tableName = "operations",
        indices = {@Index("data"), @Index("card")},
        foreignKeys = @ForeignKey(entity = Card.class, parentColumns = "code", childColumns = "card"))
public class Operation {
    @PrimaryKey
    public int uid;

    @ColumnInfo(name = "op")
    public Op op;

    @ColumnInfo(name = "card")
    public String card;

    @ColumnInfo(name = "status")
    public Status status;

    @ColumnInfo(name = "suma")
    public String suma;

    @ColumnInfo(name = "disp")
    public String disp;

    @ColumnInfo(name = "data")
    public Date data;

    @ColumnInfo(name = "desc")
    public String desc;
}
