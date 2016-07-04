package com.warmach.browserhistory.browserhistory;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Browser;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends Activity {

    File file;
    String filepath;
    FileOutputStream fileOutputStream;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        filepath = Environment.getExternalStorageDirectory().getPath();
        file = new File(filepath  + "/browser.dat");
        try {
            fileOutputStream = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Uri BOOKMARKS_URI = Uri.parse("content://com.android.chrome.browser/bookmarks");
        String[] HISTORY_PROJECTION = new String[]{
                "title",
                "url",
                "date",
                "visits",
                "created",
                "bookmark"
        };
        String[] data = new String[] { HISTORY_PROJECTION[0],
                HISTORY_PROJECTION[1], HISTORY_PROJECTION[2], HISTORY_PROJECTION[3], HISTORY_PROJECTION[4]};
        String browserHistory = HISTORY_PROJECTION[5] + " = 0";
        Cursor cursor = this.getContentResolver().query(BOOKMARKS_URI, data, browserHistory, null, null);
        cursor.moveToFirst();
        ArrayList<String> array = new ArrayList<>();
        int count = 0;
        if (cursor.moveToFirst() && cursor.getCount() > 0) {
            while (!cursor.isAfterLast()) {
                String title = cursor.getString(cursor
                        .getColumnIndex(HISTORY_PROJECTION[0]));
                String url = cursor.getString(cursor
                        .getColumnIndex(HISTORY_PROJECTION[1]));
                String date = cursor.getString(cursor
                        .getColumnIndex(HISTORY_PROJECTION[2]));
                String visits = cursor.getString(cursor
                        .getColumnIndex(HISTORY_PROJECTION[3]));
                String created = cursor.getString(cursor
                        .getColumnIndex(HISTORY_PROJECTION[4]));
                count++;
                array.add(title + "\t" + url + "\t" + getDate(Long.parseLong(date), "dd/MM/yyyy_hh:mm:ss") + "\t" + visits + "\t" + getDate(Long.parseLong(created), "dd/MM/yyyy_hh:mm:ss"));
                cursor.moveToNext();
            }
        }
        if (array.size() > 0) {
            for (String string : array) {
                writeToFile(string + "\n");
                Log.d("result ", string);
            }
        }
        Log.v("Count:", ""+count);
    }
    public void writeToFile(String data){
        try {
            fileOutputStream.write(data.getBytes());
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }

    public static String getDate(long milliSeconds, String dateFormat)
    {
        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }
}
