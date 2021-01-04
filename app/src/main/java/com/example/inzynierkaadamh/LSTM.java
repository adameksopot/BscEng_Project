package com.example.inzynierkaadamh;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.TextView;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class LSTM extends AppCompatActivity  implements SensorEventListener {
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private Sensor mGyroscope;
    private Sensor mLinearAcceleration;
    private static final int TIME_STAMP = 100;

    private static List<Float> akc_x,akc_y,akc_z;
    private static List<Float> gyro_x,gyro_y,gyro_z;
    private static List<Float> liniowy_x,liniowy_y,liniowy_z;



    private float[] results;
    private Classifier classifier;
    private TextView bikingTextView;
    private TextView downstairsTextView;
    private TextView  joggingTextView;
    private TextView  sittingTextView;
    private TextView  standingTextView;
    private TextView upstairsTextView;
    private TextView  walkingTextView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        initLayoutItems();

        akc_x=new ArrayList<>();
        akc_y=new ArrayList<>();
        akc_z=new ArrayList<>();
        gyro_x=new ArrayList<>();
        gyro_y=new ArrayList<>();
        gyro_z=new ArrayList<>();
        liniowy_x=new ArrayList<>();
        liniowy_y=new ArrayList<>();
        liniowy_z=new ArrayList<>();

        mSensorManager=(SensorManager)getSystemService(SENSOR_SERVICE);
        mAccelerometer=mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mGyroscope = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        mLinearAcceleration = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        classifier=new Classifier(getApplicationContext());
        mSensorManager.registerListener(this,mAccelerometer, SensorManager.SENSOR_DELAY_FASTEST);
        mSensorManager.registerListener(this,mGyroscope, SensorManager.SENSOR_DELAY_FASTEST);
        mSensorManager.registerListener(this,mLinearAcceleration, SensorManager.SENSOR_DELAY_FASTEST);
    }

    private void initLayoutItems() {

        bikingTextView = findViewById(R.id.biking_TextView);
        downstairsTextView = findViewById(R.id.downstairs_TextView);
        joggingTextView = findViewById(R.id.jogging_TextView);
        sittingTextView  = findViewById(R.id.sitting_TextView);
        standingTextView = findViewById(R.id.standing_TextView);
        upstairsTextView = findViewById(R.id.upstairs_TextView);
        walkingTextView = findViewById(R.id.walking_TextView);

    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        Sensor sensor = event.sensor;

        switch (sensor.getType()){
            case(Sensor.TYPE_ACCELEROMETER):{
                akc_x.add(event.values[0]);
                akc_y.add(event.values[1]);
                akc_z.add(event.values[2]);
                break;
            }


            case(Sensor.TYPE_GYROSCOPE): {
                gyro_x.add(event.values[0]);
                gyro_y.add(event.values[1]);
                gyro_z.add(event.values[2]);
                break;
            }
            default:{
                liniowy_x.add(event.values[0]);
                liniowy_y.add(event.values[1]);
                liniowy_z.add(event.values[2]);


            }}


        predictActivity();
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private void predictActivity() {
        List<Float> data=new ArrayList<>();
        if (akc_x.size() >= TIME_STAMP && akc_y.size() >= TIME_STAMP && akc_z.size() >= TIME_STAMP
                && gyro_x.size() >= TIME_STAMP && gyro_y.size() >= TIME_STAMP && gyro_z.size() >= TIME_STAMP
                && liniowy_x.size() >= TIME_STAMP && liniowy_y.size() >= TIME_STAMP && liniowy_z.size() >= TIME_STAMP) {
            data.addAll(akc_x.subList(0,TIME_STAMP));
            data.addAll(akc_y.subList(0,TIME_STAMP));
            data.addAll(akc_z.subList(0,TIME_STAMP));

            data.addAll(gyro_x.subList(0,TIME_STAMP));
            data.addAll(gyro_y.subList(0,TIME_STAMP));
            data.addAll(gyro_z.subList(0,TIME_STAMP));

            data.addAll(liniowy_x.subList(0,TIME_STAMP));
            data.addAll(liniowy_y.subList(0,TIME_STAMP));
            data.addAll(liniowy_z.subList(0,TIME_STAMP));

            results = classifier.predictProbabilities(toFloatArray(data));

            bikingTextView.setText("Rower: \t" + round(results[0],2));
            downstairsTextView.setText("Schody dol: \t" + round(results[1],2));
            joggingTextView.setText("Bieg: \t" + round(results[2],2));
            sittingTextView.setText("Siedzenie: \t" + round(results[3],2));
            standingTextView.setText("Stanie : \t" + round(results[4],2));
            upstairsTextView.setText("Schody gora: \t" + round(results[5],2));;
            walkingTextView.setText("Marsz: \t" + round(results[6],2));


            data.clear();
            akc_x.clear();
            akc_y.clear();
            akc_z.clear();
            gyro_x.clear();
            gyro_y.clear();
            gyro_z.clear();
            liniowy_x.clear();
            liniowy_y.clear();
            liniowy_z.clear();
        }
    }

    private float round(float value, int decimal_places) {
        BigDecimal bigDecimal=new BigDecimal(Float.toString(value));
        bigDecimal = bigDecimal.setScale(decimal_places, BigDecimal.ROUND_HALF_UP);
        return bigDecimal.floatValue();
    }

    private float[] toFloatArray(List<Float> data) {
        int i=0;
        float[] array=new float[data.size()];
        for (Float f:data) {
            array[i++] = (f != null ? f: Float.NaN);
        }
        return array;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this,mAccelerometer, SensorManager.SENSOR_DELAY_FASTEST);
        mSensorManager.registerListener(this,mGyroscope, SensorManager.SENSOR_DELAY_FASTEST);
        mSensorManager.registerListener(this,mLinearAcceleration, SensorManager.SENSOR_DELAY_FASTEST);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSensorManager.unregisterListener(this);
    }
}