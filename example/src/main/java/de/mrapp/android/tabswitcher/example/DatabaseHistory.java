package de.mrapp.android.tabswitcher.example;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.HashMap;


public class DatabaseHistory extends SQLiteOpenHelper {

    public static final String dbname = "History.db";
    public static final String table_hist = "History";
    public static final String hist_id = "Id";
    public static final String hist_title = "title";
    public static final String hist_Url = "url";


    public DatabaseHistory(Context context) {
        super(context, dbname, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("create table " + table_hist + " (Id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT, url INTEGER) ");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + table_hist);
    }


    //For History
    public boolean insertDatahist(String title, String url) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(hist_title, title);
        cv.put(hist_Url, url);
        Long result = db.insert(table_hist, null, cv);
        db.close();
        if (result == -1) {
            return false;
        } else {
            return true;
        }

    }





    //For History
    public ArrayList<HashMap<String, String>> showDatahist() {
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<HashMap<String, String>> userlist = new ArrayList<>();
        Cursor cursor = db.rawQuery("select * from " + table_hist + " order by Id desc", null);
        while (cursor.moveToNext()) {
            HashMap<String, String> user = new HashMap<>();
            user.put("Id", cursor.getString(cursor.getColumnIndex(hist_id)));
            user.put("Title", cursor.getString(cursor.getColumnIndex(hist_title)));

            String s = cursor.getString(cursor.getColumnIndex(hist_Url));
            int i = s.indexOf(".com");
            user.put("Url",s.substring(0,i+4));

            user.put("UrlReal", cursor.getString(cursor.getColumnIndex(hist_Url)));
            userlist.add(user);
        }
        return userlist;
    }



    //For History
    public Integer deletehist(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int delete = db.delete(table_hist, "Id = ?", new String[]{id});
        //int delete = db.delete(table_name,null,null);

        return delete;
    }


    //For History
    public void alterhist() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE SQLITE_SEQUENCE SET seq = 0 WHERE NAME = ' " + table_hist + " ' ");
    }



}