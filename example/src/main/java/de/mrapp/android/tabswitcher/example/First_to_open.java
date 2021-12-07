package de.mrapp.android.tabswitcher.example;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;


/**
 * A simple {@link Fragment} subclass.
 */
public class First_to_open extends Fragment
{
FragmentManager fgm;
int index;


    public First_to_open() {
        // Required empty public constructor
    }


     public void sendFrag(FragmentManager fg, int i)
     {
         fgm=fg;
         index=i;
     }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setRetainInstance(true);

        // Inflate the layout for this fragment
       View view=inflater.inflate(R.layout.fragment_first_to_open, container, false);





        return view;
    }


    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        Log.i("instance state","onSaveInstanceState");
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        Log.i("instance state","onViewStateRestored");
    }
}
