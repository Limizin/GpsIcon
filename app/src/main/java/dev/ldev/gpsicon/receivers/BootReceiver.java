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

public class BootReceiver extends BroadcastReceiver {

    private final static String TAG = ":::BootReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        try {

            String action = intent.getAction();
            if (action == null || !action.equals("android.intent.action.BOOT_COMPLETED"))
                return;

            Log.d(TAG, "boot event received");

            LocationManager locationManager = (LocationManager) context
                    .getSystemService(Context.LOCATION_SERVICE);

            if (locationManager == null)
                return;

            Notifier notifier = Factory.getInstance().getNotifier(context);

            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                context.startService(new Intent(context, GpsObserveService.class));
            } else {
                context.stopService(new Intent(context, GpsObserveService.class));
            }

            notifier.updateLocationState();

        } catch (Exception e) {
            Log.e(TAG, "start gps icon service error", e);
            Toast.makeText(context, e.getMessage(),
                    Toast.LENGTH_LONG).show();
            context.stopService(new Intent(context, GpsObserveService.class));
        }
    }
}
