package com.google.albertasights.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.albertasights.R;

public class MenuActivity extends AppCompatActivity {

    private static final String TAG = MenuActivity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(TAG, "exit onCreateOptionsMenu(Menu menu)");
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu); //your file name
        //  Log.i(TAG, menu.findItem(R.id.config).getTitle().toString());
        //   modifyMenuItem (getClass().getSimpleName(), menu);

        Log.d(TAG, "exit onCreateOptionsMenu(Menu menu)");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        //   this.unregisterReceiver(this.receiver);
    return true;
    }

}
