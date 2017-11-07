package com.sms.notification.model;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

/**
 * Created by ruslan on 11/7/17.
 */
@Dao
public interface CardDao {
    @Query("SELECT * FROM cards")
    List<Card> getAll();

    @Insert
    void insert(Card... cards);

    @Delete
    void delete(Card card);
}
