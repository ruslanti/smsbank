package com.sms.notification.model;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.Date;
import java.util.List;

/**
 * Created by ruslan on 11/7/17.
 */
@Dao
public interface OperationDao {
    @Query("SELECT * FROM operations")
    LiveData<List<Operation>> getAll();

    @Query("SELECT * FROM operations WHERE data > :from")
    List<Operation> findOperationsFromDate(Date from);

    @Insert
    void insert(Operation... operations);

    @Delete
    void delete(Operation operation);
}
