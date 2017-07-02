package com.alperenkantarci.alertbutton;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by Alperen Kantarci on 2.07.2017.
 */

public class WidgetService extends IntentService {


    public WidgetService(String name) {
        super("Widget Service");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Log.i("SERVICE SUCCESS","SERVICE SUCCESS");
    }
}
