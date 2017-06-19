package com.alperenkantarci.alertbutton;

import android.*;
import android.Manifest;
import android.app.ActionBar;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import static android.R.id.list;

public class MainActivity extends AppCompatActivity {
    Button add_button;
    Button list_button;
    ImageView alarm_button;
    FusedLocationProviderClient mFusedLocationClient;
    double longitude,latitude,time;
    float speed,accuracy;


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.i("ASDFSADsa", "SADFDSAFAFDS");

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.add_menu, menu);
        return true;
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);


        add_button = (Button) findViewById(R.id.main_add_button);
        list_button = (Button) findViewById(R.id.list_button);
        alarm_button = (ImageView) findViewById(R.id.alarm_button);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);


        }

        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {

                            longitude = location.getLongitude();
                            latitude = location.getLatitude();
                            time = location.getTime();
                            speed = location.getSpeed();
                            accuracy= location.getAccuracy();


                            Log.i("Longitude", String.valueOf(longitude));
                            Log.i("Latitude", String.valueOf(latitude));
                            Log.i("Time", String.valueOf(time));
                            Log.i("Speed", String.valueOf(speed));

                            findAdress(latitude,longitude);
                        }
                    }
                });

        add_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent new_activity = new Intent(getApplicationContext(), AddActivity.class);
                startActivity(new_activity);
            }
        });

        list_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent new_activity = new Intent(getApplicationContext(), ListActivity.class);
                startActivity(new_activity);
            }
        });


        // TODO (1): ADD LOCATION PROVIDER PROPERLY

        alarm_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }


        });
    }

    public void findAdress(double latitude , double longitude) {

        Geocoder geocoder = new Geocoder(MainActivity.this, Locale.US);
        List<Address> listOfAddress;
        try {
            listOfAddress = geocoder.getFromLocation(latitude, longitude, 1);
            if (listOfAddress != null && !listOfAddress.isEmpty()) {
                Address address = listOfAddress.get(0);

                String country = address.getCountryCode();
                String adminArea = address.getAdminArea();
                String locality = address.getLocality();
                String feautureName = address.getFeatureName();
                String addressLine = address.getAddressLine(1);

                try {

                    Log.i("Country",country);
                    Log.i("adminArea",adminArea);
                    Log.i("Locality",locality);
                    Log.i("FeautureName",feautureName);
                    Log.i("AddressLine",addressLine);
                }catch (NullPointerException e){
                    Log.i("NULL","NULL");
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



}