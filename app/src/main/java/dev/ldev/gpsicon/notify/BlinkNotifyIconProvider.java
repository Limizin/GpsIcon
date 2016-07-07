package dev.ldev.gpsicon.notify;


import dev.ldev.gpsicon.R;

public class BlinkNotifyIconProvider implements INotifyIconProvider {

    @Override
    public Integer getSearchIcon(int sattelitesCount) {
        return R.drawable.blink_anim;
    }

    @Override
    public Integer getFixIcon(int sattelitesCount) {
        return R.drawable.blink_d;
    }
}
