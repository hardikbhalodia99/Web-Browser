package de.mrapp.android.tabswitcher.example;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Bookmarks extends AppCompatActivity {

    DatabaseHelper mydb;
    ListView booklist;
    ListAdapter booklistAdapter;
    ArrayList<HashMap<String, String>> userlist;
    LinearLayout bookemptylayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmarks);

        mydb = new DatabaseHelper(this);
        booklist = (ListView)findViewById(R.id.booklistview);
        bookemptylayout = (LinearLayout)findViewById(R.id.bookemptylayout);
        bookemptylayout.setVisibility(android.view.View.INVISIBLE);

        getData();

        booklist.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object o = booklist.getAdapter().getItem(position);
                if(o instanceof Map)
                {
                    Map map = (Map)o;
                    Intent bookIntent = new Intent(Bookmarks.this,MainActivity.class);
                    bookIntent.putExtra("urlkey", String.valueOf(map.get("Url")));
                    startActivity(bookIntent);
                }
            }
        });
    }

    public void getData() {
        userlist = mydb.showData();

        if (userlist.isEmpty()) {
            bookemptylayout.setVisibility(android.view.View.VISIBLE);
            return;
        }

        booklistAdapter = new SimpleAdapter(Bookmarks.this, userlist, R.layout.bookmark_list, new String[]{"Id", "Title", "Url"}, new int[]{R.id.bookmarklistid, R.id.bookmarklisttitle, R.id.bookmarklisturl});
        booklist.setAdapter(booklistAdapter);
    }


        public void onBackPressed() {
            finish();
            super.onBackPressed();
        }


}

