package com.kondie.pm_mechanic;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by kondie on 2018/07/05.
 */

public class AcceptRequest extends AsyncTask<String, Void, String> {

    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private String userLat, userLng, userImagePath, userPhone, userEmail, requestId;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        MainActivity.coolLoading.startCoolLoadingAnim();
    }

    @Override
    protected String doInBackground(String... params) {

        try {
            requestId = params[0];
            userEmail = params[1];
            userImagePath = params[2];
            userPhone = params[3];
            userLat = params[4];
            userLng = params[5];
            prefs = MainActivity.activity.getSharedPreferences("PM_M", Context.MODE_PRIVATE);
            editor = prefs.edit();
            URL url = new URL(Constants.PM_HOSTING_WEBSITE + "/acceptRequest.php");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);

            Uri.Builder builder = new Uri.Builder().appendQueryParameter("requestId", params[0])
                    .appendQueryParameter("mechanicEmail", prefs.getString("email", ""));
            String query = builder.build().getEncodedQuery();

            OutputStream outStream = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outStream));

            writer.write(query);
            writer.flush();
            writer.close();
            outStream.close();
            conn.connect();

            int resCode = conn.getResponseCode();
            if (resCode == HttpURLConnection.HTTP_OK)
            {
                InputStream inStream = conn.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inStream));
                StringBuilder result = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null)
                {
                    result.append(line);
                }
                inStream.close();
                return (result.toString());
            }
            else
            {
                return ("Connection Failed " + resCode);
            }
        }
        catch(Exception e)
        {
            return ("Conn, " + e.toString());
        }
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

        try{
            MainActivity.coolLoading.stopCoolLoadingAnim();
            if (s.equalsIgnoreCase("congrats")){
                Toast.makeText(MainActivity.activity, "Request accepted", Toast.LENGTH_SHORT).show();

                JSONArray ja;
                if (prefs.getString("status", "").equals("free")){
                    ja = new JSONArray();
                }
                else {
                    ja = new JSONArray(prefs.getString("requests", ""));
                }
                JSONObject jo = new JSONObject();

                jo.put("requestId", requestId);
                jo.put("userEmail", userEmail);
                jo.put("userLat", Float.valueOf(userLat));
                jo.put("userLng", Float.valueOf(userLng));
                jo.put("userImagePath", userImagePath);
                jo.put("userPhone", userPhone);

                ja.put(jo);

                editor.putString("requests", ja.toString());
                editor.putString("status", "busy");
                editor.commit();
//                CastReceiver.setAlarm(MainActivity.activity);
                new GetRequests().execute(MainActivity.latestRequestDate);
            }

        }catch (Exception e){
//            Toast.makeText(MainActivity.activity, e.toString() + s, Toast.LENGTH_LONG).show();
        }
    }
}
