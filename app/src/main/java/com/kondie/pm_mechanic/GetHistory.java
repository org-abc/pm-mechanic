package com.kondie.pm_mechanic;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.view.View;
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

public class GetHistory extends AsyncTask<String, Void, String> {

    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;

    @Override
    protected String doInBackground(String... params) {

        try {
            prefs = HistoryAct.activity.getSharedPreferences("PM_M", Context.MODE_PRIVATE);
            editor = prefs.edit();
            URL url = new URL(Constants.PM_HOSTING_WEBSITE + "/getMechanicHistory.php");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);

            Uri.Builder builder = new Uri.Builder().appendQueryParameter("dateCreated", params[0])
                    .appendQueryParameter("email", prefs.getString("email", ""));
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
            if (s.equals("empty")){
                if (MainActivity.historyItems.size() == 0) {
                    Toast.makeText(HistoryAct.activity, "You have no history", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(HistoryAct.activity, "You have any more history", Toast.LENGTH_SHORT).show();
                }
            }
            else {
                HistoryAct.getProgressBar().setVisibility(View.GONE);
                HistoryAct.getLoadMoreProgressBar().setVisibility(View.GONE);
                JSONObject allDAta = new JSONObject(s);
                String requestsString = allDAta.getString("requests");
                String userString = allDAta.getString("users");

                JSONArray requestsArr = new JSONArray(requestsString);
                JSONArray userArr = new JSONArray(userString);

                for (int c = 0; c < requestsArr.length(); c++) {

                    HistoryItem item = new HistoryItem();
                    JSONObject request = requestsArr.getJSONObject(c);
                    try {
                        JSONObject user = userArr.getJSONObject(c);
                        item.setClientName(user.getString("fname") + " " + user.getString("lname"));
                    } catch (Exception e) {
                        item.setClientName("None");
                    }
                    item.setDateCreated(request.getString("date_created"));
                    item.setStatus(request.getString("status"));
                    item.setId(String.valueOf(request.getInt("id")));
                    item.setMinServiceFee(String.valueOf(request.getDouble("min_service_fee")));
                    item.setLat(request.getDouble("user_lat"));
                    item.setLng(request.getDouble("user_lng"));
                    item.setIssue(request.getString("issue"));
                    item.setCar(request.getString("make_and_model"));
                    item.setComment(request.getString("comment"));

                    MainActivity.historyItems.add(item);
                }
                HistoryAct.historyAdapter.notifyDataSetChanged();
                HistoryAdapter.setLoaded();
            }

        }catch (Exception e){
            HistoryAct.getProgressBar().setVisibility(View.GONE);
            HistoryAct.getLinearLayout().setVisibility(View.VISIBLE);
        }
    }
}
