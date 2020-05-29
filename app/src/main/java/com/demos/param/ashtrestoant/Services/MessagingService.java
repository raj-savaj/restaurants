package com.demos.param.ashtrestoant.Services;

import android.app.Notification;
import android.app.NotificationManager;
import android.support.v4.app.NotificationCompat;

import com.demos.param.ashtrestoant.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by root on 25/3/18.
 */

public class MessagingService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Notify(remoteMessage.getNotification().getBody());
        super.onMessageReceived(remoteMessage);
    }
    public void  Notify(String msg)
    {
        Notification notification= new NotificationCompat.Builder(this,"1")
                .setSmallIcon(R.drawable.noti)
                .setAutoCancel(true)
                .setContentTitle("Firbase Notification")
                .setContentText(msg)
                .build();
        NotificationManager notificationManager=(NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(1,notification);
    }
}
