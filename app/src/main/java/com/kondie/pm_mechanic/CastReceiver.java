package com.kondie.pm_mechanic;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;

import androidx.legacy.content.WakefulBroadcastReceiver;

/**
 * Created by kondie on 2018/02/12.
 */

public class CastReceiver extends WakefulBroadcastReceiver {

    private static final String ACTION_UPDATE_LOCATION_SERVICE = "ACTION_UPDATE_LOCATION_SERVICE";
    private static final int UPDATE_LOC_REQ_CODE = 0;

    @Override
    public void onReceive(Context context, Intent intent) {

        try {
            Intent intentService = null;

            if (intent.getAction().equals(ACTION_UPDATE_LOCATION_SERVICE)) {
                intentService = UpdateLocationService.createUpdateIntent(context);
            }

            if (intentService != null) {
                startWakefulService(context, intentService);
            }
        }catch (Exception e){
        }
    }

    public static void setAlarm(Context context){

        AlarmManager alarmMan = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent pendingIntent = getUpdateLocPendingIntent(context);
        alarmMan.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + 2000, 10000, pendingIntent);
    }

    private static PendingIntent getUpdateLocPendingIntent(Context context){

        Intent intent = new Intent(context, CastReceiver.class);
        intent.setAction(ACTION_UPDATE_LOCATION_SERVICE);
        return (PendingIntent.getBroadcast(context, UPDATE_LOC_REQ_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT));
    }
}
