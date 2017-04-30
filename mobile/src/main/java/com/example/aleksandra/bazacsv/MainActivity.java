package com.example.aleksandra.bazacsv;

/**
 * Created by aleksandra on 26.04.2017.
 */

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;


public class MainActivity extends ListActivity implements GoogleApiClient.ConnectionCallbacks, MessageApi.MessageListener {

    TextView lbl;
    DBController controller = new DBController(this);
    Button btnimport;
    ListView lv;
    final Context context = this;
    ListAdapter adapter;
    ArrayList<HashMap<String, String>> myList;
    public static final int requestcode = 1;
    private static final String WEAR_MESSAGE_PATH = "/message";
    private static final String PHONE_MESSAGE_PATH = "/pmessage";
    private static final String START_PHONE_ACTIVITY = "/start_phone_activity";
    private GoogleApiClient mApiClient;
    private String measurementText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lbl = (TextView) findViewById(R.id.txtresulttext);
        btnimport = (Button) findViewById(R.id.btnupload);
        lv = getListView();

        btnimport.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent fileintent = new Intent(Intent.ACTION_GET_CONTENT);
                fileintent.setType("*/*");
                try {
                    startActivityForResult(fileintent, requestcode);
                } catch (ActivityNotFoundException e) {
                    lbl.setText("No activity can handle picking a file. Showing alternatives.");
                }

            }
        });
        myList= controller.getAllProducts();
        if (myList.size() != 0) {
            ListView lv = getListView();
            ListAdapter adapter = new SimpleAdapter(MainActivity.this, myList,
                    R.layout.v, new String[]{"Temperature", "Light", "Humidity"}, new int[]{
                    R.id.Temperature, R.id.Light, R.id.Humidity});
            setListAdapter(adapter);
            lbl.setText("");
        }

        initGoogleApiClient();
    }

    private void initGoogleApiClient() {
        mApiClient = new GoogleApiClient.Builder( this )
                .addApi( Wearable.API )
                .addConnectionCallbacks( this )
                .build();

        mApiClient.connect();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null)
            return;
        switch (requestCode) {
            case requestcode:
                String filepath = data.getData().getPath();
                controller = new DBController(getApplicationContext());
                SQLiteDatabase db = controller.getWritableDatabase();
                String tableName = "proinfo";
                db.execSQL("delete from " + tableName);
                try {
                    if (resultCode == RESULT_OK) {
                        try {
                            FileReader file = new FileReader(filepath);

                            BufferedReader buffer = new BufferedReader(file);
                            ContentValues contentValues = new ContentValues();
                            String line = "";
                            db.beginTransaction();

                            while ((line = buffer.readLine()) != null) {
                                String[] str = line.split(",", 3);  // defining 3 columns with null or blank field //values acceptance
                                //
                                String Temperature = str[0].toString();
                                String Light = str[1].toString();
                                String Humidity = str[2].toString();


                                contentValues.put("Temperature", Temperature);
                                contentValues.put("Light", Light);
                                contentValues.put("Humidity", Humidity);
                                long res = db.insert(tableName, null, contentValues);
                                lbl.setText("Successfully Updated Database.");
                            }
                            db.setTransactionSuccessful();
                            db.endTransaction();
                        } catch (IOException e) {
                            if (db.inTransaction())
                                db.endTransaction();
                            Dialog d = new Dialog(this);
                            d.setTitle(e.getMessage().toString() + "first");
                            d.show();
                            // db.endTransaction();
                        }
                    } else {
                        if (db.inTransaction())
                            db.endTransaction();
                        Dialog d = new Dialog(this);
                        d.setTitle("Only CSV files allowed");
                        d.show();
                    }
                } catch (Exception ex) {
                    if (db.inTransaction())
                        db.endTransaction();

                    Dialog d = new Dialog(this);
                    d.setTitle(ex.getMessage().toString() + "second");
                    d.show();
                    // db.endTransaction();
                }
        }
        myList = controller.getAllProducts();

        if (myList.size() != 0) {
            ListView lv = getListView();
            ListAdapter adapter = new SimpleAdapter(MainActivity.this, myList,
                    R.layout.v, new String[]{"Temperature", "Light", "Humidity"}, new int[]{
                    R.id.Temperature, R.id.Light, R.id.Humidity});
            setListAdapter(adapter);
            lbl.setText("Data Imported");
        }
    }

        @Override
        protected void onDestroy() {
            super.onDestroy();
            if( mApiClient != null )
                mApiClient.unregisterConnectionCallbacks( this );
            mApiClient.disconnect();
        }

        @Override
        public void onConnected(@Nullable Bundle bundle) {
            Wearable.MessageApi.addListener( mApiClient, this );
        }

    @Override
    public void onConnectionSuspended(int i) {

    }

    private void sendAsyncMessage(final String path, final String message) {

        Wearable.NodeApi.getConnectedNodes( mApiClient ).setResultCallback(new ResultCallback<NodeApi.GetConnectedNodesResult>() {
            @Override
            public void onResult(@NonNull NodeApi.GetConnectedNodesResult nodes) {
                for(Node node : nodes.getNodes()) {
                    Wearable.MessageApi.sendMessage(
                            mApiClient, node.getId(), path, message.getBytes() ).setResultCallback(new ResultCallback<MessageApi.SendMessageResult>() {
                        @Override
                        public void onResult(@NonNull MessageApi.SendMessageResult sendMessageResult) {
                            if (sendMessageResult.getStatus().isSuccess()){
                                Toast.makeText(getApplicationContext(), "message sent ", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }


    @Override
    public void onMessageReceived(final MessageEvent messageEvent) {
        runOnUiThread( new Runnable() {
            @Override
            public void run() {
                if( messageEvent.getPath().equalsIgnoreCase(PHONE_MESSAGE_PATH)) {
                    String measurement = null;
                    try {
                        measurement = new String(messageEvent.getData(), "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(MainActivity.this, "message : " + measurement, Toast.LENGTH_SHORT).show();
                    int pos = ThreadLocalRandom.current().nextInt(1, 55 + 1);
                    String [] measurementRow = controller.getRow(pos).split(",");

                    switch (measurement) {
                        case "temperature":
                            measurementText = measurementRow[0];
                            break;
                        case "light":
                            measurementText = measurementRow[1];
                            break;
                        case "humidity":
                            measurementText = measurementRow[2];
                            break;
                    }
                    sendAsyncMessage(WEAR_MESSAGE_PATH, measurementText);
                }
            }
        });
    }
}
