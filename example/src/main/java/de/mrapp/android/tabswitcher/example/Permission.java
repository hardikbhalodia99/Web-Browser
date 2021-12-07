package de.mrapp.android.tabswitcher.example;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class Permission extends AppCompatActivity {

    Button permission;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permission);

        permission = (Button)findViewById(R.id.permission);

        permission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int permission_All = 1;
                String[] String_per = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};

                if(!haspermission(Permission.this,String_per))
                {
                    ActivityCompat.requestPermissions(Permission.this,String_per,permission_All);
                }
            }
        });
    }

    public static boolean haspermission(Context context, String... permission)
    {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context!=null & permission!=null)
        {
            for(String per : permission)
            {
                if(ActivityCompat.checkSelfPermission(context,per) != PackageManager.PERMISSION_GRANTED)
                {
                    return false;
                }
            }
        }

        return true;
    }
}
