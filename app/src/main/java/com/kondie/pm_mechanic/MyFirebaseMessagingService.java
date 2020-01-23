package com.kondie.pm_mechanic;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        if (remoteMessage.getNotification() != null){
            String title = remoteMessage.getNotification().getTitle();
            String msg = remoteMessage.getNotification().getBody();

            NotificationHelper.sendNotif(getApplicationContext(), title, msg);
        }
    }
}
