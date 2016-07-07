package dev.ldev.gpsicon.notify;


public interface INotifyIconProvider {

    int getSearchIcon(int sattelitesCount);

    int getFixIcon(int sattelitesCount);
}
