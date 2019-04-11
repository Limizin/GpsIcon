package dev.ldev.gpsicon.notify;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.GpsSatellite;
import android.location.LocationManager;
import android.preference.PreferenceManager;

import dev.ldev.gpsicon.C;
import dev.ldev.gpsicon.R;
import dev.ldev.gpsicon.notify.icons.BlinkNotifyIconProvider;
import dev.ldev.gpsicon.notify.icons.BuNotifyIconProvider;
import dev.ldev.gpsicon.notify.icons.INotifyIconProvider;
import dev.ldev.gpsicon.notify.icons.NotifyIconSetNames;
import dev.ldev.gpsicon.ui.MainActivity;
import dev.ldev.gpsicon.util.SatteliteUtils;

public class Notifier implements SharedPreferences.OnSharedPreferenceChangeListener {

    private final static String TAG = ":::Notifier";

    private final String searchStatus;
    private final String searchFoundMsg;
    private final String fixStatus;
    private final String fixUsedMsg;
    private boolean _showNetworkLocationState;
    private boolean _showGpsLocationState;
    private boolean _networkLocationEnabled;
    private boolean _gpsLocationEnabled;
    private boolean _isGpsWorkActive;
    private NotificationManager notificationManager;
    private INotifyIconProvider _iconProvider;
    private Context _context;
    private int _lastUsed;
    private int _lastTotal;

    public Notifier(Context context) {
        _context = context;

        searchStatus = String.format(_context.getString(R.string.notify_message_template), _context.getString(R.string.notif_search_status));
        searchFoundMsg = _context.getString(R.string.notif_search_found);
        fixStatus = String.format(_context.getString(R.string.notify_message_template), _context.getString(R.string.notif_fix_status));
        fixUsedMsg = _context.getString(R.string.notif_fix_used);

        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel notificationChannel = new NotificationChannel(C.NOTIFY_CHANNEL, C.NOTIFY_CHANNEL, importance);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        prefs.registerOnSharedPreferenceChangeListener(this);
        _showGpsLocationState = prefs.getBoolean(C.SHOW_GPS_LOCATION_STATE, false);
        _showNetworkLocationState = prefs.getBoolean(C.SHOW_NETWORK_LOCATION_STATE, false);

        String iconType = prefs.getString(C.NOTIFY_ICON_TYPE_KEY, NotifyIconSetNames.BLINK);
        switchIconSet(iconType);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences prefs, String s) {
        if (s.equals(C.SHOW_NETWORK_LOCATION_STATE))
            _showNetworkLocationState = prefs.getBoolean(C.SHOW_NETWORK_LOCATION_STATE, false);
        if (s.equals(C.SHOW_GPS_LOCATION_STATE))
            _showGpsLocationState = prefs.getBoolean(C.SHOW_GPS_LOCATION_STATE, false);
        if (s.equals(C.NOTIFY_ICON_TYPE_KEY)) {
            String iconType = prefs.getString(C.NOTIFY_ICON_TYPE_KEY, NotifyIconSetNames.BLINK);
            switchIconSet(iconType);
        }
        proccessLocationState();
    }

    public void updateLocationState() {
        //Log.v(TAG, "updateLocationState");
        LocationManager locationManager = (LocationManager) _context
                .getSystemService(Context.LOCATION_SERVICE);

        if (locationManager == null)
            return;

        _networkLocationEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        _gpsLocationEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        proccessLocationState();
    }

    public void notifyStartGpsSearch() {
        _lastUsed = 0;
        _lastTotal = 0;
        _isGpsWorkActive = true;
        notifySatSearchEvent(null);
    }

    public void notifySatSearchEvent(Iterable<GpsSatellite> sats) {
        int used = SatteliteUtils.getFoundCount(sats);
        int total = SatteliteUtils.getTotalCount(sats);

        if (total == 0 || used != _lastUsed || total != _lastTotal) {

            int animId = _iconProvider.getSearchIcon(used);
            String msg = searchFoundMsg + ": " + used + "/" + total;
            showNotify(animId, searchStatus, msg);

            _lastUsed = used;
            _lastTotal = total;
        }
    }

    public void notifySatFixEvent(Iterable<GpsSatellite> sats) {
        int used = SatteliteUtils.getFixedCount(sats);
        int total = SatteliteUtils.getTotalCount(sats);

        if (total == 0 || used != _lastUsed || total != _lastTotal) {

            int animId = _iconProvider.getFixIcon(used);

            String msg = fixUsedMsg + ": " + used + "/" + total;
            showNotify(animId, fixStatus, msg);

            _lastUsed = used;
            _lastTotal = total;
        }
    }

    public void finishGpsSearch() {
        //Log.v(TAG, "finishGpsSearch");
        _isGpsWorkActive = false;
        proccessLocationState();
    }

    private void switchIconSet(String iconSetName) {
        if (iconSetName.equals(NotifyIconSetNames.BU)) {
            _iconProvider = new BuNotifyIconProvider(_context);
            return;
        }
        _iconProvider = new BlinkNotifyIconProvider();
    }

    private void proccessLocationState() {
        //Log.v(TAG, "proccessLocationState");
        if (_isGpsWorkActive)
            return;

        if (_gpsLocationEnabled && _showGpsLocationState) {
            int iconId = _iconProvider.getGpsLocationEnabledIcon();
            //Log.v(TAG, "show gps location active");
            showNotify(iconId, _context.getString(R.string.app_name), _context.getString(R.string.gps_location_active));
            return;
        }

        if (_networkLocationEnabled && _showNetworkLocationState) {
            int iconId = _iconProvider.getNetworkLocationEnabledIcon();
            //Log.v(TAG, "show network location active");
            showNotify(iconId, _context.getString(R.string.app_name), _context.getString(R.string.network_location_active));
            return;
        }

        hideNotification();
    }


    private void showNotify(int iconId, String title, String message) {
        Notification.Builder builder = new Notification.Builder(_context);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            builder.setChannelId(C.NOTIFY_CHANNEL);
        }
        builder.setContentTitle(title);
        builder.setContentText(message);
        builder.setSmallIcon(iconId);
        builder.setOngoing(true);
        Notification notif = builder.build();

        notif.flags |= Notification.FLAG_ONGOING_EVENT;
        notif.flags |= Notification.FLAG_NO_CLEAR;

        Intent intent = new Intent(_context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra(MainActivity.QUICK_SWITCH, true);

        notif.contentIntent = PendingIntent.getActivity(_context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        notificationManager.notify(1, notif);
    }

    private void hideNotification() {
        //Log.v(TAG, "hide notifictaion");
        notificationManager.cancel(1);
    }

}
