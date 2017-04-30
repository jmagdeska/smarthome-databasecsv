package com.example.wear;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.wearable.activity.WearableActivity;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;

/**
 * Created by Jana on 4/24/2017.
 */

public class WearSingleMeasurement extends WearableActivity {

    TextView measurementTextView;
    SharedPreferences sharedPrefs;
    private ArrayList<String> measurementList;
    String measurementName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wear_activity_single_measurement);
        measurementTextView = (TextView) findViewById(R.id.measurementTextView);

        Intent intent = getIntent();
        measurementName = intent.getStringExtra("measurementName");

        StringBuilder sb = new StringBuilder(measurementName);
        sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
        measurementTextView.setText(sb.toString());

        populateListView();
    }

    public void populateListView(){

        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        if(sharedPrefs.getStringSet(measurementName.toLowerCase(), null) != null) {
            Set<String> measurementSet = sharedPrefs.getStringSet(measurementName.toLowerCase(), null);
            measurementList = new ArrayList<String>(measurementSet);
            ListView listView = (ListView) findViewById(R.id.listView);
            listView.setAdapter(new ArrayAdapter<String>(WearSingleMeasurement.this,
                    R.layout.custom_list_item, measurementList));
        }
        else Toast.makeText(this, "No previous measurements available!", Toast.LENGTH_LONG).show();
    }
}
