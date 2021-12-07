package de.mrapp.android.tabswitcher.example;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.strictmode.SqliteObjectLeakedViolation;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.DownloadListener;
import android.webkit.MimeTypeMap;
import android.webkit.URLUtil;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.widget.Toolbar.OnMenuItemClickListener;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.OnApplyWindowInsetsListener;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import de.mrapp.android.tabswitcher.AbstractState;
import de.mrapp.android.tabswitcher.AddTabButtonListener;
import de.mrapp.android.tabswitcher.Animation;
import de.mrapp.android.tabswitcher.Layout;
import de.mrapp.android.tabswitcher.PeekAnimation;
import de.mrapp.android.tabswitcher.PullDownGesture;
import de.mrapp.android.tabswitcher.RevealAnimation;
import de.mrapp.android.tabswitcher.StatefulTabSwitcherDecorator;
import de.mrapp.android.tabswitcher.SwipeGesture;
import de.mrapp.android.tabswitcher.Tab;
import de.mrapp.android.tabswitcher.TabPreviewListener;
import de.mrapp.android.tabswitcher.TabSwitcher;
import de.mrapp.android.tabswitcher.TabSwitcherListener;
import de.mrapp.android.util.ThemeUtil;
import de.mrapp.android.util.multithreading.AbstractDataBinder;


import static android.os.Environment.DIRECTORY_PICTURES;
import static de.mrapp.android.util.DisplayUtil.getDisplayWidth;


public class MainActivity extends AppCompatActivity implements TabSwitcherListener {
    private static int finaltab = 1;
    private WebView webview;
    private ProgressBar progressBar;
    private TextView searchText;
    DatabaseHelper mydb;
    DatabaseHistory mydb_hist;
    DatabaseDownloads mydb_download;
    ArrayList<HashMap<String, String>> userlist;

    String mapbookurl, mapbookid, mapoptionsmenu;
    ArrayList<String> mapurllist = new ArrayList<>();
    ArrayList<String> mapidlist = new ArrayList<>();
    ArrayList<String> mapoptionslist = new ArrayList<>();

