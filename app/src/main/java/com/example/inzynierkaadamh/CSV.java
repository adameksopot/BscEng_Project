package com.example.inzynierkaadamh;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener2;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class CSV extends AppCompatActivity implements SensorEventListener2 {
    SensorManager manager;
       Button buttonStart;
    Button buttonStop;
    boolean isRunning;
    FileWriter writer;
    private TextView StepsView;
 private  int Steps;
    private Sensor CountSteps;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);

        isRunning = false;
        manager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
       CountSteps = manager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
       StepsView = findViewById(R.id.textView);
        buttonStart = (Button)findViewById(R.id.buttonStart);
        buttonStop = (Button)findViewById(R.id.buttonStop);
        buttonStart.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                buttonStart.setEnabled(false);
                buttonStop.setEnabled(true);
                try {
                    writer = new FileWriter(new File(getStorageDir(), "sensors_" + System.currentTimeMillis() + ".csv"));
                } catch (IOException e) {
                    e.printStackTrace();
                }

                manager.registerListener(CSV.this, manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), 0);

                manager.registerListener(CSV.this, manager.getDefaultSensor(Sensor.TYPE_GYROSCOPE), 0);
                isRunning = true;
                return true;
            }
        });

        buttonStop.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                buttonStart.setEnabled(true);
                buttonStop.setEnabled(false);
                isRunning = false;
                manager.flush(CSV.this);
                manager.unregisterListener(CSV.this);
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return true;
            }
        });
    }

    private String getStorageDir() {
        return this.getExternalFilesDir(null).getAbsolutePath();
    }

    @Override
    public void onFlushCompleted(Sensor sensor) {

    }

    @Override
    public void onSensorChanged(SensorEvent evt) {
        if (evt.sensor == CountSteps) {
            Steps = (int) evt.values[0];
            StepsView.setText(String.valueOf(Steps));
        }
        if(isRunning) {
            try {
                switch(evt.sensor.getType()) {
                 case Sensor.TYPE_ACCELEROMETER:
                      writer.write(String.format("%d; ACC; %f; %f; %f\n", evt.timestamp, evt.values[0], evt.values[1], evt.values[2], 0.f, 0.f, 0.f));
                        break;

                   case Sensor.TYPE_GYROSCOPE:
                       writer.write(String.format("%d; GYRO; %f; %f; %f\n", evt.timestamp, evt.values[0], evt.values[1], evt.values[2], 0.f, 0.f, 0.f));
                       break;

                    case Sensor.TYPE_LINEAR_ACCELERATION:
                        writer.write(String.format("%d; LIN; %f; %f; %f\n", evt.timestamp, evt.values[0], evt.values[1], evt.values[2], 0.f, 0.f, 0.f));
                        break;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
    @Override
    protected void onResume() {
        super.onResume();
        if (manager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER) != null) {

           manager.registerListener(this, CountSteps, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

    }


    @Override
    protected void onDestroy() {

        super.onDestroy();
        manager.unregisterListener(this, CountSteps);

    }

}