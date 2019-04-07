package dev.ldev.gpsicon;


import android.content.Context;

import dev.ldev.gpsicon.notify.Notifier;

public final class Factory {

    private static Factory _instance;
    private Notifier _notifier;

    public static Factory getInstance() {
        if (_instance == null) {
            _instance = new Factory();
        }
        return _instance;
    }

    public void init(Context context) {
        getNotifier(context);
    }

    public Notifier getNotifier(Context context) {
        if (_notifier == null) {
            _notifier = new Notifier(context);
        }
        return _notifier;
    }
}
