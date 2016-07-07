package dev.ldev.gpsicon.notify;


public interface INotifyIconProvider {

    public Integer getSearchIcon(int sattelitesCount);

    public Integer getFixIcon(int sattelitesCount);
}
