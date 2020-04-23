package com.example.driverobotproject;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Build;
import android.util.Log;


import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class FirebaseMessageService extends FirebaseMessagingService {

    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);
        Singleton.getInstance().token = token;
        Log.i("Debug", "New Token: "+token);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.i("Debug", "Message received");
        //handle when receive notification via data event
        if (remoteMessage.getData().size() >0) {
            this.sendVisualNotification(remoteMessage.getData().get("title"), remoteMessage.getData().get("message"));
        }

        //handle when receive notification
        if (remoteMessage.getNotification() != null) {
            this.sendVisualNotification(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody());
        }
    }

    private void sendVisualNotification(String messageTitle, String messageBody) {

        // Create an Intent that will be shown when user will click on the Notification
        Intent intent = new Intent(Singleton.getInstance().aCAMainAct.getApplicationContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        // Build a Notification object
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(Singleton.getInstance().aCAMainAct.getApplicationContext(), Singleton.getInstance().aCAMainAct.getString(R.string.CHANNEL_ID))
                        .setSmallIcon(R.drawable.apropos)
                        .setContentTitle(messageTitle)
                        .setContentText(messageBody)
                        .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
                        .setAutoCancel(true)
                        .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                        .setContentIntent(pendingIntent)
                        .setOnlyAlertOnce(true);

        // Add the Notification to the Notification Manager and show it.
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Support Version >= Android 8
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(Singleton.getInstance().aCAMainAct.getString(R.string.CHANNEL_ID), "Firebase_message", NotificationManager.IMPORTANCE_HIGH);
            mChannel.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION), null);
            notificationManager.createNotificationChannel(mChannel);
        }

        // 7 - Show notification
        notificationManager.notify("Firebase", 001, notificationBuilder.build());
    }
}
