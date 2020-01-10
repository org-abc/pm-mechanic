package com.kondie.pm_mechanic;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class SetAsDelivered  extends AsyncTask<String, Void, String> {

    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        MainActivity.coolLoading.startCoolLoadingAnim();
    }

    @Override
    protected String doInBackground(String... params) {

        try {
            prefs = MainActivity.activity.getSharedPreferences("PM_M", Context.MODE_PRIVATE);
            editor = prefs.edit();
            URL url = new URL(Constants.PM_HOSTING_WEBSITE + "/setRequestAsResolved.php");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);

            Uri.Builder builder = new Uri.Builder().appendQueryParameter("reqId", params[0]);
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
                return ("Connection Failed " + String.valueOf(resCode));
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
                Toast.makeText(MainActivity.activity, "Order delivered", Toast.LENGTH_SHORT).show();
                MainActivity.requestItems.clear();
                new GetRequests().execute("5050-00-00 00:00:00");
            }else{
                Toast.makeText(MainActivity.activity, s, Toast.LENGTH_SHORT).show();
            }

        }catch (Exception e){
            Toast.makeText(MainActivity.activity, e.toString() + s, Toast.LENGTH_SHORT).show();
        }
    }
}
