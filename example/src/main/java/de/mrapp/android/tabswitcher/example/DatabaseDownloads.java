package de.mrapp.android.tabswitcher.example;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.HashMap;


public class DatabaseDownloads extends SQLiteOpenHelper {

    public static final String dbname = "Downloads.db";
    public static final String table_download = "downloads";
    public static final String down_id = "Id";
    public static final String down_title = "title";
    public static final String down_time = "time";
    public static final String down_path = "path";

    public DatabaseDownloads(Context context) {
        super(context, dbname, null, 1);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + table_download + " (Id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT, time TEXT, path TEXT) ");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + table_download);
    }


    //For Downloads
    public boolean insertDataDownload(String title, String time, String path)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(down_title,title);
        cv.put(down_time,time);
        cv.put(down_path,path);
        Long result = db.insert(table_download,null,cv);
        db.close();

        if(result==-1)
        {
            return false;
        }
        else
        {
            return true;
        }



    }


    //For Downloads
    public ArrayList<HashMap<String, String>> showDataDownload()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<HashMap<String, String>> downlist = new ArrayList<>();
        Cursor cursor = db.rawQuery("select * from " + table_download,null);
        while(cursor.moveToNext())
        {
            HashMap<String, String> user = new HashMap<>();
            user.put("Id",cursor.getString(cursor.getColumnIndex(down_id)));
            user.put("Title",cursor.getString(cursor.getColumnIndex(down_title)));
            user.put("Time",cursor.getString(cursor.getColumnIndex(down_time)));
            user.put("Path",cursor.getString(cursor.getColumnIndex(down_path)));
            downlist.add(user);
        }
        return downlist;
    }
}