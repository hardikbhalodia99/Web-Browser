package de.mrapp.android.tabswitcher.example;

import android.Manifest;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.MimeTypeMap;
import android.webkit.URLUtil;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 */
public class WebViewFragment extends Fragment {
    private WebView webview;
    private ProgressBar progressBar;
    private EditText searchText;
    DatabaseHelper mydb;
    DatabaseHistory mydb_hist;
    DatabaseDownloads mydb_download;
    ArrayList<HashMap<String, String>> userlist;


    public WebViewFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

      /*  String mapbookurl, mapbookid, mapoptionsmenu;
        ArrayList<String> mapurllist = new ArrayList<>();
        ArrayList<String> mapidlist = new ArrayList<>();
        ArrayList<String> mapoptionslist = new ArrayList<>();

        mydb = new DatabaseHelper(this);
        mydb_hist = new DatabaseHistory(getApplicationContext());
        mydb_download = new DatabaseDownloads(getApplicationContext());


        searchText = findViewById(R.id.);
        //   Toolbar toolbar = findViewById(R.id.toolbar);
        //   setSupportActionBar(toolbar);

        webview = findViewById(R.id.web_view);

        String url = "https://www.google.com/";

        //    if (getIntent().getExtras() != null) {
        //        url = getIntent().getStringExtra("urlkey");
        //     }


        webview.loadUrl(url);
        WebSettings settings = webview.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.supportZoom();
        //  settings.setDisplayZoomControls(true);
        //  settings.setBuiltInZoomControls(true);
        settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);

        settings.setLoadWithOverviewMode(true);
        settings.setUseWideViewPort(true);
        webview.clearHistory();
        webview.clearCache(true);

        webview.setWebChromeClient(new WebChromeClient());

        registerForContextMenu(webview);


        webview.setWebViewClient(new WebViewClient() {
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                progressBar.setVisibility(View.VISIBLE);

                searchText.setText(webview.getUrl());   //Set URL in Search Box

                invalidateOptionsMenu();

                final String URL = url;
                if (URL.contains("mailto:") || URL.contains("sms:") || URL.contains("tel:")) {
                    webview.stopLoading();
                    Intent i = new Intent();
                    i.setAction(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(URL));
                    startActivity(i);
                }

                super.onPageStarted(view, url, favicon);
            }

            public void onPageFinished(WebView view, String url) {
                progressBar.setVisibility(View.GONE);

                searchText.setText(webview.getUrl());   //Set URL in Search Box
                addHistory();
                invalidateOptionsMenu();


                super.onPageFinished(view, url);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return super.shouldOverrideUrlLoading(view, request);
            }
        });


        searchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                validateURL(searchText.getText().toString());

                return true;
            }
        });


        //To download something from website
        webview.setDownloadListener(new DownloadListener() {

            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    Log.i("HERE4", "FD");
                    if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        Log.i("HERE1", "FD");
                        DownloadDialog(url, userAgent, contentDisposition, mimetype);
                    } else {
                        Log.i("HERE2", "FD");
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                    }
                } else {
                    Log.i("HERE3", "FD");
                    DownloadDialog(url, userAgent, contentDisposition, mimetype);
                }
            }
        });


        searchText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.performClick();

                final int DRAWABLE_RIGHT = 2;

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (searchText.getRight() - searchText.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        startActivity(new Intent(MainActivity.this, Search.class));

                        return true;
                    }
                }
                return false;
            }
        });


        //To download something from website
        webview.setDownloadListener(new DownloadListener() {

            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    Log.i("HERE4", "FD");
                    if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        Log.i("HERE1", "FD");
                        DownloadDialog(url, userAgent, contentDisposition, mimetype);
                    } else {
                        Log.i("HERE2", "FD");
                        ActivityCompat.requestPermissions(WebViewFragment.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                    }
                } else {
                    Log.i("HERE3", "FD");
                    DownloadDialog(url, userAgent, contentDisposition, mimetype);
                }
            }
        });





    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view=inflater.inflate(R.layout.fragment_web_view, container, false);




        return view;
    }
    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
    }


    //Download image on long press
    public void onCreateContextMenu (ContextMenu menu, View v, ContextMenu.ContextMenuInfo
            menuInfo)
    {
        super.onCreateContextMenu(menu, v, menuInfo);

        final WebView.HitTestResult webviewHittestResult = webview.getHitTestResult();

        final String DownloadImageUrl = webviewHittestResult.getExtra();

        if (webviewHittestResult.getType() == WebView.HitTestResult.IMAGE_TYPE || webviewHittestResult.getType() == WebView.HitTestResult.SRC_IMAGE_ANCHOR_TYPE) {
            if (URLUtil.isNetworkUrl(DownloadImageUrl)) {
                menu.setHeaderTitle("Download Image from Below");
                menu.add(0, 1, 0, "Download Image")
                        .setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                int permission_all = 1;
                                String permission[] = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
                                if (!haspermission(MainActivity.this, permission)) {
                                    ActivityCompat.requestPermissions(MainActivity.this, permission, permission_all);
                                } else {
                                    String filename = "";
                                    String type = null;
                                    String Mimetype = MimeTypeMap.getFileExtensionFromUrl(DownloadImageUrl);
                                    filename = URLUtil.guessFileName(DownloadImageUrl, DownloadImageUrl, Mimetype);

                                    if (Mimetype != null) {
                                        type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(Mimetype);
                                    }

                                    if (type == null) {
                                        filename = filename.replace(filename.substring(filename.lastIndexOf(".")), ".jpg");
                                        type = "image/*";
                                    }

                                    DownloadManager.Request request = new DownloadManager.Request(Uri.parse(DownloadImageUrl));
                                    request.allowScanningByMediaScanner();
                                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                                    request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, filename);

                                    DownloadManager manager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                                    manager.enqueue(request);

                                    SimpleDateFormat sdf = new SimpleDateFormat("DD-MM-YYYY , hh:mm a", Locale.getDefault());

                                    File path = new File(Environment.getExternalStorageDirectory() + "/" + Environment.DIRECTORY_DOWNLOADS + "/" + filename);
                                    addDownload(filename, sdf.format(new Date()), String.valueOf(path));


                                }

                                return false;
                            }
                        });

                menu.add(0, 2, 0, "Copy Image Address").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {

                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        String imageURL = webviewHittestResult.getExtra();
                        ClipboardManager manager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                        ClipData clip = ClipData.newPlainText("label", imageURL);
                        manager.setPrimaryClip(clip);
                        Toast.makeText(MainActivity.this, "Copied to Clipboard", Toast.LENGTH_SHORT);
                        return false;
                    }


                });
            }

        }

    }


    //To download something from website
    public void DownloadDialog ( final String url, final String userAgent, String
            contentDisposition, String mimetype)
    {

        final String filename = URLUtil.guessFileName(url, contentDisposition, mimetype);

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

        builder.setTitle("Downloading...")
                .setMessage("Do you want to download " + filename + "  ")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
                        String cookie = CookieManager.getInstance().getCookie(url);
                        request.addRequestHeader("Cookie", cookie);
                        request.addRequestHeader("User Agent", userAgent);
                        request.allowScanningByMediaScanner();

                        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

                        DownloadManager manager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);

                        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, filename);

                        manager.enqueue(request);


                        //Enter in database and show in download menu
                        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-YYYY , hh:mm a", Locale.getDefault());

                        File path = new File(Environment.getExternalStorageDirectory() + "/" + Environment.DIRECTORY_DOWNLOADS + "/" + filename);
                        addDownload(filename, sdf.format(new Date()), String.valueOf(path));


                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert = builder.create();
        alert.show();


    }


    //Taking permission of storage
    public static boolean haspermission (Context context, String...permission)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null & permission != null) {
            for (String per : permission) {
                if (ActivityCompat.checkSelfPermission(context, per) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }

        return true;
    }

    public void addBookmark()
    {
        String title = webview.getTitle();
        String url = webview.getUrl();

        Boolean inserted = mydb.insertData(title, url);

        if (inserted) {
            Toast.makeText(this, "Bookmarked", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Error adding bookmark", Toast.LENGTH_SHORT).show();
        }
    }


    public void addHistory()
    {
        String title = webview.getTitle();
        String url = webview.getUrl();

        Boolean inserted = mydb_hist.insertDatahist(title, url);

        if (inserted) {

        } else {
            Toast.makeText(this, "Error adding history", Toast.LENGTH_SHORT).show();
        }
    }


    public void addDownload (String Title, String Time, String Path)
    {
        String title = Title;
        String time = Time;
        String path = Path;

        Boolean inserted = mydb_download.insertDataDownload(title, time, path);

        if (inserted) {
            Toast.makeText(this, "Downloaded", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Error downloading", Toast.LENGTH_SHORT).show();
        }
    }

    private void validateURL (String url)
    {
        String prefix = "https://www.google.com/search?q=";

        if (!url.startsWith("http://") && !url.startsWith("https://") && !url.endsWith(".com")) {
            url = prefix + url;
        }

        if (url.endsWith(".com") || url.endsWith(".in") || url.endsWith(".as") || url.endsWith(".uk") || url.endsWith(".biz")) {
            if (!url.startsWith("https://") && !url.startsWith("http://")) {
                url = "https://" + url;
            }
        }

        webview.loadUrl(url);
    }
/*
        public boolean onCreateOptionsMenu (Menu menu)
        {
            getMenuInflater().inflate(R.menu.menu_main, menu);

            if (webview.canGoForward()) {
                menu.getItem(1).setEnabled(true);   //Starts from 0, we created Forward at 1
            }

            userlist = mydb.showData();
            for (HashMap hashmap : userlist) {
                HashMap hs = hashmap;
                mapoptionsmenu = (String) hs.get("Url");
                mapoptionslist.add(mapoptionsmenu);
            }

            if (mapoptionslist != null) {
                if (mapoptionslist.contains(webview.getUrl().toString())) {
                    menu.findItem(R.id.bookmark).setIcon(R.drawable.ic_bookmark_black_24dp);
                } else {
                    menu.findItem(R.id.bookmark).setIcon(R.drawable.ic_bookmark_border_black_24dp);
                }
            } else {
                menu.findItem(R.id.bookmark).setIcon(R.drawable.ic_bookmark_border_black_24dp);
            }

            return true;
        }

*/
    }
}






