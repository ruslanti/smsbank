package com.sms.notification;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import com.sms.notification.model.Operation;

import java.util.List;

/**
 * Created by ruslan on 11/8/17.
 */

public class OperationsViewModel extends AndroidViewModel {
    private final LiveData<List<Operation>> itemAndPersonList;

    private AppDatabase appDatabase;

    public OperationsViewModel(Application application) {
        super(application);

        appDatabase = AppDatabase.getDatabase(this.getApplication());

        itemAndPersonList = appDatabase.operationDao().getAll();
    }


    public LiveData<List<Operation>> getItemAndPersonList() {
        return itemAndPersonList;
    }

    public void deleteItem(Operation borrowModel) {
        new deleteAsyncTask(appDatabase).execute(borrowModel);
    }

    private static class deleteAsyncTask extends AsyncTask<Operation, Void, Void> {

        private AppDatabase db;

        deleteAsyncTask(AppDatabase appDatabase) {
            db = appDatabase;
        }

        @Override
        protected Void doInBackground(final Operation... params) {
            db.operationDao().delete(params[0]);
            return null;
        }

    }
}
