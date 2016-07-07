package dev.ldev.gpsicon.notify;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.location.GpsSatellite;

import dev.ldev.gpsicon.R;
import dev.ldev.gpsicon.util.SatteliteUtils;


public class GpsStatusNotifier {

    private static NotificationManager notificationManager;
    private final String searchStatus;
    private final String searchFoundMsg;
    private final String fixStatus;
    private final String fixUsedMsg;
    private Context _context;
    private int _lastUsed;
    private int _lastTotal;

    private INotifyIconProvider _notifyIconProvider;

    public GpsStatusNotifier(Context context, INotifyIconProvider notifyIconProvider) {
        this._context = context;
        _notifyIconProvider = notifyIconProvider;
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        searchStatus = String.format("GPS (%s)", context.getString(R.string.notif_search_status));
        searchFoundMsg = context.getString(R.string.notif_search_found);
        fixStatus = String.format("GPS (%s)", context.getString(R.string.notif_fix_status));
        fixUsedMsg = context.getString(R.string.notif_fix_used);
    }

    public void showStartSearch() {
        _lastUsed = 0;
        _lastTotal = 0;
        showSearch(null);
    }

    public void showSearch(Iterable<GpsSatellite> sats) {

        int used = SatteliteUtils.getFoundCount(sats);
        int total = SatteliteUtils.getTotalCount(sats);

        if (total == 0 || used != _lastUsed || total != _lastTotal) {

            Integer animId = _notifyIconProvider.getSearchIcon(used);

            Notification notif = new Notification(animId, null,
                    System.currentTimeMillis());

            String msg = searchFoundMsg + ": " + used + "/" + total;
            notif.setLatestEventInfo(_context, searchStatus,
                    msg, null);
            showNotify(notif);

            _lastUsed = used;
            _lastTotal = total;
        }
    }

    public void showFix(Iterable<GpsSatellite> sats) {
        int used = SatteliteUtils.getFixedCount(sats);
        int total = SatteliteUtils.getTotalCount(sats);

        if (total == 0 || used != _lastUsed || total != _lastTotal) {

            Integer animId = _notifyIconProvider.getFixIcon(used);

            Notification notif = new Notification(animId, null,
                    System.currentTimeMillis());


            String msg = fixUsedMsg + ": " + used + "/" + total;
            notif.setLatestEventInfo(_context, fixStatus,
                    msg, null);
            showNotify(notif);

            _lastUsed = used;
            _lastTotal = total;
        }
    }

    public void hide() {
        notificationManager.cancel(1);
    }


    private void showNotify(Notification notif) {
        notif.flags |= Notification.FLAG_ONGOING_EVENT;
        notif.flags |= Notification.FLAG_NO_CLEAR;
        notificationManager.notify(1, notif);
    }
}
