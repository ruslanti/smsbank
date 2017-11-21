package com.sms.notification;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.sms.notification.model.Card;
import com.sms.notification.model.CardDao;
import com.sms.notification.model.Operation;
import com.sms.notification.model.OperationDao;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, SmsListener {

    private static final String TAG = "MainActivity";

    private OperationsViewModel viewModel;
    private RecyclerView mRecyclerView;
    private OperationsAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mRecyclerView = findViewById(R.id.my_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this,LinearLayoutManager.VERTICAL));

        // specify an adapter (see also next example)
        mAdapter = new OperationsAdapter(Collections.<Operation>emptyList());
        mRecyclerView.setAdapter(mAdapter);

        viewModel = ViewModelProviders.of(this).get(OperationsViewModel.class);

        viewModel.getItemAndPersonList().observe(MainActivity.this, new Observer<List<Operation>>() {
            @Override
            public void onChanged(@Nullable List<Operation> itemAndPeople) {
                Log.d(TAG, "onChanged "+ itemAndPeople.size());
                mAdapter.addOperations(itemAndPeople);
            }
        });

        new DatabaseInitAsyncTask(AppDatabase.getDatabase(getApplicationContext()))
                .execute(getContentResolver());

        SmsReceiver.bindListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SmsReceiver.unbindListiners();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void messageReceived(String messageText) {
        Log.e(TAG, messageText);
        Toast.makeText(MainActivity.this,"Message: "+messageText, Toast.LENGTH_LONG).show();
        new DatabaseAddAsyncTask().execute(messageText);
    }

    private class DatabaseAddAsyncTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... sms) {
            AppDatabase db = AppDatabase.getDatabase(getApplicationContext());
            MaibOperationFactory factory = new MaibOperationFactory();

            db.beginTransaction();
            OperationDao operationDao = db.operationDao();
            CardDao cardDao = db.cardDao();
            try {
                Operation op = factory.getOperation(sms[0]);
                Log.d(TAG, op.toString());
                operationDao.insert(op);
                db.setTransactionSuccessful();
            } catch (ParseException e) {
                Log.w(TAG, "parse sms error: " + e.getMessage());
            }
            db.endTransaction();
            return null;

        }
    }

    /**
     * Database initialisation task
     * Populate the notification database from old SMS
     */
    private class DatabaseInitAsyncTask extends AsyncTask<ContentResolver, Void, Void> {
        private AppDatabase db;

        public DatabaseInitAsyncTask(AppDatabase database) {
            db = database;
        }

        @Override
        protected Void doInBackground(ContentResolver... resolver) {

            if (db.operationDao().count() > 0)
                return null;

            MaibOperationFactory factory = new MaibOperationFactory();

            Log.d(TAG, "initialise database");

            Uri mSmsQueryUri = Uri.parse("content://sms/inbox");
            Cursor cursor = null;
            try {
                cursor = resolver[0].query(mSmsQueryUri, new String[]{"body"}, "address=102", null, null);
                if (cursor != null) {
                    db.beginTransaction();

                    OperationDao operationDao = db.operationDao();
                    CardDao cardDao = db.cardDao();

                    List<Operation> operations = new ArrayList<>();
                    Set<Card> cards = new HashSet<>();

                    for (boolean hasData = cursor.moveToFirst(); hasData; hasData = cursor.moveToNext()) {
                        try {
                            final String body = cursor.getString(cursor.getColumnIndexOrThrow("body"));
                            Operation op = factory.getOperation(body);
                            Log.d(TAG, op.toString());
                            operations.add(op);
                            cards.add(new Card(op.card));
                        } catch (ParseException e) {
                            Log.w(TAG, "parse sms error: " + e.getMessage());
                        }
                    }

                    cardDao.insert(cards.toArray(new Card[cards.size()]));
                    operationDao.insert(operations.toArray(new Operation[operations.size()]));

                    db.setTransactionSuccessful();
                    db.endTransaction();
                }
            } catch (Exception e) {
                Log.e(TAG, "read sms error: "+e.getMessage());
                throw e;
            } finally {
                if (cursor != null)
                    cursor.close();
            }
            return null;
        }
    }
}
