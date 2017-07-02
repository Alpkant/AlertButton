package com.alperenkantarci.alertbutton;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;

import java.util.List;

/**
 * Created by Alperen Kantarci on 2.07.2017.
 */

public class WidgetService extends Service {
    List<TrustyPerson> trustedPeople;
    FusedLocationProviderClient mFusedLocationClient;
    double longitude, latitude, time;
    float speed, accuracy;
    LocationDetails lastLocation;
    String Username = "";
    String Password = "";
    int numberOfPeople=0;

    @Override
    public int onStartCommand(Intent intent,  int flags, int startId) {
        Log.e("ASDF","SAFDFSD");
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


}
