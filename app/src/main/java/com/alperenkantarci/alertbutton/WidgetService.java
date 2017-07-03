package com.alperenkantarci.alertbutton;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executor;

/**
 * Created by Alperen Kantarci on 2.07.2017.
 */

public class WidgetService extends Service  {
    List<TrustyPerson> trustedPeople;
    FusedLocationProviderClient mFusedLocationClient;
    double longitude, latitude, time;
    float speed, accuracy;
    LocationDetails lastLocation;
    String Username = "";
    String Password = "";
    int numberOfPeople=0;
    Context context;



    @Override
    public int onStartCommand(Intent intent,  int flags, int startId) {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getApplicationContext());
        Executor executor = new Executor() {
            @Override
            public void execute(@NonNull Runnable runnable) {
                runnable.run();
            }

        };
        context = getApplicationContext();
        mFusedLocationClient.getLastLocation().addOnSuccessListener(executor, new OnSuccessListener<Location>() {

            @Override
            public void onSuccess(Location location) {
                SharedPreferences preferences = context.getSharedPreferences("com.alperenkantarci.alertbutton", MODE_MULTI_PROCESS);
                Username = preferences.getString("Email", "error@gmail.com");
                Password = preferences.getString("Password", "error");
                Log.e("USERNAME",Username);
                Log.e("PASSWORD",Password);
                stopSelf();
                /*
                trustedPeople = new ArrayList<TrustyPerson>();
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
                   // SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
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
                    String smsEditedMessage = " THIS IS AN EMERGENCY SITUATION I COULD BE IN DANGER LONGITUDE: " + String.valueOf(lastLocation.getLongitude()) + " LATITUDE: "
                            + String.valueOf(lastLocation.getLatitude()) +
                            "\nI am in the " + lastLocation.getCountry() + ", " +
                            lastLocation.getAdminArea();

                    List<String> alici_liste = new ArrayList<>();
                    for (int i = 0; i < numberOfPeople; i++) {
                        alici_liste.add(trustedPeople.get(i).getEmail());
                    }


                    try {

                        new SendMailTask().execute(Username,
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
                                        PendingIntent sentPI;
                                        String tmp[] = trustedPeople.get(i).getCountry_code().split(",");
                                        String sendNumber = tmp[0] + "" + trustedPeople.get(i).getTelephone_number();
                                        if (trustedPeople.get(i).getCountry_code().equals(tmp[1]) || shouldSendGlobalSms) {
                                            sentPI = PendingIntent.getBroadcast(getApplicationContext(),0,new Intent("SMS_SENT"),0);
                                            smsManager.sendTextMessage(sendNumber, null, smsEditedMessage, sentPI, null);
                                            Log.i("SMS SUCCESS", "SUCCESS");

                                        } else
                                            Toast.makeText(getApplicationContext(), "You selected to not send sms for foreign country telephones so we didn't send.", Toast.LENGTH_LONG).show();
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
                        Toast.makeText(getApplicationContext(), "Your settings disabled to send SMS so we didn't send any sms.\n" +
                                "To change this go to the settings", Toast.LENGTH_LONG).show();
                    }
                } */
            }
        });

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void findAdress(double latitude, double longitude) {

        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.US);
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

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


}
