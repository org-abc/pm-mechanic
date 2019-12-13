package com.kondie.pm_mechanic;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;

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
    private ProgressDialog progressDialog;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog = new ProgressDialog(HistoryAct.activity);
        progressDialog.setTitle("Collecting history...");
        progressDialog.show();
    }

    @Override
    protected String doInBackground(String... params) {

        try {
            prefs = HistoryAct.activity.getSharedPreferences("PM_M", Context.MODE_PRIVATE);
            editor = prefs.edit();
            URL url = new URL(Constants.PM_HOSTING_WEBSITE + "/getDriverHistory.php");
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
            progressDialog.dismiss();
            JSONObject allDAta = new JSONObject(s);
            String ordersString = allDAta.getString("orders");
            String userString = allDAta.getString("users");
            String shopsString = allDAta.getString("shops");
            String itemsString = allDAta.getString("items");

            JSONArray ordersArr = new JSONArray(ordersString);
            JSONArray userArr = new JSONArray(userString);
            JSONArray shopsArr = new JSONArray(shopsString);
            JSONArray itemsArr = new JSONArray(itemsString);

            for(int c=0; c<ordersArr.length(); c++){

                HistoryItem item = new HistoryItem();
                JSONObject order = ordersArr.getJSONObject(c);
                try{
                    JSONObject user = userArr.getJSONObject(c);
                    item.setDriverName(user.getString("fname") + " " + user.getString("lname"));
                }catch (Exception e){
                    item.setDriverName("None");
                }
                item.setOrderAmount(order.getString("amount"));
                item.setOrderDetails(order.getString("order_details"));
                item.setDateCreated(order.getString("date_created"));
                item.setStatus(order.getString("status"));
                item.setId(String.valueOf(order.getInt("id")));
                item.setDeliveryFee(String.valueOf(order.getDouble("delivery_fee")));

                String orderName = "";
                JSONArray detailsArr = new JSONArray(order.getString("order_details"));
                for (int j=0; j<detailsArr.length(); j++) {
                    JSONObject detailsObj = detailsArr.getJSONObject(j);
                    for (int i=0; i<itemsArr.length(); i++){
                        JSONObject itemObj = itemsArr.getJSONObject(i);
                        if (detailsObj.getString("itemId").equals(String.valueOf(itemObj.getInt("id")))) {
                            orderName += detailsObj.getString("quantity") + " " + itemObj.getString("name") + " (R" + itemObj.getDouble("price") + ")";
                            orderName += (i != itemsArr.length() - 1) ? ", " : "";
                            break;
                        }
                    }
                    for (int k=0; k < shopsArr.length(); k++) {
                        JSONObject shopObj = shopsArr.getJSONObject(k);
                        if (detailsObj.getString("shopId").equals(String.valueOf(shopObj.getInt("id")))) {
                            item.setShopName(shopObj.getString("name"));
                            item.setShopLat(shopObj.getDouble("lat"));
                            item.setShopLng(shopObj.getDouble("lng"));
                        }
                    }
                }
                item.setOrderName(orderName);

                HistoryAct.historyItems.add(item);
            }
            HistoryAct.historyAdapter.notifyDataSetChanged();

        }catch (Exception e){
            new AlertDialog.Builder(HistoryAct.activity).setCancelable(false).setTitle("Something went wrong. Retry?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                            new GetHistory().execute("5050-00-00 00:00:00");
                        }
                    })
                    .setNegativeButton("no", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            HistoryAct.activity.finish();
                        }
                    }).show();
        }
    }
}