    private final static int FCR = 1;
    private String mCM;
    private ValueCallback<Uri> mUM;
    private ValueCallback<Uri[]> mUMA;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (Build.VERSION.SDK_INT >= 21) {
            Uri[] results = null;
            //Check if response is positive
            if (resultCode == Activity.RESULT_OK) {
                if (requestCode == FCR) {
                    if (null == mUMA) {
                        return;
                    }
                    if (intent == null) {
                        //Capture Photo if no image available
                        if (mCM != null) {
                            results = new Uri[]{Uri.parse(mCM)};
                        }
                    } else {
                        String dataString = intent.getDataString();
                        if (dataString != null) {
                            results = new Uri[]{Uri.parse(dataString)};
                        }
                    }
                }
            }
            mUMA.onReceiveValue(results);
            mUMA = null;
        } else {
            if (requestCode == FCR) {
                if (null == mUM) return;
                Uri result = intent == null || resultCode != RESULT_OK ? null : intent.getData();
                mUM.onReceiveValue(result);
                mUM = null;
            }
        }
    }




    /**
     * The state of tabs, which display list items in a list view.
     */
    private class State extends AbstractState
            implements AbstractDataBinder.Listener<ArrayAdapter<String>, Tab, ListView, Void>,
            TabPreviewListener {

        /**
         * The adapter, which contains the list items of the tab.
         */
        private ArrayAdapter<String> adapter;

        /**
         * Creates a new state of a tab, which displays list items in a list view.
         *
         * @param tab The tab, the state should correspond to, as an instance of the class {@link Tab}.
         *            The tab may not be null
         */
        State(@NonNull final Tab tab) {
            super(tab);
        }

        @Override
        public boolean onLoadData(
                @NonNull final AbstractDataBinder<ArrayAdapter<String>, Tab, ListView, Void> dataBinder,
                @NonNull final Tab key, @NonNull final Void... params) {
            return true;
        }

        @Override
        public void onCanceled(
                @NonNull final AbstractDataBinder<ArrayAdapter<String>, Tab, ListView, Void> dataBinder) {

        }

        @Override
        public void onFinished(
                @NonNull final AbstractDataBinder<ArrayAdapter<String>, Tab, ListView, Void> dataBinder,
                @NonNull final Tab key, @Nullable final ArrayAdapter<String> data,
                @NonNull final ListView view, @NonNull final Void... params) {

        }

        @Override
        public final void saveInstanceState(@NonNull final Bundle outState) {

        }

        @Override
        public void restoreInstanceState(@Nullable final Bundle savedInstanceState) {
        }

        @Override
        public boolean onLoadTabPreview(@NonNull final TabSwitcher tabSwitcher,
                                        @NonNull final Tab tab) {
            return !getTab().equals(tab) || adapter != null;
        }

    }

    /**
     * The decorator, which is used to inflate and visualize the tabs of the activity's tab
     * switcher.
     */
    public class Decorator extends StatefulTabSwitcherDecorator<State> {

        @Nullable
        @Override
        protected State onCreateState(@NonNull final Context context,
                                      @NonNull final TabSwitcher tabSwitcher,
                                      @NonNull final View view, @NonNull final Tab tab,
                                      final int index, final int viewType,
                                      @Nullable final Bundle savedInstanceState) {

            return null;
        }

        @Override
        protected void onClearState(@NonNull final State state) {
            tabSwitcher.removeTabPreviewListener(state);
        }

        @Override
        protected void onSaveInstanceState(@NonNull final View view, @NonNull final Tab tab,
                                           final int index, final int viewType,
                                           @Nullable final State state,
                                           @NonNull final Bundle outState) {
            if (state != null) {
                super.onSaveInstanceState(view, tab, index, viewType, state, outState);
                state.saveInstanceState(outState);

            }
        }


        @NonNull
        @Override
        public View onInflateView(@NonNull final LayoutInflater inflater,
                                  @Nullable final ViewGroup parent, final int viewType) {
            View view;
            if (viewType == 0) {
                view = inflater.inflate(R.layout.incognto_tab, parent, false);
            } else {
                view = inflater.inflate(R.layout.home_tab, parent, false);
            }
            Toolbar toolbar = view.findViewById(R.id.toolbar);
            toolbar.inflateMenu(R.menu.tab);
            toolbar.setOnMenuItemClickListener(createToolbarMenuListener());
            Menu menu = toolbar.getMenu();
            TabSwitcher.setupWithMenu(tabSwitcher, menu, createTabSwitcherButtonListener());
            return view;
        }


        @SuppressLint("ClickableViewAccessibility")
        @Override
        public void onShowTab(@NonNull final Context context,
                              @NonNull final TabSwitcher tabSwitcher, @NonNull final View view,
                              @NonNull final Tab tab, final int index, final int viewType,
                              @Nullable final State state,
                              @Nullable final Bundle savedInstanceState) {

            Toolbar toolbar = findViewById(R.id.toolbar);
            toolbar.setVisibility(tabSwitcher.isSwitcherShown() ? View.GONE : View.VISIBLE);

            if (savedInstanceState == null ) {


                mydb = new DatabaseHelper(getApplicationContext());
                mydb_hist = new DatabaseHistory(getApplicationContext());
                mydb_download = new DatabaseDownloads(getApplicationContext());


                searchText = findViewById(R.id.searchText);
                //   Toolbar toolbar = findViewById(R.id.toolbar);
                //   setSupportActionBar(toolbar);

                webview = findViewById(R.id.web_view);
                progressBar =findViewById(R.id.progressBar);

                String url = "https://www.google.com/";
                SharedPreferences sp = getSharedPreferences("previously_opened_?", Context.MODE_PRIVATE);
                Boolean first = sp.getBoolean("open", false);
                if(!first)
                {
                    url=tab.getTitle().toString();
                }

            //    if (getIntent().getExtras() != null) {
            //        url = getIntent().getStringExtra("urlkey");
            //     }

                tab.setTitle(url);
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

                //************************8888888888888888999999999999999999999
                //this is the code for uploading the file

                if (Build.VERSION.SDK_INT >= 21) {
                    webview.getSettings().setMixedContentMode(0);
                    webview.setLayerType(View.LAYER_TYPE_HARDWARE, null);
                } else if (Build.VERSION.SDK_INT >= 19) {
                    webview.setLayerType(View.LAYER_TYPE_HARDWARE, null);
                } else if (Build.VERSION.SDK_INT < 19) {
                    webview.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
                }
                webview.setWebViewClient(new Callback());
                webview.setWebChromeClient(new WebChromeClient() {
                    //For Android 3.0+
                    public void openFileChooser(ValueCallback<Uri> uploadMsg) {
                        mUM = uploadMsg;
                        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                        i.addCategory(Intent.CATEGORY_OPENABLE);
                        i.setType("/");
                        MainActivity.this.startActivityForResult(Intent.createChooser(i, "File Chooser"), FCR);
                    }
                    // For Android 3.0+, above method not supported in some android 3+ versions, in such case we use this
                    public void openFileChooser(ValueCallback uploadMsg, String acceptType) {
                        mUM = uploadMsg;
                        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                        i.addCategory(Intent.CATEGORY_OPENABLE);
                        i.setType("/");
                        MainActivity.this.startActivityForResult(
                                Intent.createChooser(i, "File Browser"),
                                FCR);
                    }
                    //For Android 4.1+
                    public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
                        mUM = uploadMsg;
                        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                        i.addCategory(Intent.CATEGORY_OPENABLE);
                        i.setType("/");
                        MainActivity.this.startActivityForResult(Intent.createChooser(i, "File Chooser"), MainActivity.FCR);
                    }
                    //For Android 5.0+
                    public boolean onShowFileChooser(
                            WebView webView, ValueCallback<Uri[]> filePathCallback,
                            WebChromeClient.FileChooserParams fileChooserParams) {
                        if (mUMA != null) {
                            mUMA.onReceiveValue(null);
                        }
                        mUMA = filePathCallback;
                        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        if (takePictureIntent.resolveActivity(MainActivity.this.getPackageManager()) != null) {
                            File photoFile = null;
                            try {
                                photoFile = createImageFile();
                                takePictureIntent.putExtra("PhotoPath", mCM);
                            } catch (IOException ex) {
                                Log.e("TAG", "Image file creation failed", ex);
                            }
                            if (photoFile != null) {
                                mCM = "file:" + photoFile.getAbsolutePath();
                                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                            } else {
                                takePictureIntent = null;
                            }
                        }
                        Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
                        contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
                        contentSelectionIntent.setType("/");
                        Intent[] intentArray;
                        if (takePictureIntent != null) {
                            intentArray = new Intent[]{takePictureIntent};
                        } else {
                            intentArray = new Intent[0];
                        }
                        Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
                        chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);
                        chooserIntent.putExtra(Intent.EXTRA_TITLE, "Choose an Action");
                        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);
                        startActivityForResult(chooserIntent, FCR);
                        return true;
                    }
                });



                //************************99999999999999999999999888888888888888888





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
                        tab.setTitle(webview.getUrl());
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
                        hereValidateURL(searchText.getText().toString());

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
                       //v.performClick();
                        startActivity(new Intent(MainActivity.this, Search.class));

                        //final int DRAWABLE_RIGHT = 2;


                      //  if (event.getAction() == MotionEvent.ACTION_UP) {
                        //    if (event.getRawX() >= (searchText.getRight() - searchText.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        //    intent was here-->>>>>>>>
                        //        return true;
                        //    }
                      //  }
                        return true;
                    }
                });





                  /* if (findViewById(R.id.hell) != null) {

                        FragmentManager fragmentManager = getSupportFragmentManager();
                        Toast.makeText(getApplicationContext(), "111", Toast.LENGTH_SHORT).show();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        First_to_open fot = new First_to_open();
                        fot.sendFrag(fragmentManager,index);
                        fragmentTransaction.add(R.id.hell, fot, "tab " + index);
                        fragmentTransaction.commit();

                    }*/


                //webview.setLayerType(View.LAYER_TYPE_SOFTWARE,null); // this code stops flickering of web view when tab switched back to it





            }


        }

        @Override
        public int getViewTypeCount() {
            return 1000;
        }

        @Override
        public int getViewType(@NonNull final Tab tab, final int index) {
            Bundle parameters = tab.getParameters();
            return parameters != null ? parameters.getInt(VIEW_TYPE_EXTRA) : 1;
        }


    }


    /**
     * A data binder, which is used to asynchronously load the list items, which are displayed by a
     * tab.
     */
    private static class DataBinder
            extends AbstractDataBinder<ArrayAdapter<String>, Tab, ListView, Void> {

        /**
         * Creates a new data binder, which is used to asynchronously load the list items, which are
         * displayed by a tab.
         *
         * @param context The context, which should be used by the data binder, as an instance of the class
         *                {@link Context}. The context may not be null
         */
        public DataBinder(@NonNull final Context context) {
            super(context.getApplicationContext());
        }

        @Nullable
        @Override
        protected ArrayAdapter<String> doInBackground(@NonNull final Tab key,
                                                      @NonNull final Void... params) {

            String[] array = new String[10];

            for (int i = 0; i < array.length; i++) {
                array[i] = String.format(Locale.getDefault(), "%s, item %d", key.getTitle(), i + 1);
            }

            try {
                // Simulate a long loading time...
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                // There's nothing we can do...
            }

            return new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, array);
        }

        @Override
        protected void onPostExecute(@NonNull final ListView view,
                                     @Nullable final ArrayAdapter<String> data, final long duration,
                                     @NonNull final Void... params) {
            if (data != null) {
                view.setAdapter(data);
            }
        }

    }

    /**
     * The name of the extra, which is used to store the view type of a tab within a bundle.
     */
    private static final String VIEW_TYPE_EXTRA = MainActivity.class.getName() + "::ViewType";

    /**
     * The name of the extra, which is used to store the state of a list adapter within a bundle.
     */
    private static final String ADAPTER_STATE_EXTRA = State.class.getName() + "::%s::AdapterState";

    /**
     * The number of tabs, which are contained by the example app's tab switcher.
     */
    private static int TAB_COUNT = finaltab;
    SharedPreferences shad;
    /**
     * The activity's tab switcher.
     */
    private static TabSwitcher tabSwitcher;

    /**
     * The decorator of the activity's tab switcher.
     */
    private Decorator decorator;

    /**
     * The activity's snackbar.
     */
    private Snackbar snackbar;

    /**
     * The data binder, which is used to load the list items of tabs.
     */
    private DataBinder dataBinder;

    /**
     * Creates a listener, which allows to apply the window insets to the tab switcher's padding.
     *
     * @return The listener, which has been created, as an instance of the type {@link
     * OnApplyWindowInsetsListener}. The listener may not be nullFG
     */
    @NonNull
    private OnApplyWindowInsetsListener createWindowInsetsListener () {
        return new OnApplyWindowInsetsListener() {

            @Override
            public WindowInsetsCompat onApplyWindowInsets(final View v,
                                                          final WindowInsetsCompat insets) {
                int left = insets.getSystemWindowInsetLeft();
                int top = insets.getSystemWindowInsetTop();
                int right = insets.getSystemWindowInsetRight();
                int bottom = insets.getSystemWindowInsetBottom();
                tabSwitcher.setPadding(left, top, right, bottom);
                float touchableAreaBottom = bottom;

                if (tabSwitcher.getLayout() == Layout.TABLET) {
                    touchableAreaBottom += getResources()
                            .getDimensionPixelSize(R.dimen.tablet_tab_container_height);
                }

                RectF touchableArea = new RectF(left, touchableAreaBottom,
                        getDisplayWidth(MainActivity.this) - right, touchableAreaBottom +
                        ThemeUtil.getDimensionPixelSize(MainActivity.this, R.attr.actionBarSize));
                tabSwitcher.addDragGesture(
                        new SwipeGesture.Builder().setTouchableArea(touchableArea).create());
                tabSwitcher.addDragGesture(
                        new PullDownGesture.Builder().setTouchableArea(touchableArea).create());
                return insets;
            }

        };
    }

    /**
     * Creates and returns a listener, which allows to add a tab to the activity's tab switcher,
     * when a button is clicked.
     *
     * @return The listener, which has been created, as an instance of the type {@link
     * OnClickListener}. The listener may not be null
     */
    @NonNull
    private OnClickListener createAddTabListener () {
        return new OnClickListener() {

            @Override
            public void onClick(final View view) {
                int index = tabSwitcher.getCount();
                Animation animation = createRevealAnimation();
                tabSwitcher.addTab(createTab(index), 0, animation);
            }

        };
    }

    /**
     * Creates and returns a listener, which allows to observe, when an item of the tab switcher's
     * toolbar has been clicked.
     *
     * @return The listener, which has been created, as an instance of the type {@link
     * OnMenuItemClickListener}. The listener may not be null
     */
    @NonNull
    private OnMenuItemClickListener createToolbarMenuListener () {
        return new OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(final MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.remove_tab_menu_item:
                        Tab selectedTab = tabSwitcher.getSelectedTab();
                        if (selectedTab != null) {
                            tabSwitcher.removeTab(selectedTab);
                        }
                        return true;

                    case R.id.add_tab_menu_item:
                        int index = tabSwitcher.getCount();
                        Tab tab = createTab(index);


                        if (tabSwitcher.isSwitcherShown()) {
                            tabSwitcher.addTab(tab, 0, createRevealAnimation());
                        } else {
                            tabSwitcher.addTab(tab, 0, createPeekAnimation());
                        }
                        return true;

                    case R.id.clear_tabs_menu_item:
                        tabSwitcher.clear();
                        return true;

                    case R.id.reload:
                        webview.reload();
                        return true;


                    case R.id.history:
                        Intent histintent = new Intent(MainActivity.this,History.class);
                        startActivity(histintent);
                        return  true;

                    case R.id.bookmark:
                        Log.i("IAS-BOOK","FD");
                        userlist = mydb.showData();

                        for(HashMap link : userlist)
                        {
                            HashMap hashmap = link;
                            mapbookid = (String)hashmap.get("Id");
                            mapbookurl = (String)hashmap.get("Url");
                            mapurllist.add(mapbookurl);
                            mapidlist.add(mapbookid);
                        }


                        if (mapurllist.contains(searchText.getText().toString()))
                        {
                            int val = mapurllist.indexOf(webview.getUrl().toString());
                            String val1 = mapidlist.get(val);
                            int parseval1 = Integer.parseInt(val1);

                            Integer delete = mydb.delete(String.valueOf(parseval1));

                            if (delete > 0) {
                               // item.findItem(R.id.bookmark).setIcon(R.drawable.ic_bookmark_black_24dp);

                                item.setIcon(R.drawable.ic_bookmark_border_black_24dp);
                                Toast.makeText(MainActivity.this, "Bookmark Removed", Toast.LENGTH_SHORT).show();
                                mapurllist.clear();
                                mapidlist.clear();
                                mapoptionslist.clear();
                                userlist = mydb.showData();
                                mydb.alter();
                            } else {
                                Toast.makeText(MainActivity.this, "Error removing bookmark", Toast.LENGTH_SHORT).show();
                                mapurllist.clear();
                                mapidlist.clear();
                                mapoptionslist.clear();
                            }
                        }
                        else
                        {
                            mapurllist.clear();
                            mapidlist.clear();
                            mapoptionslist.clear();
                            addBookmark();
                            item.setIcon(R.drawable.ic_bookmark_black_24dp);
                            Toast.makeText(MainActivity.this, "Bookmarked", Toast.LENGTH_SHORT).show();
                        }
                        invalidateOptionsMenu();
                        return true;

                    case R.id.tab_share:
                        TextView tv = findViewById(R.id.searchText);
                        Intent sendIntent = new Intent();
                        sendIntent.setAction(Intent.ACTION_SEND);
                        sendIntent.putExtra(Intent.EXTRA_TEXT, tv.getText().toString());
                        sendIntent.setType("text/plain");
                        Intent.createChooser(sendIntent, "Share via");
                        startActivity(sendIntent);
                        return true;


                    case R.id.add_tab_incognito:
                        int i = tabSwitcher.getCount();
                        Tab itab = createIncognitoTab(i);


                        if (tabSwitcher.isSwitcherShown()) {
                            tabSwitcher.addTab(itab, 0, createRevealAnimation());
                        } else {
                            tabSwitcher.addTab(itab, 0, createPeekAnimation());
                        }
                        return true;

                    case R.id.bookmarks:
                        Intent bookmarks = new Intent(MainActivity.this,Bookmarks.class);
                        startActivity(bookmarks);
                        return true;

                    case R.id.downloads:
                        Log.i("IAS-MAIN1","FD");
                        Intent ii = new Intent(MainActivity.this,Downloads.class);
                        startActivity(ii);
                        return true;

                    case R.id.settings_menu_item:
                        Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                        startActivity(intent);
                    default:
                        return false;
                }
            }

        };
    }

    /**
     * Inflates the tab switcher's menu, depending on whether it is empty, or not.
     */
    private void inflateMenu () {
        tabSwitcher
                .inflateToolbarMenu(tabSwitcher.getCount() > 0 ? R.menu.tab_switcher : R.menu.tab,
                        createToolbarMenuListener());
    }

    /**
     * Creates and returns a listener, which allows to toggle the visibility of the tab switcher,
     * when a button is clicked.
     *
     * @return The listener, which has been created, as an instance of the type {@link
     * OnClickListener}. The listener may not be null
     */
    @NonNull
    private OnClickListener createTabSwitcherButtonListener () {
        return new OnClickListener() {

            @Override
            public void onClick(final View view) {
                tabSwitcher.toggleSwitcherVisibility();
            }

        };
    }

    /**
     * Creates and returns a listener, which allows to add a new tab to the tab switcher, when the
     * corresponding button is clicked.
     *
     * @return The listener, which has been created, as an instance of the type {@link
     * AddTabButtonListener}. The listener may not be null
     */
    @NonNull
    private AddTabButtonListener createAddTabButtonListener () {
        return new AddTabButtonListener() {

            @Override
            public void onAddTab(@NonNull final TabSwitcher tabSwitcher) {
                int index = tabSwitcher.getCount();
                Tab tab = createTab(index);
                tabSwitcher.addTab(tab, 0);
            }

        };
    }

    /**
     * Creates and returns a listener, which allows to undo the removal of tabs from the tab
     * switcher, when the button of the activity's snackbar is clicked.
     *
     * @param snackbar
     *         The activity's snackbar as an instance of the class {@link Snackbar}. The snackbar
     *         may not be null
     * @param index
     *         The index of the first tab, which has been removed, as an {@link Integer} value
     * @param tabs
     *         An array, which contains the tabs, which have been removed, as an array of the type
     *         {@link Tab}. The array may not be null
     * @return The listener, which has been created, as an instance of the type {@link
     * OnClickListener}. The listener may not be null
     */
    @NonNull
    private OnClickListener createUndoSnackbarListener ( @NonNull final Snackbar snackbar,
                                                         final int index,
                                                         @NonNull final Tab...tabs){
        return new OnClickListener() {

            @Override
            public void onClick(final View view) {
                snackbar.setAction(null, null);

                if (tabSwitcher.isSwitcherShown()) {
                    tabSwitcher.addAllTabs(tabs, index);
                } else if (tabs.length == 1) {
                    tabSwitcher.addTab(tabs[0], 0, createPeekAnimation());
                }

            }

        };
    }

    /**
     * Creates and returns a callback, which allows to observe, when a snackbar, which allows to
     * undo the removal of tabs, has been dismissed.
     *
     * @param tabs
     *         An array, which contains the tabs, which have been removed, as an array of the type
     *         {@link Tab}. The tab may not be null
     * @return The callback, which has been created, as an instance of the type class {@link
     * BaseTransientBottomBar.BaseCallback}. The callback may not be null
     */
    @NonNull
    private BaseTransientBottomBar.BaseCallback<Snackbar> createUndoSnackbarCallback (
            final Tab...tabs){
        return new BaseTransientBottomBar.BaseCallback<Snackbar>() {

            @Override
            public void onDismissed(final Snackbar snackbar, final int event) {
                if (event != DISMISS_EVENT_ACTION) {
                    for (Tab tab : tabs) {
                        tabSwitcher.clearSavedState(tab);
                        decorator.clearState(tab);
                    }
                }
            }
        };
    }

    /**
     * Shows a snackbar, which allows to undo the removal of tabs from the activity's tab switcher.
     *
     * @param text
     *         The text of the snackbar as an instance of the type {@link CharSequence}. The text
     *         may not be null
     * @param index
     *         The index of the first tab, which has been removed, as an {@link Integer} value
     * @param tabs
     *         An array, which contains the tabs, which have been removed, as an array of the type
     *         {@link Tab}. The array may not be null
     */
    private void showUndoSnackbar ( @NonNull final CharSequence text, final int index,
                                    @NonNull final Tab...tabs){
        snackbar = Snackbar.make(tabSwitcher, text, Snackbar.LENGTH_LONG).setActionTextColor(
                ContextCompat.getColor(this, R.color.snackbar_action_text_color));
        snackbar.setAction(R.string.undo, createUndoSnackbarListener(snackbar, index, tabs));
        snackbar.addCallback(createUndoSnackbarCallback(tabs));
        snackbar.show();
    }

    /**
     * Creates a reveal animation, which can be used to add a tab to the activity's tab switcher.
     *
     * @return The reveal animation, which has been created, as an instance of the class {@link
     * Animation}. The animation may not be null
     */
    @NonNull
    private Animation createRevealAnimation () {
        float x = 0;
        float y = 0;
        View view = getNavigationMenuItem();

        if (view != null) {
            int[] location = new int[2];
            view.getLocationInWindow(location);
            x = location[0] + (view.getWidth() / 2f);
            y = location[1] + (view.getHeight() / 2f);
        }

        return new RevealAnimation.Builder().setX(x).setY(y).create();
    }

    /**
     * Creates a peek animation, which can be used to add a tab to the activity's tab switcher.
     *
     * @return The peek animation, which has been created, as an instance of the class {@link
     * Animation}. The animation may not be null
     */
    @NonNull
    private Animation createPeekAnimation () {
        return new PeekAnimation.Builder().setX(tabSwitcher.getWidth() / 2f).create();
    }

    /**
     * Returns the menu item, which shows the navigation icon of the tab switcher's toolbar.
     *
     * @return The menu item, which shows the navigation icon of the tab switcher's toolbar, as an
     * instance of the class {@link View} or null, if no navigation icon is shown
     */
    @Nullable
    private View getNavigationMenuItem () {
        Toolbar[] toolbars = tabSwitcher.getToolbars();

        if (toolbars != null) {
            Toolbar toolbar = toolbars.length > 1 ? toolbars[1] : toolbars[0];
            int size = toolbar.getChildCount();

            for (int i = 0; i < size; i++) {
                View child = toolbar.getChildAt(i);

                if (child instanceof ImageButton) {
                    return child;
                }
            }
        }

        return null;
    }

    /**
     * Creates and returns a tab.
     *
     * @param index
     *         The index, the tab should be added at, as an {@link Integer} value
     * @return The tab, which has been created, as an instance of the class {@link Tab}. The tab may
     * not be null
     */

    private Tab createIncognitoTab ( int index )
    {
        CharSequence title = "Incognito Tab";
        Tab tab = new Tab(title);

        //tab.setBackgroundColor(5);
        Bundle parameters = new Bundle();
        parameters.putInt(VIEW_TYPE_EXTRA, 0);

        tab.setParameters(parameters);
        return tab;

    }


    //this method is used when app is runned for the first time in its life
    @NonNull
    private Tab createTab ( int index){
        String title="title";
        SQLiteDatabase myDB=this.openOrCreateDatabase("APPTABS",MODE_PRIVATE,null);
        String tablename="savetab",ttle="tabTitle",i="indx";


        SharedPreferences sp = getSharedPreferences("previously_opened_?", Context.MODE_PRIVATE);
        Boolean first = sp.getBoolean("open", false);
        if(!first) {
             title = " https://www.google.com/";
        }
        else {
            Toast.makeText(getApplicationContext(), " fire in hole " , Toast.LENGTH_LONG).show();

             myDB.execSQL(" select "+ttle+" into "+title+" from "+tablename+" where "+i+" = "+index);
        }
        myDB.close();
        Tab tab = new Tab(title);
        Bundle parameters = new Bundle();
        parameters.putInt(VIEW_TYPE_EXTRA, index+1);

        tab.setParameters(parameters);

        return tab;
    }

