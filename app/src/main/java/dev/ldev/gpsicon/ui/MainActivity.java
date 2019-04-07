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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.Toast;

import dev.ldev.gpsicon.C;
import dev.ldev.gpsicon.Factory;
import dev.ldev.gpsicon.R;
import dev.ldev.gpsicon.notify.Notifier;
import dev.ldev.gpsicon.notify.icons.NotifyIconSetNames;
import dev.ldev.gpsicon.services.GpsObserveService;

public class MainActivity extends Activity {

    public final static String QUICK_SWITCH = "quick_swicth";
    private final static String TAG = "main";
    private CheckBox _showNetworkLocationState;
    private boolean _quickSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Factory.getInstance().init(this);

        Intent intent = getIntent();
        _quickSwitch = intent.getBooleanExtra(QUICK_SWITCH, false);
        Log.d(TAG, "quick switch: " + String.valueOf(_quickSwitch));

        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String iconType = prefs.getString(C.NOTIFY_ICON_TYPE_KEY, NotifyIconSetNames.BLINK);

        RadioButton _blinkIconRadio = (RadioButton) findViewById(R.id.blinkIconRadio);
        if (iconType.equals(NotifyIconSetNames.BLINK))
            _blinkIconRadio.setChecked(true);
        RadioButton _buIconRadio = (RadioButton) findViewById(R.id.buIconRadio);
        if (iconType.equals(NotifyIconSetNames.BU))
            _buIconRadio.setChecked(true);

        _blinkIconRadio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchNotifyIcon(NotifyIconSetNames.BLINK);
                if (_quickSwitch)
                    finish();
            }
        });

        _buIconRadio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchNotifyIcon(NotifyIconSetNames.BU);
                if (_quickSwitch)
                    finish();
            }
        });

        CheckBox _showGpsLocationState = (CheckBox) findViewById(R.id.showGpsLocationState);
        _showNetworkLocationState = (CheckBox) findViewById(R.id.showNetworkLocationState);

        boolean showGpsLocationState = prefs.getBoolean(C.SHOW_GPS_LOCATION_STATE, false);
        _showGpsLocationState.setChecked(showGpsLocationState);
        _showGpsLocationState.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                prefs.edit().putBoolean(C.SHOW_GPS_LOCATION_STATE, b).apply();
                if (!b)
                    _showNetworkLocationState.setChecked(false);
                _showNetworkLocationState.setEnabled(b);
            }
        });

        boolean showNetworkLocationState = prefs.getBoolean(C.SHOW_NETWORK_LOCATION_STATE, false);
        _showNetworkLocationState.setChecked(showNetworkLocationState);
        _showNetworkLocationState.setEnabled(_showGpsLocationState.isChecked());
        _showNetworkLocationState.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                prefs.edit().putBoolean(C.SHOW_NETWORK_LOCATION_STATE, b).apply();
            }
        });

        startServiceIfNeed();
    }

    private void startServiceIfNeed() {
        try {
            LocationManager locationManager = (LocationManager) this
                    .getSystemService(Context.LOCATION_SERVICE);

            if (locationManager == null)
                return;

            Notifier notifier = Factory.getInstance().getNotifier(this);

            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                this.startService(new Intent(this, GpsObserveService.class));
            } else {
                this.stopService(new Intent(this, GpsObserveService.class));
            }

            notifier.updateLocationState();
        } catch (Exception e) {
            Log.e(TAG, "start gps icon service error", e);
            Toast.makeText(this, e.getMessage(),
                    Toast.LENGTH_LONG).show();
        }
    }

    private void switchNotifyIcon(String targetIcon) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.edit().putString(C.NOTIFY_ICON_TYPE_KEY, targetIcon).apply();
    }
}
