package dev.ldev.gpsicon.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import dev.ldev.gpsicon.Factory;
import dev.ldev.gpsicon.notify.GpsStatusNotifier;

public class GpsObserveService extends Service implements GpsStatus.Listener, LocationListener {

    private LocationManager _locationManager;
    private GpsStatusNotifier notifier;
    private boolean _isSearchSattelites = false;
    private boolean isGpsFixed = false;

    @Override
    public void onCreate() {
        Log.d("GpsObserveService", "create");
        _locationManager = (LocationManager) getBaseContext()
                .getSystemService(Context.LOCATION_SERVICE);

        _locationManager.addGpsStatusListener(this);
        _locationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, 0L, 0.0f, this);

        notifier = Factory.getInstance().getGpsStatusNotifier(getBaseContext());
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //return super.onStartCommand(intent, flags, startId);

        notifier.hide(); //clear probably pending notification
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.d("GpsObserveService", "destroy");
        _locationManager.removeGpsStatusListener(this);
        _locationManager.removeUpdates(this);
        notifier.hide();
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onGpsStatusChanged(int event) {

        switch (event) {
            case GpsStatus.GPS_EVENT_STARTED:
                //Log.d("GPSSTATUS", "start");
                isGpsFixed = false;
                _isSearchSattelites = true;
                notifier.showStartSearch();
                break;
            case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
                //Log.d("GpsObserveService", "sattelites");

                GpsStatus status = _locationManager.getGpsStatus(null);
                Iterable<GpsSatellite> sats = status.getSatellites();

                if (!_isSearchSattelites) {
                    //check fix lost
                    int usedSattelites = 0;
                    for (GpsSatellite sat : sats) {
                        if (sat.usedInFix())
                            usedSattelites++;
                    }

                    if (usedSattelites < 3) {
                        //Log.d("GPSSTATUS", "restart");
                        isGpsFixed = false;
                        _isSearchSattelites = true;
                        notifier.showStartSearch();
                    } else {
                        notifier.showFix(sats);
                    }
                } else {
                    notifier.showSearch(sats);
                }


                break;
            case GpsStatus.GPS_EVENT_STOPPED:

                //Log.d("GPSSTATUS", "stop");
                isGpsFixed = false;
                _isSearchSattelites = false;

                notifier.hide();

                break;
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        //Log.d("GpsObserveService", "onLOCATION");
        String provider = location.getProvider();
        //String accuracy=String.valueOf(location.getAccuracy());
        //Log.d("GpsObserveService", provider);
        //Log.d("GpsObserveService", accuracy);

        if (provider.equals("gps") && !isGpsFixed) {
            //Log.d("GPSSTATUS", "fix");
            isGpsFixed = true;
            _isSearchSattelites = false;
            notifier.showFix(null);
        }
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {
    }

    //GPSSTATUS state block

    @Override
    public void onProviderEnabled(String s) {
    }

    @Override
    public void onProviderDisabled(String s) {
    }

}
