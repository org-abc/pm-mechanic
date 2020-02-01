package com.kondie.pm_mechanic;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class UserProfile extends AppCompatActivity {

    Toolbar toolbar;
    SharedPreferences prefs;
    private TextView fullName, email, phone;
    private ImageView userDp, callClientButt;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_profile);

        try {
            prefs = getSharedPreferences("PM_M", MODE_PRIVATE);
            toolbar = findViewById(R.id.user_profile_toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            fullName = findViewById(R.id.user_profile_full_name);
            email = findViewById(R.id.user_profile_email);
            phone = findViewById(R.id.user_profile_phone);
            userDp = findViewById(R.id.user_profile_dp);
            callClientButt = findViewById(R.id.call_client_butt);

            setValues();
            callClientButt.setOnClickListener(callClient);

        }catch (Exception e){
        }
    }

    private View.OnClickListener callClient = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent callIntent = new Intent(Intent.ACTION_DIAL);
            callIntent.setData(Uri.parse("tel:" + prefs.getString("phone", "")));
            startActivity(callIntent);
        }
    };

    private void setValues(){
        fullName.setText(getIntent().getExtras().getString("lname") + " " + getIntent().getExtras().getString("fname"));
        email.setText(getIntent().getExtras().getString("email"));
        phone.setText(getIntent().getExtras().getString("phone"));
        Picasso.with(this).load(getIntent().getExtras().getString("imagePath").replace(Constants.WRONG_PART, Constants.CORRECT_PART)).placeholder(R.drawable.user_icon).into(userDp);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setValues();
    }
}
