package com.kondie.pm_mechanic;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
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

public class UpdateLocation extends AsyncTask<String, Void, String> {

    SharedPreferences prefs;
    private static boolean isLoading = false;

    @Override
    protected String doInBackground(String... strings) {
        try {
            if (isLoading){
                return "loading";
            }
            isLoading = true;
            prefs = MainActivity.activity.getSharedPreferences("PM_M", Context.MODE_PRIVATE);
            URL url = new URL(Constants.PM_HOSTING_WEBSITE + "/updateMechanicLoc.php");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setDoInput(true);

            Uri.Builder builder = new Uri.Builder().appendQueryParameter("mechanicEmail", prefs.getString("email", ""))
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

                return result.toString();
            } else {
                return "conn problems";
            }

        } catch (Exception e) {
            return e.toString();
        }
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        int delayTime = 54000000;
        if (prefs.getString("status", "").equalsIgnoreCase("busy")){
            delayTime = 10000;
        }
        if (!s.equalsIgnoreCase("loading")) {
            isLoading = false;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    new UpdateLocation().execute();
                }
            }, delayTime);
        }
    }
}
