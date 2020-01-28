package com.kondie.pm_mechanic;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class RequestItemHolder extends RecyclerView.ViewHolder {

    TextView fullName, fname, hStatus, lname, email, phone, lat, lng, shopLat, shopLng, requestComment, serviceFee, requestId, imagePath, requestIssue, makeAndModel;
    ImageView clientDp;
    Button acceptButt;
    SharedPreferences prefs;
    public static String duration;

    public RequestItemHolder(@NonNull View requestItemView) {
        super(requestItemView);

        prefs = MainActivity.activity.getSharedPreferences("PM_M", Context.MODE_PRIVATE);
        fullName = requestItemView.findViewById(R.id.client_name);
        clientDp = requestItemView.findViewById(R.id.client_dp);
        acceptButt = requestItemView.findViewById(R.id.accept_request_butt);
        lname = requestItemView.findViewById(R.id.client_hidden_lname);
        fname = requestItemView.findViewById(R.id.client_hidden_fname);
        email = requestItemView.findViewById(R.id.client_hidden_email);
        lat = requestItemView.findViewById(R.id.client_hidden_lat);
        lng = requestItemView.findViewById(R.id.client_hidden_lng);
        shopLat = requestItemView.findViewById(R.id.shop_hidden_lat);
        shopLng = requestItemView.findViewById(R.id.shop_hidden_lng);
        phone = requestItemView.findViewById(R.id.client_hidden_phone);
        requestComment = requestItemView.findViewById(R.id.request_comment);
        requestId = requestItemView.findViewById(R.id.request_id);
        imagePath = requestItemView.findViewById(R.id.client_hidden_image_path);
        requestIssue = requestItemView.findViewById(R.id.request_issue);
        makeAndModel = requestItemView.findViewById(R.id.request_make_and_model);
        serviceFee = requestItemView.findViewById(R.id.service_fee);
        hStatus = requestItemView.findViewById(R.id.client_hidden_request_status);

        acceptButt.setOnClickListener(acceptRequest);
        requestItemView.setOnClickListener(showOnMap);
        clientDp.setOnClickListener(showUserProfile);
    }

    private View.OnClickListener showUserProfile = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent toProfileIntent = new Intent(MainActivity.activity, UserProfile.class);
            toProfileIntent.putExtra("lname", lname.getText().toString());
            toProfileIntent.putExtra("fname", fname.getText().toString());
            toProfileIntent.putExtra("phone", phone.getText().toString());
            toProfileIntent.putExtra("email", email.getText().toString());
            toProfileIntent.putExtra("imagePath", imagePath.getText().toString());
            MainActivity.activity.startActivity(toProfileIntent);
        }
    };

    private View.OnClickListener showOnMap = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            try {
                showOnMapFunc();
            }catch (NullPointerException e){
                Toast.makeText(MainActivity.activity, "Still preparing the map...", Toast.LENGTH_SHORT).show();
            }
        }
    };

    private void showOnMapFunc(){
        MainActivity.navGoogleMap.clear();
        duration = "";
        LatLng clientLatLng = new LatLng(Double.valueOf(lat.getText().toString()), Double.valueOf(lng.getText().toString()));
        MainActivity.navGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(MainActivity.driverLatLng, 17));
        MainActivity.navGoogleMap.addMarker(new MarkerOptions().position(clientLatLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.map_user)).title(fname.getText().toString() + " " + lname.getText().toString()));

        new GetDirections().execute((float) MainActivity.userLocation.getLatitude(), (float) MainActivity.userLocation.getLongitude(), Float.valueOf(lat.getText().toString()), Float.valueOf(lng.getText().toString()), (float) 0);
        MainActivity.showLessOrMoreFunc();
    }

    private View.OnClickListener acceptRequest = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (hStatus.getText().toString().equalsIgnoreCase("waiting")) {
                new AcceptRequest().execute(requestId.getText().toString(), email.getText().toString(),
                        imagePath.getText().toString(), phone.getText().toString(), lat.getText().toString(), lng.getText().toString());
                showOnMapFunc();
            }
            else if (hStatus.getText().toString().equalsIgnoreCase("accept") || hStatus.getText().toString().equalsIgnoreCase("arrived")){
                new AlertDialog.Builder(MainActivity.activity).setCancelable(false).setMessage("Are you sure you about this?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                new SetAsDelivered().execute(requestId.getText().toString(), acceptButt.getText().toString().toLowerCase());
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        }).show();
            }
        }
    };
}
