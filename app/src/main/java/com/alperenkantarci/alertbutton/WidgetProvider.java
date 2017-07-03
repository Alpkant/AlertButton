package com.alperenkantarci.alertbutton;

import android.app.Activity;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.preference.PreferenceManager;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static android.R.style.Widget;
import static android.content.Context.MODE_MULTI_PROCESS;


/**
 * Created by Alperen Kantarci on 27.06.2017.
 */

public class WidgetProvider extends AppWidgetProvider {



    public static String WidgetButton = "android.appwidget.action.APPWIDGET_UPDATE";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        ComponentName thisWidget = new ComponentName(context,
                WidgetProvider.class);
        int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
        for (int widgetId : allWidgetIds) {

            RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
                    R.layout.widget_layout);

            remoteViews.setImageViewResource(R.id.alarm_button_widget, R.drawable.photo1);


            // Register an onClickListener
            Intent intent = new Intent(context, WidgetProvider.class);

            intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);

            PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
                    0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            remoteViews.setOnClickPendingIntent(R.id.alarm_button_widget, pendingIntent);
            appWidgetManager.updateAppWidget(widgetId, remoteViews);

        }

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (WidgetButton.equals(intent.getAction())) {
            SharedPreferences sharedPreferences = context.getSharedPreferences("com.alperenkantarci.alertbutton", MODE_MULTI_PROCESS);
            Boolean firstRun = sharedPreferences.getBoolean("WidgetFirstRun", true);
            if (firstRun) {
                Log.e("onReceive", "onRecieveFirst");
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("WidgetFirstRun", false);
                editor.commit();
            } else {
                context.startService(new Intent(context,WidgetService.class));
                Log.e("onReceive", "onRecieveButonActivitysi");

            }
        }
        super.onReceive(context, intent);
    }


    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        Log.e("onDeleted", "onDeleted");
        SharedPreferences sharedPreferences = context.getSharedPreferences("com.alperenkantarci.alertbutton", MODE_MULTI_PROCESS);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("WidgetFirstRun", true);
        editor.apply();
        super.onDeleted(context, appWidgetIds);
    }


}