package com.kondie.pm_mechanic;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.view.View;
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

public class SubmitSignInForm extends AsyncTask<String, Void, String> {

    private ProgressDialog pDialog;
    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    private String email, pass, origin;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        try {
            pDialog = new ProgressDialog(SignIn.activity);
            pDialog.setMessage("Logging in...");
            pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pDialog.setCancelable(false);
            pDialog.show();
        }catch (Exception e){}
    }

    @Override
    protected String doInBackground(String... params) {

        try {
            email = params[0];
            pass = params[1];
            origin = params[2];
            if (origin.equalsIgnoreCase("login")) {
                prefs = SignIn.activity.getSharedPreferences("PM_M", Context.MODE_PRIVATE);
            }else{
                prefs = WelcomeAct.activity.getSharedPreferences("PM_M", Context.MODE_PRIVATE);
            }
            editor = prefs.edit();

            URL url = new URL(Constants.PM_HOSTING_WEBSITE + "/loginMechanic.php");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setDoInput(true);

            Uri.Builder builder = new Uri.Builder().appendQueryParameter("email", params[0])
                    .appendQueryParameter("password", params[1]);
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
            if (origin.equalsIgnoreCase("login")) {
                pDialog.dismiss();
            }

            if (s.equalsIgnoreCase("active") || s.equalsIgnoreCase("inactive"))
            {
                if (s.equalsIgnoreCase("active")){
                    editor.putString("accStatus", "active");
                }else if (s.equalsIgnoreCase("inactive")){
                    editor.putString("accStatus", "inactive");
                }
                if (origin.equalsIgnoreCase("login")) {
                    Toast.makeText(SignIn.activity, "Done", Toast.LENGTH_SHORT).show();
                    editor.putString("email", email);
                    editor.putString("password", pass);
                    Intent gotoMain = new Intent(SignIn.activity, MainActivity.class);
                    SignIn.activity.startActivity(gotoMain);
                    SignIn.activity.finish();
                }
                else if (origin.equalsIgnoreCase("welcome"))
                {
                    Intent gotoMain = new Intent(WelcomeAct.activity, MainActivity.class);
                    WelcomeAct.activity.startActivity(gotoMain);
                    WelcomeAct.activity.finish();
                }
                editor.commit();
            }
            else if (s.equalsIgnoreCase("sorry") && origin.equalsIgnoreCase("welcome"))
            {
                Intent toLolginIntent = new Intent(WelcomeAct.activity, SignIn.class);
                WelcomeAct.activity.startActivity(toLolginIntent);
                WelcomeAct.activity.finish();
            }
            else
            {
                if (origin.equalsIgnoreCase("login")) {
                    Toast.makeText(SignIn.activity, "Something went wrong.", Toast.LENGTH_SHORT).show();
                }
                else{
                    WelcomeAct.getProgressBar().setVisibility(View.GONE);
                    WelcomeAct.getLinearLayout().setVisibility(View.VISIBLE);
                }
            }
        } catch (Exception e) {
            if (origin.equalsIgnoreCase("login")) {
                Toast.makeText(SignIn.activity, "Something went wrong.", Toast.LENGTH_SHORT).show();
            }
            else{
                WelcomeAct.getProgressBar().setVisibility(View.GONE);
                WelcomeAct.getLinearLayout().setVisibility(View.VISIBLE);
            }
        }
    }
}