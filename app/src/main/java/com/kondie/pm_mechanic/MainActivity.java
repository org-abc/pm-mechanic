package com.kondie.pm_mechanic;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MainActivity extends AppCompatActivity
        implements OnMapReadyCallback,
        GoogleApiClient.OnConnectionFailedListener,
        GoogleMap.OnMarkerClickListener,
        ActivityCompat.OnRequestPermissionsResultCallback,
        PermissionUtils.PermissionResultCallback,
        NavigationView.OnNavigationItemSelectedListener{

    public static Location userLocation;
    public static Activity activity;
    private final int REQUEST_CHECK_SETTINGS = 1;
    private final int SET_LOCATION_MANUALLY = 2;
    private Dialog setLocationDialog;
    private GoogleApiClient gApiClient;
    static SharedPreferences prefs;
    SharedPreferences.Editor editor;
    public static String clientName;
    public static Float clientLat, clientLng;
    public static LatLng dropOffLoc, driverLatLng;
    static Marker clientMarker, driverMarker;
    private final int EQUATOR_LENGTH = 40075000;
    public static GoogleMap navGoogleMap;
    public static LinearLayout orderListLay, showLessOrMoreButt;
    public static CoolLoading coolLoading;
    public static ImageView showIcon;
    public static RecyclerView orderList;
    public static RequestItemAdapter requestItemAdapter;
    private LinearLayoutManager linearLayMan;
    public static List<RequestItem> requestItems;
    private PermissionUtils permissionUtils;
    public static TextView duratuinTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_ACTION_BAR);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        activity = this;
        prefs = getSharedPreferences("PM_M", Context.MODE_PRIVATE);
        editor = prefs.edit();

        try {
            Toolbar toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            DrawerLayout drawer = findViewById(R.id.drawer_layout);
            NavigationView navigationView = findViewById(R.id.nav_view);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawer.addDrawerListener(toggle);
            toggle.syncState();
            navigationView.setNavigationItemSelectedListener(this);

            orderListLay = findViewById(R.id.order_list_lay);
            showLessOrMoreButt = findViewById(R.id.show_less_or_more_butt);
            orderList = findViewById(R.id.order_list);
            showIcon = findViewById(R.id.show_icon);
            duratuinTxt = findViewById(R.id.duration_txt);
            SupportMapFragment mapFrag = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.nav_map_frag);
            mapFrag.getMapAsync(MainActivity.this);

            coolLoading = new CoolLoading(activity);
            showLessOrMoreButt.setOnClickListener(showLessOrMore);
            setGApiClient();
            createLocationRequest();

            if (prefs.getString("fname", "").equals("")) {
                new GetUserInfo().execute();
            } else {
                setUserDrawerInfo((NavigationView) findViewById(R.id.nav_view));
            }
        }catch (Exception e){
            Toast.makeText(activity, e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    private void checkPermissions(){

        ArrayList<String> permissions=new ArrayList<>();
        permissionUtils=new PermissionUtils(activity,this);

        permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);

        permissionUtils.check_permission(permissions, "We need your GPS for delivery", REQUEST_CHECK_SETTINGS);
    }

    protected void createLocationRequest(){
        LocationRequest locationReq = new LocationRequest();
        locationReq.setInterval(10000);
        locationReq.setFastestInterval(5000);
        locationReq.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationReq);
        Task<LocationSettingsResponse> task = LocationServices.getSettingsClient(activity).checkLocationSettings(builder.build());

        task.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
            @Override
            public void onComplete(@NonNull Task<LocationSettingsResponse> task) {
                try{
                    LocationSettingsResponse response = task.getResult(ApiException.class);
                    checkPermissions();
                } catch (ApiException e) {
                    switch (e.getStatusCode()) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            try {
                                ResolvableApiException resolvable = (ResolvableApiException) e;
                                resolvable.startResolutionForResult(activity, REQUEST_CHECK_SETTINGS);
                            } catch (IntentSender.SendIntentException ex) {
                                // Ignore the error.
                            } catch (ClassCastException ex) {
                                // Ignore, should be an impossible error.
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            // Location settings are not satisfied. However, we have no way to fix the
                            break;
                    }
                }
            }
        });
    }

    private void setUpOrderList(){

        requestItems = new ArrayList<>();
        linearLayMan = new LinearLayoutManager(activity);
        linearLayMan.setOrientation(RecyclerView.VERTICAL);
        orderList.setLayoutManager(linearLayMan);
        requestItemAdapter = new RequestItemAdapter(activity, requestItems, orderList);
        orderList.setAdapter(requestItemAdapter);

        new GetRequests().execute("5050-00-00 00:00:00");
    }

    private View.OnClickListener showLessOrMore = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            showLessOrMoreFunc();
        }
    };

    public static void showLessOrMoreFunc(){
        if (orderList.getVisibility() == View.VISIBLE){
            orderListLay.startAnimation(coolLoading.getShowLessAnim());
        }
        else{
            orderListLay.startAnimation(coolLoading.getShowMoreAnim());
            new GetRequests().execute("5050-00-00 00:00:00");
        }
    }

    public void setGApiClient(){
        try {
            gApiClient = new GoogleApiClient
                    .Builder(this)
                    .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                        @Override
                        public void onConnected(@Nullable Bundle bundle) {
                            setLocation();
                        }

                        @Override
                        public void onConnectionSuspended(int i) {

                        }
                    })
                    .addOnConnectionFailedListener(this)
                    .addApi(Places.GEO_DATA_API)
                    .addApi(Places.PLACE_DETECTION_API)
                    .addApi(LocationServices.API)
                    .enableAutoManage(this, this)
                    .build();
        } catch (Exception e) {
            Toast.makeText(activity, e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    public static void updateLoc(){

        dropOffLoc = new LatLng(clientLat, clientLng);
        clientMarker.setPosition(dropOffLoc);
        driverMarker.setPosition(new LatLng(userLocation.getLatitude(), userLocation.getLongitude()));
    }

    void setLocation(){
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        FusedLocationProviderClient mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    userLocation = location;
                    if (requestItems == null) {
                        setUpOrderList();
                    }
                }
            }
        });
    }

    private double getScreenWidth(){
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        return (displayMetrics.widthPixels);
    }

    private double getZoomForMiters(double meters, double lat){

        final double latAdjustment = Math.cos(Math.PI * lat / 180);
        final double arg = EQUATOR_LENGTH * getScreenWidth() * latAdjustment / (meters * 256);

        return (Math.log(arg) / Math.log(2));
    }

    private void setMapType() {

        switch (prefs.getString("mapType", "")) {

            case "normal":
                navGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                break;
            case "terrain":
                navGoogleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                break;
            case "hybrid":
                navGoogleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                break;
            case "satellite":
                navGoogleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                break;
            default:
                navGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        try {
            navGoogleMap = googleMap;
            setMapType();
            setUpMap();

            navGoogleMap.setMyLocationEnabled(true);
        }catch (Exception e){
            Toast.makeText(activity, e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    private void setUpMap(){
        if (userLocation != null){
            driverLatLng = new LatLng(userLocation.getLatitude(), userLocation.getLongitude());
            navGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(driverLatLng, 17));
        }
        else{
            setLocation();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    setUpMap();
                }
            }, 5000);
        }
    }

    private void setLocationManuallyFunc(){
        try {
            Intent manualIntent = new PlacePicker.IntentBuilder().build(activity);
            startActivityForResult(manualIntent, SET_LOCATION_MANUALLY);
        }catch (GooglePlayServicesRepairableException e){
            //Toast.makeText(MainActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
        }catch (GooglePlayServicesNotAvailableException e){
            //Toast.makeText(MainActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    private View.OnClickListener setLocationManually = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            setLocationManuallyFunc();
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CHECK_SETTINGS){
            if (resultCode == RESULT_OK){
                checkPermissions();
            }else if (resultCode == RESULT_CANCELED){
                new AlertDialog.Builder(activity).setCancelable(false).setTitle("Note")
                        .setMessage("You can't use this app without switching on your location.")
                        .setPositiveButton("Switch on location", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                createLocationRequest();
                            }
                        }).show();
            }
        }else if (requestCode == SET_LOCATION_MANUALLY){
            if (resultCode == RESULT_OK){
                try {
                    Place currentPlace = PlacePicker.getPlace(activity, data);
                    LatLng latLng = currentPlace.getLatLng();
                    editor.putFloat("dropOffLat", (float)latLng.latitude);
                    editor.putFloat("dropOffLng", (float)latLng.longitude);
                    editor.commit();
                    dropOffLoc = latLng;
//                    userLocation = new Location("");
//                    userLocation.setLatitude(latLng.latitude);
//                    userLocation.setLongitude(latLng.longitude);
                    if (setLocationDialog != null && setLocationDialog.isShowing()) {
                        setLocationDialog.dismiss();
                    }
//                    else if (menuDialog != null && menuDialog.isShowing()){
//                        menuDialog.dismiss();
//                    }
//                    setUpShopList();
                }catch (Exception e){
                    Toast.makeText(activity, "Something went wrong", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        new GetRequests().execute("5050-00-00 00:00:00");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

    @Override
    public void PermissionGranted(int request_code) {

    }

    @Override
    public void PartialPermissionGranted(int request_code, ArrayList<String> granted_permissions) {

    }

    @Override
    public void PermissionDenied(int request_code) {
        new AlertDialog.Builder(activity).setCancelable(false).setTitle("Note")
                .setMessage("You can't use this app without giving it access to you location.")
                .setPositiveButton("Give permission", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        checkPermissions();
                    }
                }).show();
    }

    @Override
    public void NeverAskAgain(int request_code) {
        new AlertDialog.Builder(activity).setCancelable(false).setTitle("Note")
                .setMessage("You can't use this app without giving it access to you location.")
                .setPositiveButton("Close app", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        activity.finish();
                    }
                }).show();
    }

    public static void setUserDrawerInfo(NavigationView navigationView){
        try {
            View headerView = navigationView.getHeaderView(0);
            TextView fullName = headerView.findViewById(R.id.menu_full_name);
            TextView viewProfile = headerView.findViewById(R.id.view_profile);
            ImageView menuDp = headerView.findViewById(R.id.menu_dp);

            fullName.setText(prefs.getString("lname", "") + " " + prefs.getString("fname", ""));
            Picasso.with(activity).load(prefs.getString("imagePath", "")).into(menuDp);

            viewProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent toProfileIntent = new Intent(activity, UpdateProfile.class);
                    activity.startActivity(toProfileIntent);
                }
            });
        }catch (Exception e){
            Toast.makeText(activity, e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int id = menuItem.getItemId();

        switch (id){
            case R.id.nav_history:
                Intent showHistoryIntent = new Intent(MainActivity.this, HistoryAct.class);
                startActivity(showHistoryIntent);
                break;
            case R.id.nav_help:
                Intent showHelpIntent = new Intent(MainActivity.this, HelpAct.class);
                startActivity(showHelpIntent);
                break;
            case R.id.nav_about:
                Intent showAboutIntent = new Intent(MainActivity.this, AboutAct.class);
                startActivity(showAboutIntent);
                break;
            case R.id.nav_settings:
                Intent showSettingsIntent = new Intent(MainActivity.activity, SettingsAct.class);
                startActivity(showSettingsIntent);
                break;
            case R.id.nav_logout:
                editor.clear();
                editor.commit();

                Intent toLoginIntent = new Intent(activity, SignIn.class);
                activity.startActivity(toLoginIntent);
                activity.finish();
                break;
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
