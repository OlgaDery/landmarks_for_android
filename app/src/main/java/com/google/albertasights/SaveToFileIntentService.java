package com.google.albertasights;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.util.Log;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class SaveToFileIntentService extends IntentService {
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    public static final String SAVE_TO_FILE = "save_to_file";
    public static final String POINT_ID = "point_id";
    private static final String TAG = SaveToFileIntentService.class.getSimpleName();

    public SaveToFileIntentService() {

        super("SaveToFileIntentService");
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "enter onHandleIntent(Intent intent)");
        if (intent != null) {
            final String action = intent.getAction();
            if (SAVE_TO_FILE.equals(action)) {
                final String param1 = intent.getStringExtra(POINT_ID);
                writeToFile(param1);
            }
        }
        Log.d(TAG, "exit onHandleIntent(Intent intent)");
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void writeToFile(String param1) {
        // TODO: Handle action Foo
        Log.d(TAG, "exit writeToFile(String param1)");
        String filename = "points";
        //TODO temporary
        UiUtils.selectedPointsIds.add(param1);
        Intent i = new Intent();
        i.setAction("POINT_ADDED");
        i.putExtra("LOVED", param1);
        getApplicationContext().sendBroadcast(i);
//        String fileContents = param1.concat("; ");
//        FileOutputStream outputStream;
//
//        try {
//            outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
//            outputStream.write(fileContents.getBytes());
//            outputStream.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }


}
