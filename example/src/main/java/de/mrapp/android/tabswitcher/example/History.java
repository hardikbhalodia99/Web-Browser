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

public class History extends AppCompatActivity {

    DatabaseHistory mydb_hist;
    ListView histlist;
    ListAdapter histlistAdapter;
    ArrayList<HashMap<String, String>> userlist;
    LinearLayout histemptylayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);


        mydb_hist = new DatabaseHistory(this);
        histlist = findViewById(R.id.histlistview);
        histemptylayout = findViewById(R.id.histemptylayout);
        histemptylayout.setVisibility(android.view.View.INVISIBLE);

        getData();

        histlist.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object o = histlist.getAdapter().getItem(position);
                if(o instanceof Map)
                {
                    Map map = (Map)o;
                    Intent intent = new Intent(History.this,MainActivity.class);
                    intent.putExtra("urlkey", String.valueOf(map.get("UrlReal")));
                    startActivity(intent);
                }
            }
        });
    }

    public void getData() {
        userlist = mydb_hist.showDatahist();

        if (userlist.isEmpty()) {
            histemptylayout.setVisibility(android.view.View.VISIBLE);
            return;
        }


        histlistAdapter = new SimpleAdapter(History.this, userlist, R.layout.hist_custom_list, new String[]{"Title", "Url", "UrlReal"}, new int[]{R.id.customhisttitle, R.id.customhisturl, R.id.customhistrealurl});
        histlist.setAdapter(histlistAdapter);
    }


    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }


}
