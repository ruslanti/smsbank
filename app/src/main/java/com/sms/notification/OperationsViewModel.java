package com.sms.notification;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.os.AsyncTask;
import android.util.Log;

import com.sms.notification.model.Amount;
import com.sms.notification.model.Card;
import com.sms.notification.model.CardDao;
import com.sms.notification.model.Op;
import com.sms.notification.model.Operation;
import com.sms.notification.model.OperationDao;

import java.util.List;

/**
 * Created by ruslan on 11/8/17.
 */

public class OperationsViewModel extends AndroidViewModel {
    private static final String TAG = "OperationsViewModel";
    private AppDatabase appDatabase;
    private final MutableLiveData<String> filterInput = new MutableLiveData();

    private final LiveData<List<Operation>> operationsList;


    public OperationsViewModel(Application application) {
        super(application);
        appDatabase = AppDatabase.getDatabase(this.getApplication());

        operationsList = Transformations.switchMap(filterInput, (filter) -> {
            if (filter == null || filter.isEmpty() || filter == "All")
                return appDatabase.operationDao().getAll();
            else
                return appDatabase.operationDao().findByCard(filter);
        });
    }

    public LiveData<List<Operation>> getOperationsList() {
        return operationsList;
    }

    public void filterByCard(String card) {
        Log.d(TAG, "filterByCard: "+card);
        filterInput.postValue(card);
    }

    public void addOperations(final Operation op) {
        new addAsyncTask(appDatabase).execute(op);
    }

    private static class addAsyncTask extends AsyncTask<Operation, Void, Void> {

        private AppDatabase db;

        addAsyncTask(AppDatabase appDatabase) {
            db = appDatabase;
        }

        @Override
        protected Void doInBackground(final Operation... operations) {
            OperationDao operationDao = db.operationDao();
            CardDao cardDao = db.cardDao();
            Operation op = operations[0];
            Card card = cardDao.find(op.card);
            if (card == null) {
                if (op.op != Op.SOLD) {
                    card = new Card(op.card);
                    card.currency = op.suma.currency;
                    card.data = op.data;
                    card.available = new Amount(op.disp, card.currency);
                    cardDao.insert(card);
                }
            } else if(card.data.before(op.data)) {
                card.data = op.data;
                if (op.op == Op.SOLD) {
                    op.suma = new Amount(op.suma.amount, card.currency);
                    card.available = op.suma;
                } else
                    card.available = new Amount(op.disp, card.currency);
                cardDao.update(card);
            }

            operationDao.insert(op);
            return null;
        }

    }
}
