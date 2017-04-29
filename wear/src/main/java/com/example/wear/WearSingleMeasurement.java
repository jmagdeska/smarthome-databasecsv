package com.example.wear;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Set;

/**
 * Created by Jana on 4/24/2017.
 */

public class WearSingleMeasurement extends WearableActivity {

    TextView measurementTextView;
    SharedPreferences sharedPrefs;
    private ArrayList<String> measurementList;
    private ArrayList<Integer> mIcons;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wear_activity_single_measurement);
        measurementTextView = (TextView) findViewById(R.id.measurementTextView);

        Intent intent = getIntent();
        String measurementName = intent.getStringExtra("measurementName");
        measurementTextView.setText(measurementName);

        sharedPrefs =  getSharedPreferences("SharedPrefs", this.MODE_PRIVATE);

        Set<String> measurementSet = sharedPrefs.getStringSet(measurementName, null);

        measurementList = new ArrayList<String>(measurementSet);

        ListView listView = (ListView) findViewById(R.id.listView);
        String[] listArray = (String[]) measurementList.toArray();
        listView.setAdapter(new ArrayAdapter<String>(WearSingleMeasurement.this,
                android.R.layout.simple_list_item_1, listArray));
    }
}
