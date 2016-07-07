package dev.ldev.gpsicon.notify;


import android.content.Context;

public class BuNotifyIconProvider implements INotifyIconProvider {

    private String _iconSetName;
    private Context _context;

    public BuNotifyIconProvider(Context context) {
        _iconSetName = "bu";
        _context = context;
    }

    public Integer getSearchIcon(int sattelitesCount) {
        if (sattelitesCount > 9)
            sattelitesCount = 10;
        String animName = _iconSetName + "_location_" + String.valueOf(sattelitesCount);

        String packageName = _context.getPackageName();
        int resId = _context.getResources().getIdentifier(animName, "drawable", packageName);

        return resId;
    }

    public Integer getFixIcon(int sattelitesCount) {
        if (sattelitesCount > 9)
            sattelitesCount = 10;
        String animName = _iconSetName + "_location_" + String.valueOf(sattelitesCount) + "_d";

        String packageName = _context.getPackageName();
        int resId = _context.getResources().getIdentifier(animName, "drawable", packageName);

        return resId;
    }
}
