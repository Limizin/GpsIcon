package dev.ldev.gpsicon.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.Toast;

import dev.ldev.gpsicon.C;
import dev.ldev.gpsicon.R;
import dev.ldev.gpsicon.notify.NotifyIconProviderDirector;
import dev.ldev.gpsicon.notify.NotifyIconTypes;
import dev.ldev.gpsicon.services.GpsObserveService;

public class MainActivity extends Activity {

    public final static String QUICK_SWITCH = "quick_swicth";
    private final static String TAG = "main";
    private RadioButton _blinkIconRadio;
    private RadioButton _buIconRadio;
    private boolean _quickSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        _quickSwitch = intent.getBooleanExtra(QUICK_SWITCH, false);
        Log.d(TAG, "quick switch: " + String.valueOf(_quickSwitch));

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String iconType = prefs.getString(C.NOTIFY_ICON_TYPE_KEY, NotifyIconTypes.BLINK);
        NotifyIconProviderDirector.switchIconProvider(iconType);

        _blinkIconRadio = (RadioButton) findViewById(R.id.blinkIconRadio);
        if (iconType.equals(NotifyIconTypes.BLINK))
            _blinkIconRadio.setChecked(true);
        _buIconRadio = (RadioButton) findViewById(R.id.buIconRadio);
        if (iconType.equals(NotifyIconTypes.BU))
            _buIconRadio.setChecked(true);

        _blinkIconRadio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchNotifyIcon(NotifyIconTypes.BLINK);
                if (_quickSwitch)
                    finish();
            }
        });

        _buIconRadio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchNotifyIcon(NotifyIconTypes.BU);
                if (_quickSwitch)
                    finish();
            }
        });

        startServiceIfNeed();
    }

    private void startServiceIfNeed() {
        try {
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                startService(new Intent(this, GpsObserveService.class));
            }
        } catch (Exception e) {
            Log.e(TAG, "start gps icon service error", e);
            Toast.makeText(this, e.getMessage(),
                    Toast.LENGTH_LONG).show();
        }
    }

    private void switchNotifyIcon(String targetIcon) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.edit().putString(C.NOTIFY_ICON_TYPE_KEY, targetIcon).apply();
        NotifyIconProviderDirector.switchIconProvider(targetIcon);
    }
}
