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
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class MainActivity extends AppCompatActivity {
    List<TrustyPerson> trustedPeople;

    Button add_button;
    Button list_button;
    ImageView alarm_button;
    LocationDetails lastLocation;
    FusedLocationProviderClient mFusedLocationClient;
    double longitude, latitude, time;
    float speed, accuracy;
    String Username = "";
    String Password = "";
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, FragmentActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
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
                            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                            findAdress(latitude, longitude);
                            Boolean shouldSendSms = preferences.getBoolean("ShouldSendSms", true);
                            Boolean shouldSendGlobalSms = preferences.getBoolean("ShouldSendGlobalSms", true);

                            numberOfPeople = preferences.getInt("Number", 0);
                            for (int i = 0; i < numberOfPeople; i++) {
                                String name = preferences.getString(String.valueOf(i) + " name", "");
                                String surname = preferences.getString(String.valueOf(i) + " surname", "");
                                String countryCode = preferences.getString(String.valueOf(i) + " country", "");
                                String phoneNumber = preferences.getString(String.valueOf(i) + " number", "");
                                String email = preferences.getString(String.valueOf(i) + " email", "");
                                trustedPeople.add(new TrustyPerson(name, surname, countryCode, phoneNumber, email));
                            }
                            String editedMessage = "I'M IN AN EMERGENCY SITUTATION. I COULD BE KIDNAPPED OR " +
                                    "LOST MY LAST LOCATION IS HERE \nLONGITUDE: " + String.valueOf(lastLocation.getLongitude()) + " LATITUDE: "
                                    + String.valueOf(lastLocation.getLatitude()) +
                                    "\nI am in the " + lastLocation.getCountry() + ", " +
                                    lastLocation.getAdminArea() + "\n" + "\nMY SPEED: " +
                                    String.valueOf(lastLocation.getSpeed());

                            List<String> alici_liste = new ArrayList<String>();
                            for (int i = 0; i < numberOfPeople; i++) {
                                alici_liste.add(trustedPeople.get(i).getEmail());
                            }


                            try {

                                new SendMailTask(MainActivity.this).execute(Username,
                                        Password, alici_liste, "EMERGENCY CALL PLEASE HELP ME!", editedMessage);
                            } catch (NullPointerException e) {
                                Log.e("NULL", "NULL");
                            }

                            if (shouldSendSms) {
                                for (int i = 0; i < numberOfPeople; i++) {
                                    try {
                                        if (lastLocation.getLatitude() != 0) {
                                            try {
                                                SmsManager smsManager = SmsManager.getDefault();
                                                String tmp[] = trustedPeople.get(i).getCountry_code().split(",");
                                                String sendNumber = tmp[0] + "" + trustedPeople.get(i).getTelephone_number();
                                                if (trustedPeople.get(i).getCountry_code().equals(tmp[1]) || shouldSendGlobalSms) {
                                                    smsManager.sendTextMessage(sendNumber, null, editedMessage, null, null);
                                                    Log.i("SMS SUCCESS", "SUCCESS");

                                                } else
                                                    Toast.makeText(MainActivity.this, "You selected to not send sms for foreign country telephones so we didn't send.", Toast.LENGTH_LONG).show();
                                            } catch (NullPointerException e) {
                                                Log.i("SMS ERROR", "NULL");
                                            }
                                        }
                                    } catch (
                                            NullPointerException e) {
                                        Log.i("NULL", "NULL");
                                    }
                                }
                            } else {
                                Toast.makeText(MainActivity.this, "Your settings disabled to send SMS so we didn't send any sms.\n" +
                                        "To change this go to the settings", Toast.LENGTH_LONG).show();
                            }
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


        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = preferences.edit();
        Boolean runBefore = preferences.getBoolean("RunBefore", false);
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
        if (!runBefore) {

            AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this, R.layout.user_password_layout);
            final EditText username = new EditText(MainActivity.this);
            final EditText password = new EditText(MainActivity.this);
            alert.setTitle("Mail Verification");
            alert.setMessage("You need to enter your mail and password in order to send mail. \n" +
                    "Your data doesn't share by anyone.This is an open source project so you can check" +
                    " it out if you want to see the code.");
            alert.setView(R.layout.user_password_layout);


            alert.setPositiveButton("Enter", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    Username = username.getText().toString();
                    Password = password.getText().toString();
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


        alarm_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor = preferences.edit();

                if (!Username.equals("")) {
                    editor.putString("Email", Username);
                    editor.putString("Password", Password);
                    editor.commit();
                } else {
                    Username = preferences.getString("Email", "alperenkantarci@gmail.com");
                    Password = preferences.getString("Password", "Alpbeysubuka4");
                }

                Log.e("USERNAME", Username);
                Log.e("PASSWORD", Password);

                getTheLocationInfo(MainActivity.this);


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

                try {

                    Log.i("Country", country);
                    Log.i("adminArea", adminArea);

                    lastLocation.setCountry(country);
                    lastLocation.setAdminArea(adminArea);
                    lastLocation.setAdminArea(adminArea);


                } catch (NullPointerException e) {
                    Log.i("NULL", "NULL");
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
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


                    } else {

                        // permission denied, boo! Disable the
                        // functionality that depends on this permission.

                    }
                    return;

                }

            }
        }


    }
}