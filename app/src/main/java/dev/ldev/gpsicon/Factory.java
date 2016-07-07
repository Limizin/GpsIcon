package dev.ldev.gpsicon;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import dev.ldev.gpsicon.notify.BlinkNotifyIconProvider;
import dev.ldev.gpsicon.notify.BuNotifyIconProvider;
import dev.ldev.gpsicon.notify.GpsStatusNotifier;
import dev.ldev.gpsicon.notify.INotifyIconProvider;
import dev.ldev.gpsicon.notify.NotifyIconTypes;

public final class Factory {

    private static Factory _instance;

    public static Factory getInstance() {
        if (_instance == null) {
            _instance = new Factory();
        }
        return _instance;
    }

    public GpsStatusNotifier getGpsStatusNotifier(Context context) {
        return new GpsStatusNotifier(context, getGpsIconManager(context));
    }

    public INotifyIconProvider getGpsIconManager(Context context) {

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String iconType = prefs.getString(C.NotifyIconTypeKey, NotifyIconTypes.Blink);

        if (iconType.equals(NotifyIconTypes.Bu)) {
            return new BuNotifyIconProvider(context);
        }
        return new BlinkNotifyIconProvider();
    }
}
