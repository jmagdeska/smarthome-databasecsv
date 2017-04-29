package com.example.wear;

import android.content.Intent;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.WearableListView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Jana on 4/23/2017.
 */

public class WearMain extends WearableActivity {

    private ArrayList<Integer> mIcons;
    private ArrayList<String> mIconsNames;
    private TextView mHeader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wear_activity_main);

        // Sample icons for the list
        mIcons = new ArrayList<Integer>();
        mIconsNames = new ArrayList<String>();
        mIcons.add(R.drawable.overview);
        mIcons.add(R.drawable.measure);
        mIconsNames.add("Overview");
        mIconsNames.add("Measure");

        mHeader = (TextView) findViewById(R.id.header);

        WearableListView wearableListView =
                (WearableListView) findViewById(R.id.wearable_List);
        wearableListView.setAdapter(new WearableAdapter(getApplicationContext(), mIcons, mIconsNames));
        wearableListView.setClickListener(mClickListener);
        wearableListView.addOnScrollListener(mOnScrollListener);
    }

    // Handle our Wearable List's click events
    private WearableListView.ClickListener mClickListener =
            new WearableListView.ClickListener() {
                @Override
                public void onClick(WearableListView.ViewHolder viewHolder) {
                    if(viewHolder.getLayoutPosition() == 0) {
                        Intent intent = new Intent(getApplicationContext(), CustomWearableList.class);
                        startActivity(intent);
                    }
                    else if(viewHolder.getLayoutPosition() == 1){
                        Intent intent = new Intent(getApplicationContext(), WearCommunication.class);
                        startActivity(intent);
                    }
                }

                @Override
                public void onTopEmptyRegionClick() {

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
