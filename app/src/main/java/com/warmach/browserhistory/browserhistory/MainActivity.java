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
        String[] data = new String[] { Browser.BookmarkColumns.TITLE,
                Browser.BookmarkColumns.URL, Browser.BookmarkColumns.DATE };
        Uri uriCustom = Uri.parse("content://com.android.chrome.browser/bookmarks");
        String browserHistory = Browser.BookmarkColumns.BOOKMARK + " = 0"; // 0 = history, 1= bookmark
        Cursor mycur = this.managedQuery(uriCustom, data,browserHistory, null, null);
        this.startManagingCursor(mycur);
        mycur.moveToFirst();

        ArrayList<String> array = new ArrayList<>();
        String title = "";
        String url = "";
        String date = "";

        if (mycur.moveToFirst() && mycur.getCount() > 0) {
            while (!mycur.isAfterLast()) {
                title = mycur.getString(mycur
                        .getColumnIndex(Browser.BookmarkColumns.TITLE));
                url = mycur.getString(mycur
                        .getColumnIndex(Browser.BookmarkColumns.URL));
                date = mycur.getString(mycur
                        .getColumnIndex(Browser.BookmarkColumns.DATE));
                array.add(title + "\t" + url + "\t" + getDate(Long.parseLong(date), "dd/MM/yyyy_hh:mm:ss" ));

                mycur.moveToNext();
            }
        }
        if (array.size() > 0) {
            for (String string : array) {
                writeToFile(string + "\n");
                Log.d("result ", string);
            }

        }
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
