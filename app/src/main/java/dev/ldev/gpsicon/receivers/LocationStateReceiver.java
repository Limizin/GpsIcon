package dev.ldev.gpsicon.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import dev.ldev.gpsicon.services.ServiceStarter;


public class LocationStateReceiver extends BroadcastReceiver {

    private final static String TAG = "LocationStateReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            Log.d(TAG, "location state change event received");
            ServiceStarter.Create(context).startServiceIfNeed();
        } catch (Exception e) {
            Log.e(TAG, "start gps icon service error", e);
            Toast.makeText(context, e.getMessage(),
                    Toast.LENGTH_LONG).show();
        }
    }


}
