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

public class BootReceiver extends BroadcastReceiver {

    private final static String TAG = ":::BootReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action == null || !action.equals("android.intent.action.BOOT_COMPLETED"))
            return;

        Log.d(TAG, "boot event received");

        ServiceStarter.Create(context).startServiceIfNeed();
    }
}
