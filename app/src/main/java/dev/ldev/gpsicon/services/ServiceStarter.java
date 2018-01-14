package dev.ldev.gpsicon.services;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.util.Log;

import dev.ldev.gpsicon.Factory;
import dev.ldev.gpsicon.R;
import dev.ldev.gpsicon.notify.GpsStatusNotifier;
import dev.ldev.gpsicon.util.permissions.OnRequestPermissionsResultCallback;
import dev.ldev.gpsicon.util.permissions.PermissionHelper;

public class ServiceStarter implements OnRequestPermissionsResultCallback{

    private final static String TAG = "service_starter";
    private Context _context;
    public final static int PERMISSION_REQUEST_CODE = 1;

    private ServiceStarter(Context _context) {
        this._context = _context;
    }

    public static ServiceStarter Create(Context context){
        return new ServiceStarter(context);
    }

    public void  startServiceIfNeed(){
        stopService();
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
        _context.stopService(new Intent(_context, GpsObserveService.class));
        //close pending icon if proccess was DIRTY killed
        GpsStatusNotifier notifier = Factory.getInstance().getGpsStatusNotifier(_context);
        notifier.hide();
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
        try {
            LocationManager locationManager = (LocationManager) _context.getSystemService(Context.LOCATION_SERVICE);
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                _context.startService(new Intent(_context, GpsObserveService.class));
            }
        } catch (Exception e) {
            Log.e(TAG, "start gps icon service error", e);
            stopService();
        }
    }
}
