package dev.ldev.gpsicon.notify.icons;


public interface INotifyIconProvider {

    int getSearchIcon(int sattelitesCount);

    int getFixIcon(int sattelitesCount);

    int getNetworkLocationEnabledIcon();

    int getGpsLocationEnabledIcon();
}
