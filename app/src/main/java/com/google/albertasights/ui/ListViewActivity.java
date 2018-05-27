package com.google.albertasights.ui;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.albertasights.R;
import com.google.albertasights.models.Place;

import java.util.ArrayList;

public class ListViewActivity extends AppCompatActivity {
    ArrayList<String> placesLst;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_view);
        if (getIntent().getExtras()!= null) {
            placesLst = (ArrayList)getIntent().getStringArrayListExtra(UiUtils.PLACES);
        }

        ListView listView = (ListView) findViewById(R.id.mobile_list);
        ArrayAdapter adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, placesLst);
        listView.setAdapter(adapter);


    }

}
