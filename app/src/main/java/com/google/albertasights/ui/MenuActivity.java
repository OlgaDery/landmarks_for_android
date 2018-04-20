package com.google.albertasights.ui;

import android.content.Intent;
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

        //TODO if user is null or it logged out regarding the shared preferences data, remove R.id.log_out


        Log.d(TAG, "exit onCreateOptionsMenu(Menu menu)");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.profile:
                //TODO move this to separate activities start methods

                item.setChecked(true);
                final Intent intent5 = new Intent(this, UserActivity.class);
                startActivity(intent5);
                return true;
            case R.id.log_in:

              //show log in form
                //  Log.d(TAG, "session list has been already created");
                //               }
                item.setChecked(true);
                return true;

            case R.id.map:
                final Intent intent2 = new Intent(this, MapsActivity.class);
                startActivity(intent2);
                item.setChecked(true);
                return true;

            case R.id.log_out:
//                final Intent intent_logOut = new Intent(this, DbIntentService.class);
//                intent_logOut.setAction(DbIntentService.USER_LOGGING_OUT);
//                //    ((User)RuneNames.getUser()).setLoggedIn(false);
//
//                //TODO call the async service to set the user isLoggedIn attribute in DB as FALSE
//                item.setChecked(true);
//                startService(intent_logOut);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }

}
