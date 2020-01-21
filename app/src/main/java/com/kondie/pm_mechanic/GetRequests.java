package com.kondie.pm_mechanic;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
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

/**
 * Created by kondie on 2018/07/05.
 */

public class GetRequests extends AsyncTask<String, Void, String> {

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
            URL url = new URL(Constants.PM_HOSTING_WEBSITE + "/getRequests.php");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);

            Uri.Builder builder = new Uri.Builder().appendQueryParameter("dateCreated", params[0])
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
            MainActivity.requestItems.clear();
            MainActivity.coolLoading.stopCoolLoadingAnim();
            JSONObject allDAta = new JSONObject(s);
            String reqsString = allDAta.getString("requests");
            String usersString = allDAta.getString("users");

            JSONArray reqsArr = new JSONArray(reqsString);
            JSONArray usersArr = new JSONArray(usersString);

            if (MainActivity.requestItems.size() == 0 && reqsArr.length() > 0){
                MainActivity.sendNotif("Request", "There is someone who needs help around your area", "kok");
            }
            MainActivity.requestItems.clear();

            for(int c=0; c<reqsArr.length(); c++){

                RequestItem item = new RequestItem();
                JSONObject req = reqsArr.getJSONObject(c);
                JSONObject user = usersArr.getJSONObject(c);
                item.setClientFName(user.getString("fname"));
                item.setClientLName(user.getString("lname"));
                item.setClientEmail(req.getString("user_email"));
                item.setClientPhone(user.getString("phone"));
                item.setLat(req.getDouble("user_lat"));
                item.setLng(req.getDouble("user_lng"));
                item.setStatus(req.getString("status"));
                item.setRequestComment(req.getString("comment"));
                item.setIssue(req.getString("issue"));
                item.setMakeAndModel(req.getString("make_and_model"));
                item.setImagePath(user.getString("image_path"));
                item.setDateCreated(req.getString("date_created"));
                item.setId(String.valueOf(req.getInt("id")));
                item.setServiceFee(String.valueOf(req.getDouble("min_service_fee")));

//                if (DistanceCalc.distance(MainActivity.userLocation.getLatitude(), MainActivity.userLocation.getLongitude(), item.shopLat, item.shopLng) <= 20000) {
                    MainActivity.requestItems.add(item);
//                }
            }
            MainActivity.requestItemAdapter.notifyDataSetChanged();

        }catch (Exception e){
            if (s.equalsIgnoreCase("conn")){
                new AlertDialog.Builder(MainActivity.activity).setCancelable(false).setTitle("Something went wrong")
                        .setPositiveButton("RETRY", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                                new GetRequests().execute("5050-00-00 00:00:00");
                            }
                        }).show();
            }
            else if (MainActivity.requestItems.size() == 0){
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        new GetRequests().execute("5050-00-00 00:00:00");
                    }
                }, 60000);
//                new AlertDialog.Builder(MainActivity.activity).setCancelable(true).setTitle("No orders found")
//                        .setPositiveButton("Refresh", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialogInterface, int i) {
//                                dialogInterface.dismiss();
//                                new GetRequests().execute("5050-00-00 00:00:00");
//                            }
//                        })
//                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialogInterface, int i) {
//                                dialogInterface.dismiss();
//                            }
//                        }).show();
            }
        }
    }
}
