package de.mrapp.android.tabswitcher.example;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class Search extends AppCompatActivity {

    private SearchView searchView;
    private ListView listView;

    ArrayList<String> list = new ArrayList<>();
    ArrayAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        searchView = (SearchView)findViewById(R.id.searchView);
        listView = (ListView)findViewById(R.id.listView);

        list.add("google");
        list.add("amazon");
        list.add("facebook");
        list.add("yahoo");
        list.add("instagram");
        list.add("apple");
        list.add("godrej");
        list.add("you tube");

        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,list);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
            {
                Object item = listView.getAdapter().getItem(i);
                String itemstr = String.valueOf(item);
                validateURL(itemstr);
            }
        });

        searchView.setOnQueryTextListener(new android.widget.SearchView.OnQueryTextListener()
        {
            @Override
            public boolean onQueryTextSubmit(String query)
            {
                if(list.contains(query))
                {
                    adapter.getFilter().filter(query);
                    validateURL(query);
                }
                else
                {
                    Toast.makeText(Search.this, "No text found", Toast.LENGTH_SHORT).show();
                }

                validateURL(query);
                return true;
            }

            public boolean onQueryTextChange(String newText)
            {
                if(list.isEmpty())
                {

                }
                else
                {
                    adapter.getFilter().filter(newText);
                }

                return false;
            }


        });
    }



    private void validateURL(String url)
    {
        String prefix = "https://www.google.com/search?q=";

        if(!url.startsWith("http://") && !url.startsWith("https://") && !url.endsWith(".com"))
        {
            url = prefix + url;
        }

        if(url.endsWith(".com") || url.endsWith(".in") || url.endsWith(".as") || url.endsWith(".uk") || url.endsWith(".biz"))
        {
            if(!url.startsWith("https://") && !url.startsWith("http://"))
            {
                url = "https://" + url;
            }
        }

        Intent i2 = new Intent(Search.this,MainActivity.class);
        i2.putExtra("second",url);
        startActivity(i2);
    }
}
