package dev.ldev.gpsicon.notify.icons;


import dev.ldev.gpsicon.R;

public class BlinkNotifyIconProvider implements INotifyIconProvider {

    @Override
    public int getSearchIcon(int sattelitesCount) {
        return R.drawable.blink_anim;
    }

    @Override
    public int getFixIcon(int sattelitesCount) {
        return R.drawable.blink_d;
    }

    @Override
    public int getNetworkLocationEnabledIcon() {
        return R.drawable.network_location_enabled;
    }

    @Override
    public int getGpsLocationEnabledIcon() {
        return R.drawable.gps_location_enabled;
    }
}
