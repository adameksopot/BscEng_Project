package com.example.inzynierkaadamh;

import java.util.Timer;
import java.util.TimerTask;
import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.widget.TextView;
import android.widget.Toast;

public class MyService extends Service implements SensorEventListener {
    private Toast toast;
    private SensorManager sensorManager;
    private Sensor mineSensor;
    int Steps;
    private TextView textView;

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor == mineSensor) {
            Steps= (int) sensorEvent.values[0];
            Intent i = new Intent("my_reciever");
            i.putExtra("myvalues",Steps);
            sendBroadcast(i);

        }
    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

        private void showToast(String text) {
        toast.setText(text);
        toast.show();
    }


    @Override
    public void onCreate() {
        super.onCreate();

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        if (sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER) != null) {
            mineSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);

        } else {
            textView.setText("No sensor found!");
        }
        toast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {



        showToast("Stepscounter enabled!");
        return super.onStartCommand(intent, flags, startId);
    }


    @Override
    public void onDestroy() {


        showToast("Stepscounter disabled!");
        super.onDestroy();
    }
    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }
}
