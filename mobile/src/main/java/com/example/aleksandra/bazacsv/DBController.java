package com.example.aleksandra.bazacsv;



/**
 * Created by aleksandra on 26.04.2017.
 */
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import java.util.ArrayList;
import java.util.HashMap;

public class DBController extends SQLiteOpenHelper {
    private static final String LOGCAT = null;

    public DBController(Context applicationcontext) {
        super(applicationcontext, "PrdouctDB.db", null, 1);  // creating DATABASE
        Log.d(LOGCAT, "Created");
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        String query;
        query = "CREATE TABLE IF NOT EXISTS proinfo ( Id INTEGER PRIMARY KEY, Temperature TEXT,Light TEXT,Humidity TEXT)";
        database.execSQL(query);
    }


    @Override
    public void onUpgrade(SQLiteDatabase database, int version_old,
                          int current_version) {
        String query;
        query = "DROP TABLE IF EXISTS proinfo";
        database.execSQL(query);
        onCreate(database);
    }

    public ArrayList<HashMap<String, String>> getAllProducts() {
        ArrayList<HashMap<String, String>> proList;
        proList = new ArrayList<HashMap<String, String>>();
        String selectQuery = "SELECT * FROM proinfo";
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("Id", cursor.getString(0));
                map.put("Temperature", cursor.getString(1));
                map.put("Light", cursor.getString(2));
                map.put("Humidity", cursor.getString(3));
                proList.add(map);
            } while (cursor.moveToNext());
        }

        return proList;
    }

    public String getRow(int pos) {
        String selectQuery = "SELECT * FROM proinfo WHERE Id=?";
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, new String[]{String.valueOf(pos)});
        String row = "";

        if (cursor.moveToFirst()) {
            row = cursor.getString(1) + "," + cursor.getString(2) + "," + cursor.getString(3);
        }

        cursor.close();

        return row;
    }

}
