package dev.ldev.gpsicon.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import dev.ldev.gpsicon.services.ServiceStarter;

public class RestartReceiver extends BroadcastReceiver {

    private final static String TAG = ":::RestartReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action == null || !action.equals("dev.ldev.gpsicon.RESTART"))
            return;

        Log.d(TAG, "restart event received");

        ServiceStarter.Create(context).startServiceIfNeed();
    }
}