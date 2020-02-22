package com.kondie.pm_mechanic;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class UpdateProfile extends AppCompatActivity {

    Toolbar toolbar;
    SharedPreferences prefs;
    private EditText fname, lname, phone;
    private ImageView userDp, userID, userQualification;
    private TextView updateButt;
    public static Activity activity;
    final int OPEN_DP_GALLERY_CODE = 0, OPEN_ID_GALLERY_CODE = 1, OPEN_QUALIFICATION_GALLERY_CODE = 2;
    private boolean isDpChanged = false, isIDChanged = false, isQualificationChanged = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_profile);

        activity = this;
        prefs = getSharedPreferences("PM_M", MODE_PRIVATE);
        toolbar = findViewById(R.id.edit_profile_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        fname = findViewById(R.id.edit_client_fname);
        lname = findViewById(R.id.edit_client_lname);
        phone = findViewById(R.id.edit_client_phone);
        userDp = findViewById(R.id.edit_user_dp);
        userID = findViewById(R.id.edit_user_id);
        userQualification = findViewById(R.id.edit_user_qualification);
        updateButt = findViewById(R.id.finish_edit_button);

        fname.setText(prefs.getString("lname", ""));
        lname.setText(prefs.getString("fname", ""));
        phone.setText(prefs.getString("phone", ""));
        try {
            Picasso.with(this).load(prefs.getString("imagePath", "").replace(Constants.WRONG_PART, Constants.CORRECT_PART)).placeholder(R.drawable.user_icon).into(userDp);
            Picasso.with(this).load(prefs.getString("idImagePath", "").replace(Constants.WRONG_PART, Constants.CORRECT_PART)).placeholder(R.drawable.id_icon).into(userID);
            Picasso.with(this).load(prefs.getString("qualificationImagePath", "").replace(Constants.WRONG_PART, Constants.CORRECT_PART)).placeholder(R.drawable.qualification_icon).into(userQualification);
        }catch (Exception e){
            Toast.makeText(activity, "Failed to load the pictures", Toast.LENGTH_SHORT).show();
        }

        updateButt.setOnClickListener(updateIt);
        userDp.setOnClickListener(openDPGallery);
        userID.setOnClickListener(openIDGallery);
        userQualification.setOnClickListener(openQualificationGallery);
    }

    View.OnClickListener updateIt = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (!fname.getText().toString().equalsIgnoreCase("") &&
                    !lname.getText().toString().equalsIgnoreCase("") &&
                    !phone.getText().toString().equalsIgnoreCase(""))
            {
                new UpdateUserProfile().execute(fname.getText().toString(),
                        lname.getText().toString(),
                        phone.getText().toString(),
                        getShopIconString("dp"),
                        getImageName("dp"),
                        getShopIconString("id"),
                        getImageName("id"),
                        getShopIconString("qualification"),
                        getImageName("qualification"));
            }
            else
            {
                Toast.makeText(activity, "Fill in all the details", Toast.LENGTH_SHORT).show();
            }
        }
    };

    private View.OnClickListener openDPGallery = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            openGallery(OPEN_DP_GALLERY_CODE);
        }
    };


    private View.OnClickListener openIDGallery = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            openGallery(OPEN_ID_GALLERY_CODE);
        }
    };


    private View.OnClickListener openQualificationGallery = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            openGallery(OPEN_QUALIFICATION_GALLERY_CODE);
        }
    };

    void openGallery(int code){
        Intent openGalleryIntent = new Intent();
        openGalleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        openGalleryIntent.setType("image/*");
        startActivityForResult(Intent.createChooser(openGalleryIntent, "Select icon"), code);
    }

    private String getShopIconString(String type){

        BitmapDrawable bitmapDrawable;
        if (type.equalsIgnoreCase("dp")){
            bitmapDrawable = (BitmapDrawable) userDp.getDrawable();
        }
        else if (type.equalsIgnoreCase("id")){
            bitmapDrawable = (BitmapDrawable) userID.getDrawable();
        }
        else{
            bitmapDrawable = (BitmapDrawable) userQualification.getDrawable();
        }
        Bitmap shopIconBitmap = bitmapDrawable.getBitmap();

        ByteArrayOutputStream byteArrOutStream = new ByteArrayOutputStream();
        shopIconBitmap.compress(Bitmap.CompressFormat.JPEG, 70, byteArrOutStream);
        byte[] byteArr = byteArrOutStream.toByteArray();

        String iconString = Base64.encodeToString(byteArr, Base64.DEFAULT);

        return (iconString);
    }

    private String getImageName(String type){

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmSS").format(new Date());
        String imageName = "pm_user_" + type + "_" + fname.getText().toString() + "_" + timeStamp;

        if (isDpChanged && type.equalsIgnoreCase("dp")) {
            return (imageName);
        }
        if (isIDChanged && type.equalsIgnoreCase("id")) {
            return (imageName);
        }
        if (isQualificationChanged && type.equalsIgnoreCase("qualification")) {
            return (imageName);
        }
        return "";
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == OPEN_DP_GALLERY_CODE){

            if (resultCode == RESULT_OK){

                Uri imageUri = data.getData();
                userDp.setImageURI(imageUri);
                isDpChanged = true;
            }
        }
        else if (requestCode == OPEN_ID_GALLERY_CODE){

            if (resultCode == RESULT_OK){

                Uri imageUri = data.getData();
                userID.setImageURI(imageUri);
                isIDChanged = true;
            }
        }
        else if (requestCode == OPEN_QUALIFICATION_GALLERY_CODE){

            if (resultCode == RESULT_OK){

                Uri imageUri = data.getData();
                userQualification.setImageURI(imageUri);
                isQualificationChanged = true;
            }
        }
    }
}
