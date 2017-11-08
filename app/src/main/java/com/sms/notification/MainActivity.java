package com.sms.notification;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.sms.notification.model.Operation;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "MainActivity";

    private List<Operation> movieList = new ArrayList<>();
    private OperationsViewModel viewModel;
    private RecyclerView mRecyclerView;
    private MoviesAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        Operation operation = new Operation();
        operation.card = "card";
        operation.suma = "10";
        operation.desc = "desc";
        movieList.add(operation);
        mAdapter = new MoviesAdapter(movieList);
        mRecyclerView.setAdapter(mAdapter);

        viewModel = ViewModelProviders.of(this).get(OperationsViewModel.class);

        viewModel.getItemAndPersonList().observe(MainActivity.this, new Observer<List<Operation>>() {
            @Override
            public void onChanged(@Nullable List<Operation> itemAndPeople) {
                mAdapter.addItems(itemAndPeople);
            }
        });

        new DatabaseInitAsyncTask(AppDatabase.getDatabase(getApplicationContext()))
                .execute(getContentResolver());
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
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

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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

            if (db.operationDao().getAll().getValue() != null && !db.operationDao().getAll().getValue().isEmpty())
                return null;

            Log.d(TAG, "initialise database");

            Uri mSmsQueryUri = Uri.parse("content://sms/inbox");
            List<String> messages = new ArrayList<String>();

            Cursor cursor = null;
            try {
                cursor = resolver[0].query(mSmsQueryUri, new String[]{"body"}, "address=102", null, null);
                if (cursor != null) {
                    for (boolean hasData = cursor.moveToFirst(); hasData; hasData = cursor.moveToNext()) {
                        final String body = cursor.getString(cursor.getColumnIndexOrThrow("body"));
                        //messages.add(body);
                        Log.d(TAG, body);
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            } finally {
                if (cursor != null)
                    cursor.close();
            }
            return null;
        }
    }
}
