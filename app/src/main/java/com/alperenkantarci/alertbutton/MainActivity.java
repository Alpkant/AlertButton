package com.alperenkantarci.alertbutton;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.Editable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.gms.common.api.BooleanResult;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.alperenkantarci.alertbutton.R.id.username;


public class MainActivity extends AppCompatActivity {
    List<TrustyPerson> trustedPeople;

    Button add_button;
    Button list_button;
    ImageView alarm_button;
    LocationDetails lastLocation;
    FusedLocationProviderClient mFusedLocationClient;
    double longitude, latitude, time;
    float speed, accuracy;
    String Username, Password;
    int numberOfPeople;


    public static boolean hasPermissions(Context context, String... permissions) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }


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
        trustedPeople = new ArrayList<>();

        add_button = (Button) findViewById(R.id.main_add_button);
        list_button = (Button) findViewById(R.id.list_button);
        alarm_button = (ImageView) findViewById(R.id.alarm_button);


        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        final SharedPreferences.Editor editor = preferences.edit();
        final Boolean runBefore = preferences.getBoolean("RunBefore", false);
        if (runBefore == false) {

            editor.putBoolean("RunBefore", true);
            editor.apply();
        }

        numberOfPeople = preferences.getInt("Number", 0);
        for (int i = 0; i < numberOfPeople; i++) {
            String name = preferences.getString(String.valueOf(i) + " name", "");
            String surname = preferences.getString(String.valueOf(i) + " surname", "");
            String countryCode = preferences.getString(String.valueOf(i) + " country", "");
            String phoneNumber = preferences.getString(String.valueOf(i) + " number", "");
            String email = preferences.getString(String.valueOf(i) + " email", "");
            trustedPeople.add(new TrustyPerson(name, surname, countryCode, phoneNumber, email));
        }


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

        final String[] PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.SEND_SMS, Manifest.permission.INTERNET};


        alarm_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                    if (!runBefore) {

                        AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this, R.layout.user_password_layout);
                        final EditText username = new EditText(MainActivity.this);
                        final EditText password = new EditText(MainActivity.this);
                        alert.setTitle("Mail Verification");
                        alert.setMessage("You need to enter your password in order to send mail.");
                        alert.setView(R.layout.user_password_layout);

                        alert.setPositiveButton("Enter", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                Username = username.getText().toString();
                                Password = password.getText().toString();
                                editor.putString("Email", Username);
                                editor.putString("Password", Password);
                                editor.commit();
                            }
                        });

                        alert.setNegativeButton("Don't send message", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {

                            }
                        });

                        alert.show();

                    }

                    if (!hasPermissions(MainActivity.this, PERMISSIONS)) {
                        ActivityCompat.requestPermissions(MainActivity.this, PERMISSIONS, 1);
                    }

                    getTheLocationInfo(MainActivity.this);

                    List<String> alici_liste = new ArrayList<String>();
                    for (int i = 0; i < numberOfPeople; i++) {
                        alici_liste.add(trustedPeople.get(i).getEmail());
                    }

                    Username = preferences.getString("Email", "alperenkantarci@gmail.com");
                    Password = preferences.getString("Password", "Alpbeysubuka4");
                    try {

                        new SendMailTask(MainActivity.this).execute(Username,
                                Password, alici_liste, "THIS IS AN EMERGENCY SITUATION", "I'M IN AN EMERGENCY SITUTATION. I COULD BE KIDNAPPED OR " +
                                        "LOST MY LAST LOCATION IS HERE LONGITUDE: " + String.valueOf(lastLocation.getLongitude()) + " LATITUDE: "
                                        + String.valueOf(lastLocation.getLatitude()) + "\nMY SPEED: " + String.valueOf(lastLocation.getSpeed()));
                    } catch (NullPointerException e) {
                        Log.e("NULL", "NULL");
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

    /*
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
    */
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
                        getTheLocationInfo(MainActivity.this);

                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.

                }
                return;
            }

            case 2: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.SEND_SMS)
                            == PackageManager.PERMISSION_GRANTED) {

                        try {

                            if (lastLocation.getLatitude() != 0) {

                                try {
                                    Log.i("SMS SUCCESS", "SUCCESS");
                                    SmsManager smsManager = SmsManager.getDefault();
                                    smsManager.sendTextMessage("05063014341", null, String.valueOf(lastLocation.getLatitude()), null, null);
                                } catch (NullPointerException e) {
                                    Log.i("NULL", "NULL");
                                }


                            } else {
                                Log.i("SMS FAIL", "FAIL");
                            }
                        } catch (NullPointerException e) {
                            Log.i("NULL", "NULL");
                        }

                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.

                }
                return;

            }

            case 3: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.INTERNET)
                            == PackageManager.PERMISSION_GRANTED) {


                        List<String> alici_liste = new ArrayList<String>();
                        for (int i = 0; i < numberOfPeople; i++) {
                            alici_liste.add(trustedPeople.get(i).getEmail());
                        }
                        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                         SharedPreferences.Editor editor = preferences.edit();
                        Username = preferences.getString("Email", "alperenkantarci@gmail.com");
                        Password = preferences.getString("Password", "Alpbeysubuka4");
                        try {

                            new SendMailTask(MainActivity.this).execute(Username,
                                    Password, alici_liste, "THIS IS AN EMERGENCY SITUATION", "I'M IN AN EMERGENCY SITUTATION. I COULD BE KIDNAPPED OR " +
                                            "LOST MY LAST LOCATION IS HERE LONGITUDE: " + String.valueOf(lastLocation.getLongitude()) + " LATITUDE: "
                                            + String.valueOf(lastLocation.getLatitude()) + "\nMY SPEED: " + String.valueOf(lastLocation.getSpeed()));
                        } catch (NullPointerException e) {
                            Log.e("NULL", "NULL");
                        }

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