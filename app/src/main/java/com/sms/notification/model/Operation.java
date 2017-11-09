package com.sms.notification.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "operations",
        indices = {@Index("data"), @Index("card")},
        foreignKeys = @ForeignKey(entity = Card.class, parentColumns = "code", childColumns = "card"))
public class Operation {
    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "op")
    public Op op;

    @ColumnInfo(name = "card")
    public String card;

    @ColumnInfo(name = "status")
    public Status status;

    @Embedded(prefix = "suma_")
    public Amount suma;

    @ColumnInfo(name = "disp")
    public int disp;

    @ColumnInfo(name = "data")
    public Date data;

    @ColumnInfo(name = "desc")
    public String desc;

    @Override
    public String toString() {
        return "Operation{" +
                "id=" + id +
                ", op=" + op +
                ", card='" + card + '\'' +
                ", status=" + status +
                ", suma='" + suma + '\'' +
                ", disp='" + disp + '\'' +
                ", data=" + data +
                ", desc='" + desc + '\'' +
                '}';
    }
}
