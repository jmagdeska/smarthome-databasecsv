package com.example.wear;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.support.wearable.view.WearableListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class CustomWearableList extends Activity {
    private ArrayList<Integer> mIcons;
    private ArrayList<String> mIconsNames;
    private TextView mHeader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wear_activity_measurements);

        // Sample icons for the list
        mIcons = new ArrayList<Integer>();
        mIconsNames = new ArrayList<String>();
        mIcons.add(R.drawable.temperature);
        mIcons.add(R.drawable.light);
        mIcons.add(R.drawable.humidity);
        mIconsNames.add("Temperature");
        mIconsNames.add("Light");
        mIconsNames.add("Humidity");

        WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                // This is our list header
                mHeader = (TextView) findViewById(R.id.header);

                WearableListView wearableListView =
                        (WearableListView) findViewById(R.id.wearable_List);
                wearableListView.setAdapter(new WearableAdapter(getApplicationContext(), mIcons, mIconsNames));
                wearableListView.setClickListener(mClickListener);
                wearableListView.addOnScrollListener(mOnScrollListener);
            }
        });
    }

    // Handle our Wearable List's click events
    private WearableListView.ClickListener mClickListener =
            new WearableListView.ClickListener() {
                @Override
                public void onClick(WearableListView.ViewHolder viewHolder) {
                    Intent intent = new Intent(getApplicationContext(), WearSingleMeasurement.class);
                    intent.putExtra("measurementName", mIconsNames.get(viewHolder.getLayoutPosition()));
                    startActivity(intent);
                }

                @Override
                public void onTopEmptyRegionClick() {
                    Toast.makeText(CustomWearableList.this,
                            "Top empty area tapped", Toast.LENGTH_SHORT).show();
                }
            };

    // The following code ensures that the title scrolls as the user scrolls up
    // or down the list
    private WearableListView.OnScrollListener mOnScrollListener =
            new WearableListView.OnScrollListener() {
                @Override
                public void onAbsoluteScrollChange(int i) {
                    // Only scroll the title up from its original base position
                    // and not down.
                    if (i > 0) {
                        mHeader.setY(-i);
                    }
                }

                @Override
                public void onScroll(int i) {
                    // Placeholder
                }

                @Override
                public void onScrollStateChanged(int i) {
                    // Placeholder
                }

                @Override
                public void onCentralPositionChanged(int i) {
                    // Placeholder
                }
            };
}