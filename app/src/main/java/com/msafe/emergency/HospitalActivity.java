package com.msafe.emergency;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.msafe.emergency.R;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;

public class HospitalActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener {

    private GoogleMap mMap;
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private Location lastLocation;
    private LatLng CustomerPickUpLocation;
    private  int radius=1;
    private Boolean driverfound=false;
    private String driverfoundID;
    Marker DriverMarker;
    private static final int Request_User_Location_Code=99;

    private Button btncallambulance,btncancelambulance;
//    private EditText etsearch;
//    private ImageButton searchimg;

    DatabaseReference driveravailableref;
    DatabaseReference driverworkingref;

    private boolean cancel=false;

    GeoQuery geoQuery;
    private ValueEventListener driverLocRefValueEventListener;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private DatabaseReference CustomerDatabaseRef;
    private DatabaseReference DriverAvailableRef;
    private DatabaseReference driverref;
    private DatabaseReference driverlocref;

    double LocationLat= 0;
    double LocationLong= 0;

    String customerId;
    String category,condition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hospital);


        Bundle b = getIntent().getExtras();
        if(b!=null)
        {
            category =(String) b.get("Category");
            condition =(String) b.get("Condition");

        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        GPSLocationPermission();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkUserLocationPermission();
        }

        btncallambulance=findViewById(R.id.btncallambulance);
        btncancelambulance=findViewById(R.id.btncancelambulance);

        btncancelambulance.setVisibility(View.INVISIBLE);
        btncancelambulance.setEnabled(false);

        mAuth=FirebaseAuth.getInstance();
        currentUser=mAuth.getCurrentUser();
       //   customerId=currentUser.getUid();

        CustomerDatabaseRef= FirebaseDatabase.getInstance().getReference().child("CUSTOMERS REQUESTS");
        DriverAvailableRef= FirebaseDatabase.getInstance().getReference().child("DRIVERS AVAILABLE");

        btncallambulance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // GeoFire geoFire = new GeoFire(CustomerDatabaseRef);
               //  System.out.println(geoFire.hashCode());
               //   geoFire.setLocation(customerId, new GeoLocation(lastLocation.getLatitude(),lastLocation.getLongitude()));

                CustomerPickUpLocation=new LatLng(lastLocation.getLatitude(),lastLocation.getLongitude());
                mMap.addMarker(new MarkerOptions().position(CustomerPickUpLocation).title("Pick Up Here"));

                btncallambulance.setText("Getting Your Ambulance");
                getclosestambulance();
            }
        });

        btncancelambulance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CancelAmbulance();
            }
        });

    }



    private void CancelAmbulance() {
        geoQuery.removeAllListeners();
        driverlocref.removeEventListener(driverLocRefValueEventListener);

        String userid=FirebaseAuth.getInstance().getCurrentUser().getUid();

        DatabaseReference ref= FirebaseDatabase.getInstance().getReference().child("CUSTOMERS REQUESTS");
        GeoFire geofire=new GeoFire(ref);
        geofire.removeLocation(userid);

        DatabaseReference dref=FirebaseDatabase.getInstance().getReference().child("DRIVER-CUSTOMER").child(driverfoundID);
        dref.removeValue();

        Intent intent= new Intent(this, HospitalActivity.class);
        startActivity(intent);
        finish();
    }

    private void getclosestambulance() {

        GeoFire geoFire=new GeoFire(DriverAvailableRef);

        geoQuery=geoFire.queryAtLocation(new GeoLocation(CustomerPickUpLocation.latitude, CustomerPickUpLocation.longitude), radius);
        geoQuery.removeAllListeners();
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                if (!driverfound) {
                    driverfound = true;
                    driverfoundID = key;

                    driverref = FirebaseDatabase.getInstance().getReference().child("DRIVER-CUSTOMER").child(driverfoundID);

                    HashMap drivermap = new HashMap();
                    drivermap.put("CustomerRideID", customerId);
                    drivermap.put("Category", category);
                    drivermap.put("Condition", condition);
                    driverref.updateChildren(drivermap);

                    btncallambulance.setText("Looking for Driver Location...");

                    DatabaseReference driveravailableref = FirebaseDatabase.getInstance().getReference().child("DRIVERS AVAILABLE");
                    DatabaseReference driverworkingref = FirebaseDatabase.getInstance().getReference().child("DRIVERS WORKING");

                    moveFirebaseRecord(driveravailableref.child(driverfoundID), driverworkingref.child(driverfoundID));

                    driveravailableref.child(driverfoundID).removeValue();
                    GetDriverLocation();
                }

            }

            @Override
            public void onKeyExited(String key) {

            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {

            }

            @Override
            public void onGeoQueryReady() {
                if(!driverfound)
                {
                    radius=radius + 1;
                    getclosestambulance();
                }
            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });
    }


    public void moveFirebaseRecord(DatabaseReference fromPath, final DatabaseReference toPath) {
        fromPath.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                toPath.setValue(dataSnapshot.getValue(), new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        if (databaseError != null) {
                            Log.i("AVAILABLE TO WORKING :", "COPY FAILED");
                        } else {
                            Log.i("AVAILABLE TO WORKING :", "COPY SUCCESS");

                        }
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.i("A to W (onCancelled) :", "COPY FAILED");

            }
        });
    }


    private void GetDriverLocation() {
        driverlocref=FirebaseDatabase.getInstance().getReference().child("DRIVERS WORKING").child(driverfoundID).child("l");
        driverLocRefValueEventListener = driverlocref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    List<Object> driverLocationMap=(List<Object>) dataSnapshot.getValue();
                    LocationLat= 0;
                    LocationLong= 0;
                    btncallambulance.setText("Ambulance Found");
                    if(driverLocationMap.get(0) != null){
                        LocationLat=Double.parseDouble(driverLocationMap.get(0).toString());

                    }
                    if(driverLocationMap.get(1) != null){
                        LocationLong=Double.parseDouble(driverLocationMap.get(1).toString());

                    }
                    LatLng driverLatLng = new LatLng(LocationLat, LocationLong);

                    if(DriverMarker != null){
                        DriverMarker.remove();
                    }
                    Location location1=new Location("");
                    location1.setLatitude(CustomerPickUpLocation.latitude);
                    location1.setLongitude(CustomerPickUpLocation.longitude);


                    Location location2=new Location("");
                    location2.setLatitude(driverLatLng.latitude);
                    location2.setLongitude(driverLatLng.longitude);
                    float distance=location1.distanceTo(location2);

                    if(distance < 90)
                        btncallambulance.setText("Ambulane Reached");
                    else
                        btncallambulance.setText("Ambulane Found:"+String.valueOf(distance));

                    btncancelambulance.setVisibility(View.VISIBLE);
                    btncancelambulance.setEnabled(true);

                    DriverMarker = mMap.addMarker(new MarkerOptions().position(driverLatLng).title("Your Driver is here"));
                    DriverMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ambulanceicon));


                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(DriverMarker.getPosition(),14));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED)
        {
            buildGoogleApiCLient();
            mMap.setMyLocationEnabled(true);
        }
    }
    protected synchronized void buildGoogleApiCLient()
    {
        googleApiClient=new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        googleApiClient.connect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        locationRequest=new LocationRequest();
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        if (ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED)
        {
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient,locationRequest, this);
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {lastLocation=location;

        LatLng latLng=new LatLng(location.getLatitude(),location.getLongitude());

        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomBy(14f));
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(getCameraPositionWithBearing(latLng)));

        if (googleApiClient!=null)
        {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, (com.google.android.gms.location.LocationListener) this);
        }

    }

    @NonNull
    private CameraPosition getCameraPositionWithBearing(LatLng latLng) {
        return new CameraPosition.Builder().target(latLng).zoom(15).build();
    }






    // RunTime permission for Google Location Services
    private void GPSLocationPermission()
    {
        LocationRequest mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(1000)
                .setFastestInterval(1000);

        LocationSettingsRequest.Builder settingsBuilder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);
        settingsBuilder.setAlwaysShow(true);

        Task<LocationSettingsResponse> result = LocationServices.getSettingsClient(this)
                .checkLocationSettings(settingsBuilder.build());

        result.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
            @Override
            public void onComplete(@NonNull Task<LocationSettingsResponse> task) {
                try {
                    LocationSettingsResponse response =
                            task.getResult(ApiException.class);
                } catch (ApiException ex) {
                    switch (ex.getStatusCode()) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            try {
                                ResolvableApiException resolvableApiException =
                                        (ResolvableApiException) ex;
                                resolvableApiException
                                        .startResolutionForResult(HospitalActivity.this,Request_User_Location_Code);
                            } catch (IntentSender.SendIntentException e) {

                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:

                            break;
                    }
                }
            }
        });

    }

    public boolean checkUserLocationPermission()
    {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?

            if (ActivityCompat.shouldShowRequestPermissionRationale(HospitalActivity.this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                new AlertDialog.Builder(this)
                        .setTitle("Required Location Permission").setMessage("You have to give this permission to access this feature")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(HospitalActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        Request_User_Location_Code);
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .create()
                        .show();
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(HospitalActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        Request_User_Location_Code);
            }
            return false;
        }
        else
        {
            return true;
        }
    }
}
