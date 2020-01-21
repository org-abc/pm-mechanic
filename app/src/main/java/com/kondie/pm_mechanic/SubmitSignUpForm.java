package com.kondie.pm_mechanic;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
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

/**
 * Created by kondie on 2018/02/09.
 */

public class SubmitSignUpForm extends AsyncTask<String, Void, String> {

    private ProgressDialog pDialog;
    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    private String email, pass, fname, lname, phone;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        pDialog = new ProgressDialog(SignUp.activity);
        pDialog.setMessage("Saving details...");
        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pDialog.setCancelable(false);
        pDialog.show();
    }

    @Override
    protected String doInBackground(String... params) {

        try {
            prefs = SignUp.activity.getSharedPreferences("PM_M", Context.MODE_PRIVATE);
            editor = prefs.edit();
            fname = params[0];
            lname = params[1];
            email = params[2];
            phone = params[3];
            pass = params[4];

            URL url = new URL(Constants.PM_HOSTING_WEBSITE + "/signUpMechanic.php");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setDoInput(true);

            Uri.Builder builder = new Uri.Builder().appendQueryParameter("fname", params[0])
                    .appendQueryParameter("lname", params[1])
                    .appendQueryParameter("email", params[2])
                    .appendQueryParameter("phone", params[3])
                    .appendQueryParameter("password", params[4])
                    .appendQueryParameter("minServiceFee", params[5])
                    .appendQueryParameter("imageData", params[6])
                    .appendQueryParameter("imageName", params[7])
                    .appendQueryParameter("IdImageData", params[8])
                    .appendQueryParameter("IdImageName", params[9]);
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

        try {
            pDialog.dismiss();
            if (s.equalsIgnoreCase("congrats"))
            {
                editor.putString("email", email);
                editor.putString("password", pass);
                editor.commit();
                Intent gotoMain = new Intent(SignUp.activity, MainActivity.class);
                if (s.equals("CONGRATS")){
                    gotoMain.putExtra("status", "busy");
                }else{
                    gotoMain.putExtra("status", "free");
                }
                SignUp.activity.startActivity(gotoMain);
                SignUp.activity.finish();
            }
            else
            {
                Toast.makeText(SignUp.activity, "Something went wrong."+s+"__", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(SignUp.activity, e.toString() + s, Toast.LENGTH_SHORT).show();
        }
    }
}