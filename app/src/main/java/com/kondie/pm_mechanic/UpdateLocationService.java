package com.kondie.pm_mechanic;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import androidx.annotation.Nullable;
import androidx.legacy.content.WakefulBroadcastReceiver;

/**
 * Created by kondie on 2018/02/12.
 */

public class UpdateLocationService extends IntentService {

    private static final String ACTION_START_UPDATE = "ACTION_START_UPDATE";
    SharedPreferences prefs;

    public UpdateLocationService() {
        super(UpdateLocationService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        try {
            String action = intent.getAction();
            Toast.makeText(this, "this is running bra...", Toast.LENGTH_SHORT).show();

            if (action.equals(ACTION_START_UPDATE)) {
                startLocUpdate();
            }
        }finally {
            WakefulBroadcastReceiver.completeWakefulIntent(intent);
        }
    }

    private void startLocUpdate(){

        try {
            prefs = MainActivity.activity.getSharedPreferences("PM_M", Context.MODE_PRIVATE);
            URL url = new URL(Constants.PM_HOSTING_WEBSITE + "/updateDriverLoc.php");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setDoInput(true);

            Uri.Builder builder = new Uri.Builder().appendQueryParameter("userEmail", prefs.getString("userEmail", ""))
                    .appendQueryParameter("driverEmail", prefs.getString("email", ""))
                    .appendQueryParameter("lat",  String.valueOf(MainActivity.userLocation.getLatitude()))
                    .appendQueryParameter("lng", String.valueOf(MainActivity.userLocation.getLongitude()));
            String query = builder.build().getEncodedQuery();

            OutputStream outStream = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outStream));
            writer.write(query);
            writer.flush();
            writer.close();
            outStream.close();
            conn.connect();

            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {

                InputStream inStream = conn.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inStream));
                StringBuilder result = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }
                inStream.close();

                if (!result.equals("null")) {
                    JSONObject locOb = new JSONObject(result.toString());
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putFloat("driverLat", (float) locOb.getDouble("lat"));
                    editor.putFloat("driverLng", (float) locOb.getDouble("lng"));
                    editor.commit();
                    MainActivity.clientLat = (float) locOb.getDouble("lat");
                    MainActivity.clientLng = (float) locOb.getDouble("lng");
                    MainActivity.updateLoc();
                }
            } else {
                Toast.makeText(this, "conn problems", Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            Toast.makeText(this, "Here: " + e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    public static Intent createUpdateIntent(Context context){
        Intent intent = new Intent(context, UpdateLocationService.class);
        intent.setAction(ACTION_START_UPDATE);
        return intent;
    }
}
