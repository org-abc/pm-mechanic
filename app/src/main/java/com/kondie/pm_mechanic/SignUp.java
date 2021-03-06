package com.kondie.pm_mechanic;

import android.app.Activity;
import android.content.Intent;
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

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

/**
 * Created by kondie on 2019/01/14.
 */

public class SignUp extends AppCompatActivity {

    private EditText fname, lname, email, phone, password, conf_pass, minServiceFee;
    private ImageView driverDp, mechanicIDPic, mechanicQualificationPic;
    public static Activity activity;
    final int OPEN_GALLERY_FOR_DP_CODE = 0, OPEN_GALLERY_FOR_ID_CODE=1, OPEN_GALLERY_FOR_QUALIFICATION_CODE=2;
    private boolean isDpSet = false, isIdSet = false, isQualificationSet = false;
    private Toolbar toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up);
        activity = SignUp.this;

        toolbar = findViewById(R.id.signup_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        fname = findViewById(R.id.client_fname);
        lname = findViewById(R.id.client_lname);
        email = findViewById(R.id.client_email);
        phone = findViewById(R.id.client_phone);
        minServiceFee = findViewById(R.id.min_service_fee);
        driverDp = findViewById(R.id.add_mechanic_dp);
        mechanicIDPic = findViewById(R.id.add_mechanic_id_dp);
        mechanicQualificationPic = findViewById(R.id.add_mechanic_qualification_pic);
        password = findViewById(R.id.client_pass);
        conf_pass = findViewById(R.id.client_conf_pass);
        TextView signUpButton = findViewById(R.id.sign_up_button);

        signUpButton.setOnClickListener(signItUp);
        driverDp.setOnClickListener(openGalley);
        mechanicIDPic.setOnClickListener(openGalleyForIdPic);
        mechanicQualificationPic.setOnClickListener(openGalleyForQualificationPic);
    }

    private View.OnClickListener openGalley = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            openGallery(OPEN_GALLERY_FOR_DP_CODE);
        }
    };

    private View.OnClickListener openGalleyForIdPic = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            openGallery(OPEN_GALLERY_FOR_ID_CODE);
        }
    };

    private View.OnClickListener openGalleyForQualificationPic = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            openGallery(OPEN_GALLERY_FOR_QUALIFICATION_CODE);
        }
    };

    private void openGallery(final int code){
        Intent openGalleryIntent = new Intent();
        openGalleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        openGalleryIntent.setType("image/*");
        startActivityForResult(Intent.createChooser(openGalleryIntent, "Select icon"), code);
    }

    View.OnClickListener signItUp = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (!fname.getText().toString().equalsIgnoreCase("") &&
                    !lname.getText().toString().equalsIgnoreCase("") &&
                    !phone.getText().toString().equalsIgnoreCase("") &&
                    !password.getText().toString().equalsIgnoreCase("") &&
                    isDpSet &&
                    isIdSet)
            {
                if (password.getText().toString().equalsIgnoreCase(conf_pass.getText().toString()))
                {
                    if (isQualificationSet) {
                        new SubmitSignUpForm().execute(fname.getText().toString(),
                                lname.getText().toString(),
                                email.getText().toString(),
                                phone.getText().toString(),
                                password.getText().toString(),
                                minServiceFee.getText().toString(),
                                getShopIconString(),
                                getImageName("PM_mechanic_"),
                                getIDImageString(),
                                getImageName("PM_mechanic_id_"),
                                "yes",
                                getQualificationImageString(),
                                getImageName("PM_mechanic_qualification_"));
                    }
                    else{
                        new SubmitSignUpForm().execute(fname.getText().toString(),
                                lname.getText().toString(),
                                email.getText().toString(),
                                phone.getText().toString(),
                                password.getText().toString(),
                                minServiceFee.getText().toString(),
                                getShopIconString(),
                                getImageName("PM_mechanic_"),
                                getIDImageString(),
                                getImageName("PM_mechanic_id_"),
                                "no");
                    }
                }
                else {
                    Toast.makeText(activity, "Password doesn't match", Toast.LENGTH_SHORT).show();
                }
            }
            else
            {
                Toast.makeText(activity, "Fill in all the details", Toast.LENGTH_SHORT).show();
            }
        }
    };

    private String getShopIconString(){

        BitmapDrawable bitmapDrawable = (BitmapDrawable) driverDp.getDrawable();
        Bitmap shopIconBitmap = bitmapDrawable.getBitmap();

        ByteArrayOutputStream byteArrOutStream = new ByteArrayOutputStream();
        shopIconBitmap.compress(Bitmap.CompressFormat.JPEG, 70, byteArrOutStream);
        byte[] byteArr = byteArrOutStream.toByteArray();

        String iconString = Base64.encodeToString(byteArr, Base64.DEFAULT);

        return (iconString);
    }

    private String getIDImageString(){

        BitmapDrawable bitmapDrawable = (BitmapDrawable) mechanicIDPic.getDrawable();
        Bitmap shopIconBitmap = bitmapDrawable.getBitmap();

        ByteArrayOutputStream byteArrOutStream = new ByteArrayOutputStream();
        shopIconBitmap.compress(Bitmap.CompressFormat.JPEG, 70, byteArrOutStream);
        byte[] byteArr = byteArrOutStream.toByteArray();

        String iconString = Base64.encodeToString(byteArr, Base64.DEFAULT);

        return (iconString);
    }

    private String getQualificationImageString(){

        BitmapDrawable bitmapDrawable = (BitmapDrawable) mechanicQualificationPic.getDrawable();
        Bitmap shopIconBitmap = bitmapDrawable.getBitmap();

        ByteArrayOutputStream byteArrOutStream = new ByteArrayOutputStream();
        shopIconBitmap.compress(Bitmap.CompressFormat.JPEG, 70, byteArrOutStream);
        byte[] byteArr = byteArrOutStream.toByteArray();

        String iconString = Base64.encodeToString(byteArr, Base64.DEFAULT);

        return (iconString);
    }

    private String getImageName(String preStr){

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmSS").format(new Date());
        String imageName = preStr + fname.getText().toString() + "_" + timeStamp;

        return (imageName);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == OPEN_GALLERY_FOR_DP_CODE){

            if (resultCode == RESULT_OK){

                Uri shopIconUri = data.getData();
                driverDp.setImageURI(shopIconUri);
                isDpSet = true;
            }
        }
        else if (requestCode == OPEN_GALLERY_FOR_ID_CODE){

            if (resultCode == RESULT_OK){

                Uri shopIconUri = data.getData();
                mechanicIDPic.setImageURI(shopIconUri);
                isIdSet = true;
            }
        }
        else if (requestCode == OPEN_GALLERY_FOR_QUALIFICATION_CODE){

            if (resultCode == RESULT_OK){

                Uri shopIconUri = data.getData();
                mechanicQualificationPic.setImageURI(shopIconUri);
                isQualificationSet = true;
            }
        }
    }
}
