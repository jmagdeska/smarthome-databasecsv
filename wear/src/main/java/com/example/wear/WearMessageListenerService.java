package com.example.wear;

import android.content.Intent;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

/**
 * Created by mario on 06.08.16.
 */
public class WearMessageListenerService extends WearableListenerService {
    private static final String PATH_ACTION = "/action";
    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        if( messageEvent.getPath().equalsIgnoreCase(PATH_ACTION) ) {
            Intent intent = new Intent( this, WearMeasure.class );
            intent.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
            startActivity( intent );
        } else {
            super.onMessageReceived(messageEvent);
        }
    }
}
