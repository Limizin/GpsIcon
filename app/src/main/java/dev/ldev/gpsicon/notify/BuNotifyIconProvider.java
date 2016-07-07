package dev.ldev.gpsicon.notify;


import android.content.Context;

public class BuNotifyIconProvider implements INotifyIconProvider {

    private String _iconSetName;
    private Context _context;
    private String _packageName;

    public BuNotifyIconProvider(Context context) {
        _iconSetName = "bu";
        _context = context;
        _packageName = _context.getPackageName();
    }

    public int getSearchIcon(int sattelitesCount) {
        return getIconResId(sattelitesCount, "");
    }

    public int getFixIcon(int sattelitesCount) {
        return getIconResId(sattelitesCount, "_d");
    }

    private int getIconResId(int sattelitesCount, String iconTypeModifier) {
        if (sattelitesCount > 9)
            sattelitesCount = 10;
        String animName = _iconSetName + "_location_" + String.valueOf(sattelitesCount) + iconTypeModifier;
        return _context.getResources().getIdentifier(animName, "drawable", _packageName);
    }
}