/*
    @NonNull // this method is used to create tabs when opened second time or after
    private Tab createTab ( final int index, int h){
        // this shared preference is used as if app is opened before then old tabs has to be loaded else only one new tabb has to be loaded.
        SharedPreferences smm = getSharedPreferences("tabtitle", Context.MODE_PRIVATE);
        String title = smm.getString("tab" + index, ""); // here the tabtitlelist will store all titles of  all opened tab
        Tab tab = new Tab(title);
        Bundle parameters = new Bundle();
        parameters.putInt(VIEW_TYPE_EXTRA, index+1);
        tab.setParameters(parameters);
        return tab;
    }
*/
    @Override
    public final void onSwitcherShown ( @NonNull final TabSwitcher tabSwitcher){

    }

    @Override
    public final void onSwitcherHidden ( @NonNull final TabSwitcher tabSwitcher){
        if (snackbar != null) {
            snackbar.dismiss();
        }
    }

    @Override
    public final void onSelectionChanged ( @NonNull final TabSwitcher tabSwitcher,
                                           final int selectedTabIndex,
                                           @Nullable final Tab selectedTab){

    }

    @Override
    public final void onTabAdded ( @NonNull final TabSwitcher tabSwitcher, final int index,
                                   @NonNull final Tab tab, @NonNull final Animation animation){
        inflateMenu();
        TabSwitcher.setupWithMenu(tabSwitcher, createTabSwitcherButtonListener());
    }

    @Override
    public final void onTabRemoved ( @NonNull final TabSwitcher tabSwitcher, final int index,
                                     @NonNull final Tab tab, @NonNull final Animation animation){
        CharSequence text = getString(R.string.removed_tab_snackbar, tab.getTitle());
        showUndoSnackbar(text, index, tab);
        inflateMenu();
        TabSwitcher.setupWithMenu(tabSwitcher, createTabSwitcherButtonListener());
    }

    @Override
    public final void onAllTabsRemoved ( @NonNull final TabSwitcher tabSwitcher,
                                         @NonNull final Tab[] tabs,
                                         @NonNull final Animation animation){
        CharSequence text = getString(R.string.cleared_tabs_snackbar);
        showUndoSnackbar(text, 0, tabs);
        inflateMenu();
        TabSwitcher.setupWithMenu(tabSwitcher, createTabSwitcherButtonListener());
    }

    @Override
    public final void setTheme ( final int resid){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String themeKey = getString(R.string.theme_preference_key);
        String themeDefaultValue = getString(R.string.theme_preference_default_value);
        int theme = Integer.valueOf(sharedPreferences.getString(themeKey, themeDefaultValue));

        if (theme != 0) {
            super.setTheme(R.style.AppTheme_Translucent_Dark);
        } else {
            super.setTheme(R.style.AppTheme_Translucent_Light);
        }
    }


    @Override
    protected final void onCreate ( final Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        dataBinder = new DataBinder(this);
        decorator = new Decorator();
        tabSwitcher = findViewById(R.id.tab_switcher);
        tabSwitcher.clearSavedStatesWhenRemovingTabs(false);
        ViewCompat.setOnApplyWindowInsetsListener(tabSwitcher, createWindowInsetsListener());
        tabSwitcher.setDecorator(decorator);
        tabSwitcher.addListener(this);
        tabSwitcher.showToolbars(true);


        shad = getSharedPreferences("tabcount", Context.MODE_PRIVATE);
        TAB_COUNT = shad.getInt("opentab", finaltab);

            for (int i = 0; i < TAB_COUNT; i++) {
                tabSwitcher.addTab(createTab(i));

            }


        tabSwitcher.showAddTabButton(createAddTabButtonListener());
        tabSwitcher.setToolbarNavigationIcon(R.drawable.ic_plus_24dp, createAddTabListener());
        TabSwitcher.setupWithMenu(tabSwitcher, createTabSwitcherButtonListener());
        inflateMenu();


    }
    private class Browser_Home extends WebViewClient {
        Browser_Home() {
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
        }
    }

    class ChromeClient extends WebChromeClient {
        private View mCustomView;
        private WebChromeClient.CustomViewCallback mCustomViewCallback;
        protected FrameLayout mFullscreenContainer;
        private int mOriginalOrientation;
        private int mOriginalSystemUiVisibility;

        ChromeClient() {
        }

        public Bitmap getDefaultVideoPoster() {
            if (mCustomView == null) {
                return null;
            }
            return BitmapFactory.decodeResource(getApplicationContext().getResources(), 2130837573);
        }

        public void onHideCustomView() {
            ((FrameLayout) getWindow().getDecorView()).removeView(this.mCustomView);
            this.mCustomView = null;
            getWindow().getDecorView().setSystemUiVisibility(this.mOriginalSystemUiVisibility);
            setRequestedOrientation(this.mOriginalOrientation);
            this.mCustomViewCallback.onCustomViewHidden();
            this.mCustomViewCallback = null;
        }

        public void onShowCustomView(View paramView, WebChromeClient.CustomViewCallback paramCustomViewCallback) {
            if (this.mCustomView != null) {
                onHideCustomView();
                return;
            }
            this.mCustomView = paramView;
            this.mOriginalSystemUiVisibility = getWindow().getDecorView().getSystemUiVisibility();
            this.mOriginalOrientation = getRequestedOrientation();
            this.mCustomViewCallback = paramCustomViewCallback;
            ((FrameLayout) getWindow().getDecorView()).addView(this.mCustomView, new FrameLayout.LayoutParams(-1, -1));
            getWindow().getDecorView().setSystemUiVisibility(3846 | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        }
    }


    @Override
    protected void onPause ()
    { ///when app is close or went to recent apps this mehtod is called

        int count = tabSwitcher.getCount();
        String tablename="savetab";
        SQLiteDatabase myDB=this.openOrCreateDatabase("APPTABS",MODE_PRIVATE,null);
        myDB.execSQL("DROP TABLE IF EXISTS " + tablename);


        myDB.execSQL("create table "+tablename+ " ( tabTitle TEXT , indx INTEGER )");
        String title;
        // this for loop stores the tab's title in shared preference("tabtitle")
        for(int i=0;i<count;i++)
        {
            Tab tab=tabSwitcher.getTab(i);
            title=tab.getTitle().toString();

                myDB.execSQL("insert into "+tablename+" values ('"+ title +"' , "+i+" )");
        }
        myDB.close();
       // Toast.makeText(getApplicationContext(), " name : " + title, Toast.LENGTH_LONG).show();



//this shared preference has default value of false as app is never opened and this becomes true when app is once opened
// this is used as if app is opened before then old tabs has to be loaded else only one new tabb has to be loaded.
        SharedPreferences sop = getSharedPreferences("previously_opened_?", Context.MODE_PRIVATE);
        SharedPreferences.Editor ed = sop.edit();
        ed.clear();
        ed.putBoolean("open", true);
        ed.apply();


// this shared preference stores the value of previously opened tabs before closing app and this is later utilised when app is again reopened
        SharedPreferences shad = getSharedPreferences("tabcount", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = shad.edit();
        finaltab = tabSwitcher.getCount();
        editor.clear();
        editor.putInt("opentab", finaltab);
        editor.apply();







        super.onPause();




    }








    //*************************************************************************************************************
    //***********************************************************************************************************8
    //************************************************************************************************************

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

        private void hereValidateURL (String url)
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

    private File createImageFile() throws IOException {
        @SuppressLint("SimpleDateFormat") String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "img_" + timeStamp + "_";

        File storageDir = Environment.getExternalStoragePublicDirectory(DIRECTORY_PICTURES);
        return File.createTempFile(imageFileName, ".jpg", storageDir);
    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }
    public class Callback extends WebViewClient {
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            Toast.makeText(getApplicationContext(), "Failed loading app!", Toast.LENGTH_SHORT).show();
        }
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
