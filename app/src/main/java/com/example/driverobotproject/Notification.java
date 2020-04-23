package com.example.driverobotproject;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Random;

public class Notification {

    private static final Notification instance = new Notification();
    public static Notification getInstance() { return instance; }

    public void initNotification() {
        //Firebase
        FirebaseMessaging.getInstance().setAutoInitEnabled(true);
        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
            @Override
            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                if(!task.isSuccessful()){
                    Log.i("Debug", "Task failed ");
                    return;
                }
                Log.i("Debug", "The result : "+task.getResult().getToken());
            }
        });


        //create notification channel
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel notificationChannel = new NotificationChannel(Singleton.getInstance().aCAMainAct.getString(R.string.CHANNEL_ID), Singleton.getInstance().aCAMainAct.getString(R.string.CHANNEL_NAME), NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.setDescription("");
            notificationChannel.setShowBadge(true);
            NotificationManager notificationManager = Singleton.getInstance().aCAMainAct.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }

    public void sendNotification(String s){
        FirebaseMessaging.getInstance().send(new RemoteMessage.Builder(FirebaseInstanceId.getInstance().getId())
                .setMessageId("1")
                .addData("message", s)
                .build());
    }
}
