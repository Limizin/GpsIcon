package dev.ldev.gpsicon;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.os.SystemClock;

import dev.ldev.gpsicon.receivers.LocationStateReceiver;
import dev.ldev.gpsicon.receivers.RestartReceiver;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        BroadcastReceiver br = new LocationStateReceiver();
        IntentFilter filter = new IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION);
        this.registerReceiver(br, filter);


    }

    // This is called when the overall system is running low on memory,
    // and would like actively running processes to tighten their belts.
    // Overriding this method is totally optional!
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        Intent intent = new Intent();
        intent.setAction("dev.ldev.gpsicon.RESTART");
        intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        intent.setComponent(
                new ComponentName("dev.ldev.gpsicon","dev.ldev.gpsicon.receivers.RestartReceiver"));

        @SuppressLint("UnspecifiedImmutableFlag")
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 7, intent, 0);
        AlarmManager alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + 5000, pendingIntent);
    }


}