package com.kondie.pm_mechanic;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.provider.Settings;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.lang.reflect.Array;

public class NotificationHelper {


    public static void sendNotif(Context context, String tittle, String contentText){
        try {
            Intent toMainIntent = new Intent(context, MainActivity.class);
            toMainIntent.putExtra("track", "yes");
            toMainIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            PendingIntent toMainPIntent = PendingIntent.getActivity(context, MainActivity.NOTIF_ID, toMainIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            long vibrationPattern[] = {1000, 1000, 1000, 1000, 1000};
            NotificationCompat.Builder notif = new NotificationCompat.Builder(context, MainActivity.CHANNEL_ID);
            notif.setContentTitle(tittle)
                    .setContentText(contentText)
                    .setAutoCancel(true)
                    .setSmallIcon(R.drawable.notification_icon)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setSound(Uri.parse("android.resource://"+context.getPackageName()+"/"+R.raw.pm_notif))
                    .setVibrate(vibrationPattern)
                    .setContentIntent(toMainPIntent)
                    .setDeleteIntent(getDeleteIntent(context));

            NotificationManagerCompat notifMan = NotificationManagerCompat.from(context);
            notifMan.notify(MainActivity.NOTIF_ID, notif.build());
        }catch (Exception e){
//            Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show();
        }
    }

    public static PendingIntent getDeleteIntent(Context context){

        Intent intent = new Intent(context, MainActivity.class);
        intent.setAction(MainActivity.ACTION_DELETE_NOTIFICATION);
        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

}
