package dev.ldev.gpsicon.notify;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import dev.ldev.gpsicon.C;

public class NotifyIconProviderDirector {

    private static String _iconProviderName;
    private static INotifyIconProvider _iconProvider;
    private Context _context;

    public static void switchIconProvider(String iconProviderName){
        _iconProviderName=iconProviderName;
        _iconProvider=null;
    }

    public NotifyIconProviderDirector(Context context) {
        _context = context;
    }

    public INotifyIconProvider getGpsIconManager() {

        INotifyIconProvider iconProvider=_iconProvider;

        if (iconProvider==null){
            if(_iconProviderName==null){
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(_context);
                String iconType = prefs.getString(C.NotifyIconTypeKey, NotifyIconTypes.Blink);
                iconProvider=getIconProvider(iconType);
            }else{
                iconProvider=getIconProvider(_iconProviderName);
            }
            _iconProvider=iconProvider;
        }

        return iconProvider;
    }

    private INotifyIconProvider getIconProvider(String name){
        if (name.equals(NotifyIconTypes.Bu)) {
            return new BuNotifyIconProvider(_context);
        }
        return new BlinkNotifyIconProvider();
    }
}
