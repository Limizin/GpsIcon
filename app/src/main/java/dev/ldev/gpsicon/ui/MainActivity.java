package dev.ldev.gpsicon.ui;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.RadioButton;

import dev.ldev.gpsicon.BuildConfig;
import dev.ldev.gpsicon.C;
import dev.ldev.gpsicon.R;
import dev.ldev.gpsicon.notify.NotifyIconProviderDirector;
import dev.ldev.gpsicon.notify.NotifyIconTypes;

public class MainActivity extends Activity {

    private RadioButton _blinkIconRadio;
    private RadioButton _buIconRadio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String iconType = prefs.getString(C.NotifyIconTypeKey, NotifyIconTypes.Blink);
        NotifyIconProviderDirector.switchIconProvider(iconType);

        _blinkIconRadio = (RadioButton) findViewById(R.id.blinkIconRadio);
        if (iconType.equals(NotifyIconTypes.Blink))
            _blinkIconRadio.setChecked(true);
        _buIconRadio = (RadioButton) findViewById(R.id.buIconRadio);
        if (iconType.equals(NotifyIconTypes.Bu))
            _buIconRadio.setChecked(true);

        _blinkIconRadio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchNotifyIcon(NotifyIconTypes.Blink);
            }
        });

        _buIconRadio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchNotifyIcon(NotifyIconTypes.Bu);
            }
        });
    }

    private void switchNotifyIcon(String targetIcon) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.edit().putString(C.NotifyIconTypeKey, targetIcon).apply();
        NotifyIconProviderDirector.switchIconProvider(targetIcon);
    }
}
