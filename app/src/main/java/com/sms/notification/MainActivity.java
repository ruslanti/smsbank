package com.sms.notification;

import android.Manifest;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModelProviders;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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
import android.view.View;
import android.widget.Toast;

import com.sms.notification.model.Amount;
import com.sms.notification.model.Card;
import com.sms.notification.model.CardDao;
import com.sms.notification.model.Op;
import com.sms.notification.model.Operation;
import com.sms.notification.model.OperationDao;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, SmsListener {

    private static final String TAG = "MainActivity";
    private static final int PERMISSIONS_REQUEST_READ_SMS = 1;
    private static final int PERMISSIONS_REQUEST_RECEIVE_SMS = 2;

    private OperationsViewModel viewModel;
    private RecyclerView mRecyclerView;
    private OperationsAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private LiveData<List<Card>> cardList;

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

        Menu menu = navigationView.getMenu();
        menu.add( R.id.nav_group, -1, Menu.NONE, "All").setCheckable(true).setIcon(R.drawable.ic_menu_gallery);

        cardList = AppDatabase.getDatabase(getApplicationContext()).cardDao().getAll();
        cardList.observe(MainActivity.this, cards -> {
            for(Card c: cards) {
                Log.d(TAG, c.toString());
                menu.add(R.id.nav_group, -1, Menu.NONE, c.code+"           "+c.available.toString())
                        .setCheckable(true).setIcon(R.drawable.ic_menu_gallery).setTitleCondensed(c.code);
            }
        });

        mRecyclerView = findViewById(R.id.my_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this,LinearLayoutManager.VERTICAL));

        // specify an adapter (see also next example)
        mAdapter = new OperationsAdapter();
        mRecyclerView.setAdapter(mAdapter);

        viewModel = ViewModelProviders.of(this).get(OperationsViewModel.class);

        viewModel.getOperationsList().observe(MainActivity.this, operations -> mAdapter.addOperations(operations));

        viewModel.filterByCard(null);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_SMS)) {
            } else
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_SMS}, PERMISSIONS_REQUEST_READ_SMS);
        } else {
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
            String origin = sharedPref.getString("phone_number", "102");

            new DatabaseInitAsyncTask(AppDatabase.getDatabase(getApplicationContext()), origin)
                    .execute(getContentResolver());
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECEIVE_SMS)) {
            } else
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECEIVE_SMS}, PERMISSIONS_REQUEST_RECEIVE_SMS);
        }else
            SmsReceiver.bindListener(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_READ_SMS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
                    String origin = sharedPref.getString("phone_number", "102");

                    new DatabaseInitAsyncTask(AppDatabase.getDatabase(getApplicationContext()), origin)
                            .execute(getContentResolver());
                }
                return;
            }
            case PERMISSIONS_REQUEST_RECEIVE_SMS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    SmsReceiver.bindListener(this);
                }
                return;

            }
        }
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

        Log.d(TAG, item.toString());
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivities(new Intent[]{intent});
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        viewModel.filterByCard(item.getTitleCondensed().toString());

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void messageReceived(String messageText) {
        //Log.e(TAG, messageText);
        Toast.makeText(MainActivity.this,"Message: "+messageText, Toast.LENGTH_LONG).show();
        MaibOperationFactory factory = new MaibOperationFactory();

        try {
            Operation op = factory.getOperation(messageText);
            Log.d(TAG, op.toString());
            viewModel.addOperations(op);
        } catch (ParseException e) {
            Log.w(TAG, "parse sms error: " + e.getMessage());
            Toast.makeText(MainActivity.this,"SMS parse error: "+e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Database initialisation task
     * Populate the notification database from old SMS
     */
    private class DatabaseInitAsyncTask extends AsyncTask<ContentResolver, Void, Void> {
        private AppDatabase db;
        private String origin;

        public DatabaseInitAsyncTask(AppDatabase database, String origin) {
            this.db = database;
            this.origin = origin;
        }

        @Override
        protected Void doInBackground(ContentResolver... resolver) {
            if (db.operationDao().count() == 0) {

                MaibOperationFactory factory = new MaibOperationFactory();

                Log.d(TAG, "initialise database");

                Uri mSmsQueryUri = Uri.parse("content://sms/inbox");
                Cursor cursor = null;
                try {
                    cursor = resolver[0].query(mSmsQueryUri, new String[]{"body"}, "address="+origin, null, null);
                    if (cursor != null) {
                        db.beginTransaction();

                        OperationDao operationDao = db.operationDao();
                        CardDao cardDao = db.cardDao();

                        List<Operation> operations = new ArrayList<>();
                        Map<String, Card> cards = new HashMap<>();

                        for (boolean hasData = cursor.moveToFirst(); hasData; hasData = cursor.moveToNext()) {
                            try {
                                final String body = cursor.getString(cursor.getColumnIndexOrThrow("body"));
                                Operation op = factory.getOperation(body);
                                Log.d(TAG, op.toString());
                                operations.add(op);
                                Card card = cards.get(op.card);
                                if (card == null) {
                                    card = new Card(op.card);
                                    card.currency = op.suma.currency;
                                    card.data = op.data;
                                    card.available = new Amount(op.disp, card.currency);
                                    cards.put(card.code, card);
                                } else if(card.data.before(op.data)) {
                                    card.data = op.data;
                                    if (op.op == Op.SOLD) {
                                        op.suma = new Amount(op.suma.amount, card.currency);
                                        card.available = op.suma;
                                    } else
                                        card.available = new Amount(op.disp, card.currency);
                                }
                            } catch (ParseException e) {
                                Log.w(TAG, "parse sms error: " + e.getMessage());
                            }
                        }

                        cardDao.insert(cards.values().toArray(new Card[cards.size()]));
                        operationDao.insert(operations.toArray(new Operation[operations.size()]));

                        db.setTransactionSuccessful();
                        db.endTransaction();
                    }
                } catch (Exception e) {
                    Log.e(TAG, "read sms error: " + e.getMessage());
                    throw e;
                } finally {
                    if (cursor != null)
                        cursor.close();
                }
            }

            return null;
        }
    }

    public void onClickOperationDate(View view) {
        //Log.d(TAG, "onClickOperationDate " + view.toString());
    }
}
