package de.mrapp.android.tabswitcher.example;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.HashMap;


public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String dbname = "Bookmarks.db";
    public static final String table_name = "bookmarks";
    public static final String col_id = "Id";
    public static final String col_title = "title";
    public static final String col_Url = "url";




    public DatabaseHelper(Context context) {
        super(context, dbname, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("create table " + table_name + " (Id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT, url INTEGER) ");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + table_name);

    }

    //For Bookmarks
    public boolean insertData(String title, String url)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(col_title,title);
        cv.put(col_Url,url);
        Long result = db.insert(table_name,null,cv);
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






    public boolean updateData(String id, String title, String url)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(col_id,id);
        cv.put(col_title,title);
        cv.put(col_Url,url);
        int i = db.update(table_name,cv,"id = ?", new String[]{id});
        if(i==1)
        {
            return true;
        }
        else
        {
            return false;
        }
    }


    //For Bookmarks
    public ArrayList<HashMap<String, String>> showData()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<HashMap<String, String>> userlist = new ArrayList<>();
        Cursor cursor = db.rawQuery("select * from " + table_name,null);
        while(cursor.moveToNext())
        {
            HashMap<String, String> user = new HashMap<>();
            user.put("Id",cursor.getString(cursor.getColumnIndex(col_id)));
            user.put("Title",cursor.getString(cursor.getColumnIndex(col_title)));
            user.put("Url",cursor.getString(cursor.getColumnIndex(col_Url)));
            userlist.add(user);
        }
        return userlist;
    }



    //For Bookmarks
    public Integer delete(String id)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        int delete = db.delete(table_name,"Id = ?",new String[]{id});
        //int delete = db.delete(table_name,null,null);

        return delete;
    }


    //For Bookmarks
    public void alter()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE SQLITE_SEQUENCE SET seq = 0 WHERE NAME = ' "+table_name+" ' ");
    }

}