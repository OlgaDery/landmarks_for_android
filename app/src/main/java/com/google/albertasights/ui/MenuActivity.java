package com.google.albertasights.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.albertasights.DBIntentService;
import com.google.albertasights.R;
import com.google.albertasights.models.User;

public class MenuActivity extends AppCompatActivity {

    private static final String TAG = MenuActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "enter onCreate");
        super.onCreate(savedInstanceState);
        Log.d(TAG, "exit onCreate");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(TAG, "exit onCreateOptionsMenu(Menu menu)");
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        if (this.getClass().getCanonicalName().contains("User")) {
            menu.removeItem(R.id.profile);
        } else if (this.getClass().getCanonicalName().contains("Map")) {
            menu.removeItem(R.id.map);
        }

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
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
                if (prefs.contains(UiUtils.EMAIL)) {
                    Log.i(TAG, "user found");
                    User user = new User (prefs.getString(UiUtils.EMAIL, "email"),
                            prefs.getString(UiUtils.PASSWORD, "email"));
                    user.setFirstName(prefs.getString(UiUtils.FIRST_NAME, "Dear Friend"));
                    user.setFirstName(prefs.getString(UiUtils.LAST_NAME, "No"));
                    user.setLoggedIn(prefs.getBoolean(UiUtils.LOGGED_IN, true));
                    user.setRole(prefs.getString(UiUtils.ROLE, "user"));
                    intent5.putExtra(UiUtils.USER, user);
                }
                startActivity(intent5);
                return true;

            case R.id.map:
                final Intent intent2 = new Intent(this, MapsActivity.class);
                startActivity(intent2);
                item.setChecked(true);
                return true;

//            case R.id.log_in:
//                //show log in form
//                final Intent intent3 = new Intent(this, UserActivity.class);
//                startActivity(intent3);
//
//                item.setChecked(true);
//                return true;
//
//            case R.id.log_out:
//                //Right now user is logging out only on the device level
//                SharedPreferences.Editor editor = UiUtils.getEditor(this);
//                editor.putBoolean(UiUtils.LOGGED_IN, false);
//                editor.commit();
//                item.setChecked(true);
//                if (this.getClass().getCanonicalName().contains("User")) {
//                    this.recreate();
//                }
//                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }

}
