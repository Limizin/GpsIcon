package dev.ldev.gpsicon;


import android.content.Context;

import dev.ldev.gpsicon.notify.GpsStatusNotifier;
import dev.ldev.gpsicon.notify.NotifyIconProviderDirector;

public final class Factory {

    private static Factory _instance;

    public static Factory getInstance() {
        if (_instance == null) {
            _instance = new Factory();
        }
        return _instance;
    }

    public GpsStatusNotifier getGpsStatusNotifier(Context context) {
        return new GpsStatusNotifier(context, new NotifyIconProviderDirector(context));
    }

}
