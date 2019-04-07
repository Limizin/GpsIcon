package dev.ldev.gpsicon.notify.icons;


import android.content.Context;

import dev.ldev.gpsicon.R;

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

    @Override
    public int getNetworkLocationEnabledIcon() {
        return R.drawable.network_location_enabled;
    }

    @Override
    public int getGpsLocationEnabledIcon() {
        return R.drawable.gps_location_enabled;
    }

    private int getIconResId(int sattelitesCount, String iconTypeModifier) {
        if (sattelitesCount > 9)
            sattelitesCount = 10;
        String animName = _iconSetName + "_location_" + String.valueOf(sattelitesCount) + iconTypeModifier;
        return _context.getResources().getIdentifier(animName, "drawable", _packageName);
    }
}
