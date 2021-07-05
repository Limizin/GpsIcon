package dev.ldev.gpsicon.services;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.util.Log;
import android.widget.Toast;

import dev.ldev.gpsicon.BuildConfig;
import dev.ldev.gpsicon.Factory;
import dev.ldev.gpsicon.R;
import dev.ldev.gpsicon.notify.Notifier;
import dev.ldev.gpsicon.util.permissions.OnRequestPermissionsResultCallback;
import dev.ldev.gpsicon.util.permissions.PermissionHelper;

public class ServiceStarter implements OnRequestPermissionsResultCallback{

    private final static String TAG = "service_starter";
    private final Context _context;
    private final static int PERMISSION_REQUEST_CODE = 1;

    private ServiceStarter(Context _context) {
        this._context = _context;
    }

    public static ServiceStarter Create(Context context){
        return new ServiceStarter(context);
    }

    public void  startServiceIfNeed(){
        //stopService();
        if(_context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED){
            _startService();
        }else{
            PermissionHelper.requestPermissions(
                    _context,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSION_REQUEST_CODE,
                    _context.getString(R.string.app_name),
                    _context.getString(R.string.location_perm_request),
                    R.drawable.blink,
                    this);
        }
    }

    public void stopService(){
        Log.d(TAG,"service stop");
        _context.stopService(new Intent(_context, GpsObserveService.class));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        for (int i = 0; i < permissions.length; i++) {
            String permission = permissions[i];
            int grantResult = grantResults[i];

            if (permission.equals(Manifest.permission.ACCESS_FINE_LOCATION)) {
                if (grantResult == PackageManager.PERMISSION_GRANTED) {
                    _startService();
                }
            }
        }
    }

    private void _startService(){
        if (BuildConfig.DEBUG)
            Log.d(TAG, "start service if need");
        try {
            LocationManager locationManager = (LocationManager) _context
                    .getSystemService(Context.LOCATION_SERVICE);

            if (locationManager == null)
                return;

            Notifier notifier = Factory.getInstance().getNotifier(_context);

            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                if (BuildConfig.DEBUG)
                    Log.d(TAG, "start foreground");
                _context.startForegroundService(new Intent(_context, GpsObserveService.class));
            } else {
                if (BuildConfig.DEBUG)
                    Log.d(TAG, "gps not enabled");
                _context.stopService(new Intent(_context, GpsObserveService.class));
            }

            notifier.updateLocationState();
        } catch (Exception e) {
            Log.e(TAG, "start gps icon service error", e);
            Toast.makeText(_context, e.getMessage(),
                    Toast.LENGTH_LONG).show();
            stopService();
        }
    }
}
