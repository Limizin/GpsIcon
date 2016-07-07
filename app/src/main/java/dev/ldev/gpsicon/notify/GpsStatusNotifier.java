package dev.ldev.gpsicon.notify;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.GpsSatellite;

import dev.ldev.gpsicon.R;
import dev.ldev.gpsicon.ui.MainActivity;
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

    private NotifyIconProviderDirector _notifyIconProvider;

    public GpsStatusNotifier(Context context, NotifyIconProviderDirector notifyIconProvider) {
        _context = context;
        _notifyIconProvider = notifyIconProvider;
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        searchStatus = String.format(_context.getString(R.string.notify_message_template), _context.getString(R.string.notif_search_status));
        searchFoundMsg = _context.getString(R.string.notif_search_found);
        fixStatus = String.format(_context.getString(R.string.notify_message_template), _context.getString(R.string.notif_fix_status));
        fixUsedMsg = _context.getString(R.string.notif_fix_used);
    }

    public void showStartSearch() {
        _lastUsed = 0;
        _lastTotal = 0;
        notifySatSearchEvent(null);
    }

    public void notifySatSearchEvent(Iterable<GpsSatellite> sats) {

        int used = SatteliteUtils.getFoundCount(sats);
        int total = SatteliteUtils.getTotalCount(sats);

        if (total == 0 || used != _lastUsed || total != _lastTotal) {

            Integer animId = _notifyIconProvider.getGpsIconManager().getSearchIcon(used);

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

    public void notifySatFixEvent(Iterable<GpsSatellite> sats) {
        int used = SatteliteUtils.getFixedCount(sats);
        int total = SatteliteUtils.getTotalCount(sats);

        if (total == 0 || used != _lastUsed || total != _lastTotal) {

            Integer animId = _notifyIconProvider.getGpsIconManager().getFixIcon(used);

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

        Intent intent = new Intent(_context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent pIntent = PendingIntent.getActivity(_context, 0, intent, 0);
        notif.contentIntent = pIntent;

        notificationManager.notify(1, notif);
    }
}
