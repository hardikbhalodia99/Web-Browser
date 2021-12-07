package de.mrapp.android.tabswitcher.example;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Downloads extends AppCompatActivity {

    DatabaseDownloads mydb_download;
    ListView lview;
    ListAdapter lviewAdapter;
    ArrayAdapter adapter;
    ArrayList<HashMap<String, String>> downlist;

    LinearLayout emptylist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_downloads);

        mydb_download = new DatabaseDownloads(this);
        lview = (ListView)findViewById(R.id.downloadlistview);
        emptylist = (LinearLayout)findViewById(R.id.emptylist);
        emptylist.setVisibility(View.GONE);

        getData();

        lview.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object o = lview.getAdapter().getItem(position);
                if(o instanceof Map)
                {
                    Map map = (Map) o;
                    try
                    {
                        String filename = String.valueOf(map.get("Title"));
                        String extension = filename.substring(filename.lastIndexOf(".")+1);
                        String type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);

                        Uri path = Uri.parse((String)map.get("Path"));

                        File file = new File(Environment.getExternalStorageDirectory() + "/" + Environment.DIRECTORY_DOWNLOADS + "/" + filename);

                        Uri filepath = Uri.parse(String.valueOf(file));

                        Intent downin = new Intent(Intent.ACTION_VIEW);

                        downin.setDataAndType(filepath,type);
                        downin.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                        downin.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                        startActivity(Intent.createChooser(downin, "Open With : "));
                    }
                    catch (Exception e)
                    {
                        Toast.makeText(Downloads.this,e.getMessage()+" ", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    public void getData()
    {
        Log.i("IAS","FD");
        downlist = mydb_download.showDataDownload();
        Log.i("IAS11","FD");
        if(downlist.isEmpty())
        {
            emptylist.setVisibility(View.VISIBLE);
            Log.i("IAS2","FD");
            return;
        }
        Log.i("IAS3","FD");

        lviewAdapter = new SimpleAdapter(Downloads.this,downlist,R.layout.download_custom_list,
                new String[]{"Id_Download","Title","Time","Path"},
                new int[]{R.id.customiddownload,R.id.customtitledownload,R.id.customtimedownload,R.id.custompathdownload});
        lview.setAdapter(lviewAdapter);
        Log.i("IAS4","FD");
    }
}
