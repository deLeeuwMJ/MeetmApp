package com.jaysonm.meetm.util;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.View;
import android.widget.RemoteViews;

import androidx.annotation.StringRes;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.android.material.snackbar.Snackbar;
import com.jaysonm.meetm.R;

public class NotificationUtil {

    private static final String CHANNEL_CROSSED_ID = "CROSSED_01";
    private static NotificationManagerCompat notificationManager;

    public static void showSnackBar(Activity activity, @StringRes int resId) {
        View rootView = activity.getWindow().getDecorView().findViewById(android.R.id.content);
        Snackbar.make(rootView, resId, Snackbar.LENGTH_SHORT).show();
    }

    public static void showSnackBar(Activity activity, String message) {
        View rootView = activity.getWindow().getDecorView().findViewById(android.R.id.content);
        Snackbar.make(rootView, message, Snackbar.LENGTH_SHORT).show();
    }

    public static void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_CROSSED_ID,
                    "Crossed channel",
                    NotificationManager.IMPORTANCE_HIGH
            );

            NotificationManager manager = context.getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);

            notificationManager = NotificationManagerCompat.from(context);
        }
    }

    public static void showNotification(Context context) {
        RemoteViews collapsedView = new RemoteViews(context.getPackageName(),
                R.layout.notification_crossed_collapsed);

        Notification notification = new NotificationCompat.Builder(context, CHANNEL_CROSSED_ID)
                .setSmallIcon(R.drawable.meetm_icon)
                .setCustomContentView(collapsedView)
                .build();

        notificationManager.notify(1, notification);
    }
}
