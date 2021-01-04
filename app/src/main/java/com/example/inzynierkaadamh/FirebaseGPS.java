package com.example.inzynierkaadamh;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;

public class FirebaseGPS extends FragmentActivity implements OnMapReadyCallback {

        private GoogleMap mMap;
        private DatabaseReference databaseReference;
        private LocationListener locationListener;
        private LocationManager locationManager;
        private final long MIN_TIME = 1000;
        private final long MIN_DIST = 5;
        private EditText editTextLatitude;
        private EditText editTextLongitude;


        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_maps);
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, PackageManager.PERMISSION_GRANTED);

            editTextLatitude = findViewById(R.id.editText);
            editTextLongitude = findViewById(R.id.editText2);

            databaseReference = FirebaseDatabase.getInstance().getReference("Location");
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    try {
                        String databaseLatitudeString = dataSnapshot.child("latitude").getValue().toString().substring(1, dataSnapshot.child("latitude").getValue().toString().length()-1);
                        String databaseLongitudedeString = dataSnapshot.child("longitude").getValue().toString().substring(1, dataSnapshot.child("longitude").getValue().toString().length()-1);

                        String[] stringLat = databaseLatitudeString.split(", ");
                        Arrays.sort(stringLat);
                        String latitude = stringLat[stringLat.length-1].split("=")[1];

                        String[] stringLong = databaseLongitudedeString.split(", ");
                        Arrays.sort(stringLong);
                        String longitude = stringLong[stringLong.length-1].split("=")[1];


                        LatLng latLng = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));

                        mMap.addMarker(new MarkerOptions().position(latLng).title(latitude + " , " + longitude));
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));


                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }


                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        @Override
        public void onMapReady(GoogleMap googleMap) {
            mMap = googleMap;

            locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    try {
                        editTextLatitude.setText(Double.toString(location.getLatitude()));
                        editTextLongitude.setText(Double.toString(location.getLongitude()));
                    } catch (Exception e) {
                        e.printStackTrace();
                    } }

                @Override
                public void onStatusChanged(String s, int i, Bundle bundle) { }

                @Override
                public void onProviderEnabled(String s) {

             }

                @Override
                public void onProviderDisabled(String s) {

                }
            };

            locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
               
                return;
            }

            try {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME, MIN_DIST, locationListener);
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME, MIN_DIST, locationListener);
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }

        public void updateButtonOnclick(View view){

            databaseReference.child("latitude").push().setValue(editTextLatitude.getText().toString());
            databaseReference.child("longitude").push().setValue(editTextLongitude.getText().toString());

        }


}