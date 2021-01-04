package com.example.inzynierkaadamh;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Button;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;


public class Steps extends AppCompatActivity {
    private final int PHYISCAL_ACTIVITY = 0;
    private Button btnStopService;
    private Button btnStartService;
    private BroadcastReceiver broadcastReceiver;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main4);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_DENIED) {
            requestPermissions(new String[]{Manifest.permission.ACTIVITY_RECOGNITION}, PHYISCAL_ACTIVITY);
        }
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        btnStartService = findViewById(R.id.btnStartService);
        btnStopService = findViewById(R.id.btnStopService);
        initButtonsOnClick();
    }

    private void initButtonsOnClick() {
        OnClickListener listener = new OnClickListener() {
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.btnStartService:
                        startMyService();
                        break;
                    case R.id.btnStopService:
                        stopMyService();
                        break;
                    default:
                        break;
                }
            }
        };
        btnStartService.setOnClickListener(listener);
        btnStopService.setOnClickListener(listener);
    }

    private void startMyService() {
        if (broadcastReceiver == null) {
            broadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                  //  image.setText(String.valueOf(intent.getExtras().get("myvalues")));
                }}; }
        registerReceiver(broadcastReceiver, new IntentFilter("my_reciever"));
        Intent i = new Intent(getApplicationContext(), MyService.class);
        startService(i);
    }

    private void stopMyService() {
        if(broadcastReceiver != null){
            unregisterReceiver(broadcastReceiver);
            broadcastReceiver=null;

        }
        Intent i = new Intent(getApplicationContext(),MyService.class);
        stopService(i);

    }
}