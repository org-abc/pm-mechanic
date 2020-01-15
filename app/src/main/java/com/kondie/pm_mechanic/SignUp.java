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
    private ImageView driverDp;
    public static Activity activity;
    final int OPEN_GALLERY_CODE = 0;
    private boolean isDpSet = false;
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
        password = findViewById(R.id.client_pass);
        conf_pass = findViewById(R.id.client_conf_pass);
        TextView signUpButton = findViewById(R.id.sign_up_button);

        signUpButton.setOnClickListener(signItUp);
        driverDp.setOnClickListener(openGalley);
    }

    private View.OnClickListener openGalley = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent openGalleryIntent = new Intent();
            openGalleryIntent.setAction(Intent.ACTION_GET_CONTENT);
            openGalleryIntent.setType("image/*");
            startActivityForResult(Intent.createChooser(openGalleryIntent, "Select icon"), OPEN_GALLERY_CODE);
        }
    };

    View.OnClickListener signItUp = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (!fname.getText().toString().equalsIgnoreCase("") &&
                    !lname.getText().toString().equalsIgnoreCase("") &&
                    !phone.getText().toString().equalsIgnoreCase("") &&
                    !password.getText().toString().equalsIgnoreCase("") &&
                    isDpSet)
            {
                if (password.getText().toString().equalsIgnoreCase(conf_pass.getText().toString()))
                {
                    new SubmitSignUpForm().execute(fname.getText().toString(),
                            lname.getText().toString(),
                            email.getText().toString(),
                            phone.getText().toString(),
                            password.getText().toString(),
                            minServiceFee.getText().toString(),
                            getShopIconString(),
                            getImageName());
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

    private String getImageName(){

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmSS").format(new Date());
        String imageName = "PM_mechanic_" + fname.getText().toString() + "_" + timeStamp;

        return (imageName);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == OPEN_GALLERY_CODE){

            if (resultCode == RESULT_OK){

                Uri shopIconUri = data.getData();
                driverDp.setImageURI(shopIconUri);
                isDpSet = true;
            }
        }
    }
}
