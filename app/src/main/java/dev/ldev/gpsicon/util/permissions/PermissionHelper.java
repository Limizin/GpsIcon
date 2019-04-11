package dev.ldev.gpsicon.util.permissions;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.ResultReceiver;

/**
 * Provides helper methods to request permissions from components other than Activities.
 */
public class PermissionHelper {
    private static final String TAG = PermissionHelper.class.getSimpleName();


    /**
     * Requests permissions to be granted to this application.
     * <p>
     * This method is a wrapper around
     * {@link android.app.Activity#requestPermissions(String[], int)}
     * which works in a similar way, except it can be called from non-activity contexts. When called, it
     * displays a notification with a customizable title and text. When the user taps the notification, an
     * activity is launched in which the user is prompted to allow or deny the request.
     * <p>
     * After the user has made a choice,
     * {@link OnRequestPermissionsResultCallback#onRequestPermissionsResult(int, String[], int[])}
     * is called, reporting whether the permissions were granted or not.
     *
     * @param context           The context from which the request was made. The context supplied must implement
     *                          {@link OnRequestPermissionsResultCallback} and will receive the
     *                          result of the operation.
     * @param permissions       The requested permissions
     * @param requestCode       Application specific request code to match with a result reported to
     *                          {@link OnRequestPermissionsResultCallback#onRequestPermissionsResult(int, String[], int[])}
     * @param notificationTitle The title for the notification
     * @param notificationText  The text for the notification
     * @param notificationIcon  Resource identifier for the notification icon
     */
    public static <T extends Context> void requestPermissions(
            final T context,
            String[] permissions,
            int requestCode,
            String notificationTitle,
            String notificationText,
            int notificationIcon,
            final OnRequestPermissionsResultCallback callback) {

        ResultReceiver resultReceiver = new ResultReceiver(new Handler(Looper.getMainLooper())) {
            @Override
            protected void onReceiveResult(int resultCode, Bundle resultData) {
                String[] outPermissions = resultData.getStringArray(Const.KEY_PERMISSIONS);
                int[] grantResults = resultData.getIntArray(Const.KEY_GRANT_RESULTS);
                callback.onRequestPermissionsResult(resultCode, outPermissions, grantResults);
            }
        };

        Intent permIntent = new Intent(context, PermissionRequestActivity.class);
        permIntent.putExtra(Const.KEY_GRANT_RESULT_RECEIVER, resultReceiver);
        permIntent.putExtra(Const.KEY_PERMISSIONS, permissions);
        permIntent.putExtra(Const.KEY_REQUEST_CODE, requestCode);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addNextIntent(permIntent);

        PendingIntent permPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel notificationChannel = new NotificationChannel(Const.NOTIFY_PERMISSION_CHANNEL, Const.NOTIFY_PERMISSION_CHANNEL, importance);
            notificationManager.createNotificationChannel(notificationChannel);
        }
        Notification.Builder builder = new Notification.Builder(context)
                .setSmallIcon(notificationIcon)
                .setContentTitle(notificationTitle)
                .setContentText(notificationText)
                .setOngoing(true)
                //.setCategory(Notification.CATEGORY_STATUS)
                .setAutoCancel(true)
                .setWhen(0)
                .setContentIntent(permPendingIntent)
                .setStyle(null);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            builder.setChannelId(Const.NOTIFY_PERMISSION_CHANNEL);
        }

        notificationManager.notify(requestCode, builder.build());
    }


}