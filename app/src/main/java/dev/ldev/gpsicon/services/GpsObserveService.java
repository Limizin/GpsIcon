package dev.ldev.gpsicon.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import dev.ldev.gpsicon.BuildConfig;
import dev.ldev.gpsicon.Factory;
import dev.ldev.gpsicon.R;
import dev.ldev.gpsicon.notify.Notifier;
import dev.ldev.gpsicon.util.SatteliteUtils;

@SuppressWarnings("deprecation")
public class GpsObserveService extends Service implements GpsStatus.Listener, LocationListener {

    private final static String TAG = ":::GpsObserveService";

    private LocationManager _locationManager;
    private Notifier notifier;
    private boolean _isSearchSattelites = false;
    private boolean isGpsFixed = false;

    @SuppressWarnings("MissingPermission")
    @Override
    public void onCreate() {
        Log.i(TAG, "create");
        super.onCreate();
        notifier = Factory.getInstance().getNotifier(getBaseContext());
        String channel = notifier.initChannel();
        Notification notification = new Notification.Builder(this, channel)
                .setContentTitle("")
                .setContentText("")
                .setColor(1)
                .setSmallIcon(R.drawable.blink).build();

        startForeground(1, notification);
        _locationManager = (LocationManager) getBaseContext()
                .getSystemService(Context.LOCATION_SERVICE);

        assert _locationManager != null;
        _locationManager.addGpsStatusListener(this);
        _locationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, 0L, 0.0f, this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //return super.onStartCommand(intent, flags, startId);


        notifier.updateLocationState(); //clear probably pending notification
        return START_STICKY;
    }

    @SuppressWarnings("MissingPermission")
    @Override
    public void onDestroy() {
        Log.i(TAG, "destroy");
        _locationManager.removeGpsStatusListener(this);
        _locationManager.removeUpdates(this);
        notifier.finishGpsSearch();
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @SuppressWarnings("MissingPermission")
    @Override
    public void onGpsStatusChanged(int event) {

        switch (event) {
            case GpsStatus.GPS_EVENT_STARTED:
                if (BuildConfig.DEBUG)
                    Log.v(TAG, "GpsStatus.GPS_EVENT_STARTED");

                isGpsFixed = false;
                _isSearchSattelites = true;
                notifier.notifyStartGpsSearch();
                break;
            case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
                if (BuildConfig.DEBUG)
                    Log.v(TAG, "GpsStatus.GPS_EVENT_SATELLITE_STATUS");

                GpsStatus status = _locationManager.getGpsStatus(null);
                assert status != null;
                Iterable<GpsSatellite> sats = status.getSatellites();

                if (!_isSearchSattelites) {
                    //check fix lost
                    int usedSattelites = 0;
                    for (GpsSatellite sat : sats) {
                        if (sat.usedInFix())
                            usedSattelites++;
                    }

                    if (usedSattelites < 3) {
                        if (BuildConfig.DEBUG)
                            Log.d(TAG, "fix is lost. restart search");

                        isGpsFixed = false;
                        _isSearchSattelites = true;
                        notifier.notifyStartGpsSearch();
                    } else {
                        if (BuildConfig.DEBUG)
                            Log.d(TAG, String.format("fix. %d sattelites", usedSattelites));
                        notifier.notifySatFixEvent(sats);
                    }
                } else {
                    if (BuildConfig.DEBUG) {
                        int used = SatteliteUtils.getFoundCount(sats);
                        int total = SatteliteUtils.getTotalCount(sats);
                        Log.d(TAG, String.format("no fix. continue %d/%d", used, total));
                    }
                    notifier.notifySatSearchEvent(sats);
                }
                break;
            case GpsStatus.GPS_EVENT_STOPPED:
                if (BuildConfig.DEBUG)
                    Log.v(TAG, "GpsStatus.GPS_EVENT_STOPPED");

                isGpsFixed = false;
                _isSearchSattelites = false;

                notifier.finishGpsSearch();

                break;
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        String provider = location.getProvider();
        if (BuildConfig.DEBUG)
            Log.v(TAG, "onLocationChanged: "+provider);

        if (provider.equals("gps") && !isGpsFixed) {
            if (BuildConfig.DEBUG)
                Log.d(TAG, "FIX");
            isGpsFixed = true;
            _isSearchSattelites = false;
            notifier.notifySatFixEvent(null);
        }
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {
    }

    @Override
    public void onProviderEnabled(String s) {
    }

    @Override
    public void onProviderDisabled(String s) {
    }


}
