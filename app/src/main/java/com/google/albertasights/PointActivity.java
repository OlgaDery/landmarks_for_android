package com.google.albertasights;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashSet;

public class PointActivity extends AppCompatActivity {
    private Place point;
    private ListView listview;
    private ImageView photo;
    private ImageButton closeButton;

 //   ArrayList <String> listItemsValue = new ArrayList<>(4);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_point);

        if (getIntent().getExtras()!= null) {
            Bundle extras = getIntent().getExtras();
            point = (Place) extras.getSerializable("POINT");
        }
  //      listItemsValue.clear();

//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
        listview = (ListView) findViewById(R.id.listView1);
        photo = (ImageView) findViewById(R.id.img);
        closeButton = (ImageButton) findViewById(R.id.close);
        closeButton.setImageResource(R.drawable.close);

        if (point.getPhotoLink()!=null && point.getPhotoLink().length()>5)
        {
            Picasso.with(getApplicationContext())
                    .load(UiUtils.parseUrl(point.getPhotoLink()))
                    .fit()
                    .into(photo);

        } else
        {
            Picasso.with(getApplicationContext())
                    .load(R.drawable.no_ph)
                    .into(photo);
        }

//        listItemsValue.add(point.getName());
//        listItemsValue.add(point.getDescript());
//        listItemsValue.add(point.getWebLink());
//        listItemsValue.add(String.valueOf(point.getRating()));

        PointListviewAdapter adapter = new PointListviewAdapter(point, getApplicationContext());
        listview.setAdapter(adapter);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

}
