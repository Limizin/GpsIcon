package dev.ldev.gpsicon.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.util.Log;
import android.widget.Toast;

import dev.ldev.gpsicon.Factory;
import dev.ldev.gpsicon.notify.Notifier;
import dev.ldev.gpsicon.services.GpsObserveService;
import dev.ldev.gpsicon.services.ServiceStarter;


public class LocationStateReceiver extends BroadcastReceiver {

    private final static String TAG = ":::LocationState";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action == null || !action.equals("android.location.PROVIDERS_CHANGED"))
            return;

        Log.d(TAG, "location state change event received");

        ServiceStarter.Create(context).startServiceIfNeed();
    }


}
