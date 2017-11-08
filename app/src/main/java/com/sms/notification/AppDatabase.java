package com.sms.notification;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;

import com.sms.notification.model.Card;
import com.sms.notification.model.CardDao;
import com.sms.notification.model.Converters;
import com.sms.notification.model.Operation;
import com.sms.notification.model.OperationDao;

/**
 * Created by ruslan on 11/7/17.
 */
@Database(entities = {Operation.class, Card.class}, version = 1, exportSchema = false)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {
    private static AppDatabase instance;

    public static AppDatabase getDatabase(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, "smsbank_db").build();
        }
        return instance;
    }

    public abstract OperationDao operationDao();
    public abstract CardDao cardDao();
}
