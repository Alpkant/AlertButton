package com.alperenkantarci.alertbutton;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
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


public class MainActivity extends AppCompatActivity {
    Button add_button;
    Button list_button;
    ImageView alarm_button;
    LocationDetails lastLocation;
    FusedLocationProviderClient mFusedLocationClient;
    double longitude, latitude, time;
    float speed, accuracy;


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.add_menu, menu);
        return true;
    }

    public void getTheLocationInfo(Activity activity) {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(activity);

        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(activity, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            longitude = location.getLongitude();
                            latitude = location.getLatitude();
                            time = location.getTime();
                            speed = location.getSpeed();
                            accuracy = location.getAccuracy();

                            lastLocation = new LocationDetails(longitude, latitude);
                            lastLocation.setTime(time);
                            lastLocation.setSpeed(speed);
                            lastLocation.setAccuracy(accuracy);


                            Log.i("Longitude", String.valueOf(longitude));
                            Log.i("Latitude", String.valueOf(latitude));
                            Log.i("Time", String.valueOf(time));
                            Log.i("Speed", String.valueOf(speed));

                            findAdress(latitude, longitude);

                        }
                    }
                });
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        add_button = (Button) findViewById(R.id.main_add_button);
        list_button = (Button) findViewById(R.id.list_button);
        alarm_button = (ImageView) findViewById(R.id.alarm_button);


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


        alarm_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                    if (checkLocationPermission()) {
                        if (ContextCompat.checkSelfPermission(MainActivity.this,
                                Manifest.permission.ACCESS_FINE_LOCATION)
                                == PackageManager.PERMISSION_GRANTED) {

                            //Request location updates:
                            getTheLocationInfo(MainActivity.this);

                            if(checkInternetPermission()){
                                if (ContextCompat.checkSelfPermission(MainActivity.this,
                                        Manifest.permission.INTERNET)
                                        == PackageManager.PERMISSION_GRANTED){

                                    try {
                                        GMailSender sender = new GMailSender("alperenkantarci@gmail.com", "Alpbeysubuka4");
                                        sender.sendMail("This is Subject",
                                                "This is Body",
                                                "alperenkantarci@gmail.com",
                                                "alperenkantarci@gmail.com");
                                        Log.e("SendMail", "SUCCESS");
                                    } catch (Exception e) {
                                        Log.e("SendMail", e.getMessage(), e);
                                    }


                                }
                            }








                        /*
                            try{

                                if (lastLocation.getLatitude() != 0) {
                                    if (checkSMSPermission()) {
                                        if (ContextCompat.checkSelfPermission(MainActivity.this,
                                                Manifest.permission.SEND_SMS)
                                                == PackageManager.PERMISSION_GRANTED) {
                                            try {
                                                Log.i("SMS SUCCESS", "SUCCESS");
                                                SmsManager smsManager = SmsManager.getDefault();
                                                smsManager.sendTextMessage("05063014341", null, String.valueOf(lastLocation.getLatitude()), null, null);
                                            } catch (NullPointerException e) {
                                                Log.i("NULL", "NULL");
                                            }

                                        }
                                    }
                                } else {
                                    Log.i("SMS FAIL", "FAIL");
                                }
                            }catch (NullPointerException e){
                                Log.i("NULL","NULL");
                            } */
                        }
                    }
                }

            }

        });
    }

    public void findAdress(double latitude, double longitude) {

        Geocoder geocoder = new Geocoder(MainActivity.this, Locale.US);
        List<Address> listOfAddress;
        try {
            listOfAddress = geocoder.getFromLocation(latitude, longitude, 1);
            if (listOfAddress != null && !listOfAddress.isEmpty()) {
                Address address = listOfAddress.get(0);

                String country = address.getCountryCode();
                String adminArea = address.getAdminArea();
                String locality = address.getLocality();
                String featureName = address.getFeatureName();
                String addressLine = address.getAddressLine(1);

                try {

                    Log.i("Country", country);
                    Log.i("adminArea", adminArea);
                    Log.i("Locality", locality);
                    Log.i("FeatureName", featureName);
                    Log.i("AddressLine", addressLine);

                    lastLocation.setCountry(country);
                    lastLocation.setAdminArea(adminArea);
                    lastLocation.setAdminArea(adminArea);
                    lastLocation.setAddress(addressLine);


                } catch (NullPointerException e) {
                    Log.i("NULL", "NULL");
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public boolean checkSMSPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.SEND_SMS)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle("We need sms permission")
                        .setMessage("In order to use the app please give permission.")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(MainActivity.this,
                                        new String[]{Manifest.permission.SEND_SMS},
                                        2);
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.SEND_SMS}, 2);
            }
            return false;
        } else {
            return true;
        }
    }

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle("We need location permission")
                        .setMessage("In order to use the app please give permission.")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(MainActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        1);
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
            return false;
        } else {
            return true;
        }
    }

    public boolean checkInternetPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.INTERNET)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.INTERNET)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle("We need internet permission")
                        .setMessage("In order to use the app please give permission.")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(MainActivity.this,
                                        new String[]{Manifest.permission.INTERNET},
                                        3);
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.INTERNET}, 3);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        //Request location updates:
                        getTheLocationInfo(this);

                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.

                }
                return;
            }

            case 2:  {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.SEND_SMS)
                            == PackageManager.PERMISSION_GRANTED) {

                        //Request location updates:
                       // getTheLocationInfo(this);

                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.

                }
                return;

            }

            case 3:  {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.INTERNET)
                            == PackageManager.PERMISSION_GRANTED) {

                        //Request location updates:
                        // getTheLocationInfo(this);

                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.

                }
                return;

            }

        }
    }


}