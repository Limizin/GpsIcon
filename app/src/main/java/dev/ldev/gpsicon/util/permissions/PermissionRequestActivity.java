package dev.ldev.gpsicon.util.permissions;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.ResultReceiver;

/**
 * A blank {@link Activity} on top of which permission request dialogs can be displayed
 */
public class PermissionRequestActivity extends Activity {

    ResultReceiver resultReceiver;
    String[] permissions;
    int requestCode;

    /**
     * Called when the user has made a choice in the permission dialog.
     * <p>
     * This method wraps the responses in a {@link Bundle} and passes it to the {@link ResultReceiver}
     * specified in the {@link Intent} that started the activity, then closes the activity.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        Bundle resultData = new Bundle();
        resultData.putStringArray(Const.KEY_PERMISSIONS, permissions);
        resultData.putIntArray(Const.KEY_GRANT_RESULTS, grantResults);
        resultReceiver.send(requestCode, resultData);
        finish();
    }


    /**
     * Called when the activity is started.
     * <p>
     * This method obtains several extras from the {@link Intent} that started the activity: the request
     * code, the requested permissions and the {@link ResultReceiver} which will receive the results.
     * After that, it issues the permission request.
     */
    @Override
    protected void onStart() {
        super.onStart();

        resultReceiver = this.getIntent().getParcelableExtra(Const.KEY_GRANT_RESULT_RECEIVER);
        permissions = this.getIntent().getStringArrayExtra(Const.KEY_PERMISSIONS);
        requestCode = this.getIntent().getIntExtra(Const.KEY_REQUEST_CODE, 0);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permissions, requestCode);
        }
    }
}
