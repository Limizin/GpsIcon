package dev.ldev.gpsicon.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.widget.Toast;

import dev.ldev.gpsicon.services.GpsObserveService;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            LocationManager locationManager = (LocationManager) context
                    .getSystemService(Context.LOCATION_SERVICE);

            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                context.startService(new Intent(context, GpsObserveService.class));
            }
        } catch (Exception e) {
            Toast.makeText(context, e.getMessage(),
                    Toast.LENGTH_LONG).show();
            context.stopService(new Intent(context, GpsObserveService.class));
        }
    }
}
