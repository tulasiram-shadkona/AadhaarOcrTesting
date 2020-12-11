package com.example.robolectrictestframework.geoloc;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.robolectrictestframework.MainActivity;
import com.example.robolectrictestframework.MainActivityContractor;
import com.example.robolectrictestframework.MainActivityPresenter;
import com.example.robolectrictestframework.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,ConnectionCallbacks, OnConnectionFailedListener, com.google.android.gms.location.LocationListener  {

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private Location mLocation;
    double lat, lng;
    private LocationRequest mLocationRequest;

    SupportMapFragment mapFragment;
    TextView latitudeLabel,longitudeLabel;
    Button backButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        latitudeLabel = (TextView)findViewById(R.id.latitude);
        longitudeLabel = (TextView)findViewById(R.id.longitude);
        backButton = findViewById(R.id.backButton);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
         mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
//        mapFragment.getMapAsync(this);
        // Check runtime Permission //
        int Permission_All = 1;
        String[] Permissions = {android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION};
        if(!hasPermissions(this, Permissions)){
            ActivityCompat.requestPermissions(this, Permissions, Permission_All);
        }
        createLocationRequest();

        if(mGoogleApiClient==null) {
            mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MapsActivity.this,MainActivity.class);
                i.putExtra("LAT",latitudeLabel.getText().toString());
                i.putExtra("LNG",longitudeLabel.getText().toString());
                startActivity(i);
//                MainActivityPresenter presenter = new MainActivityPresenter( MapsActivity.this);
//                presenter.getGeoCooordinates(latitudeLabel.getText().toString(), longitudeLabel.getText().toString());
                finish();

            }
        });
    }

    protected  void createLocationRequest(){

        if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION)== PackageManager.PERMISSION_GRANTED ) {

            mLocationRequest = new LocationRequest();
//            mLocationRequest.setInterval(20000L);
//            mLocationRequest.setFastestInterval(10000L);
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        }

    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(lat, lng);
        String address = getAddress(this,lat,lng);
        latitudeLabel.setText("Latitude : "+lat);
        longitudeLabel.setText("Longitude : "+lng);
        mMap.addMarker(new MarkerOptions().position(sydney).title(address)
        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        mMap.animateCamera( CameraUpdateFactory.zoomTo( 14.0f ) );
//        mMap.setStreetView()
        mMap.setMapType(mMap.MAP_TYPE_HYBRID); // Here is where you set the map type

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener(){

            @Override
            public void onMapClick(LatLng latLng) {
                // Creating a marker
                MarkerOptions markerOptions = new MarkerOptions();

                // Setting the position for the marker
                markerOptions.position(latLng);
                String addressUpdate = getAddress(MapsActivity.this,latLng.latitude,latLng.longitude);
                // Setting the title for the marker.
                // This will be displayed on taping the marker
                markerOptions.title(addressUpdate);

                // Clears the previously touched position
                googleMap.clear();

                // Animating to the touched position
                googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));

                // Placing a marker on the touched position
                googleMap.addMarker(markerOptions);

                latitudeLabel.setText("Latitude : "+latLng.latitude);
                longitudeLabel.setText("Longitude : "+latLng.longitude);
            }
        });


    }

    // Get Address from latitude and longitude //
    public String getAddress(Context ctx, double lat, double lng){
        String fullAdd=null;
        try{
            Geocoder geocoder= new Geocoder(ctx, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(lat,lng,1);
            if(addresses.size()>0){
                Address address = addresses.get(0);
                fullAdd = address.getAddressLine(0);

                // if you want only city or pin code use following code //
           /* String Location = address.getLocality();
            String zip = address.getPostalCode();
            String Country = address.getCountryName(); */
            }


        }catch(IOException ex){
            ex.printStackTrace();
        }
        return fullAdd;
    }
    // must declare methods //

    public void onStart(){
        mGoogleApiClient.connect();
        super.onStart();
        if(mGoogleApiClient.isConnected()){
            startLocationUpdates();
        }
    }
    public void onStop(){
        mGoogleApiClient.disconnect();
        stopLocationUpdate();
        super.onStop();
    }
    public void onPause(){
        mGoogleApiClient.disconnect();
        stopLocationUpdate();
        super.onPause();

    }
    public void onResume(){
        mGoogleApiClient.connect();
        super.onResume();
        if(mGoogleApiClient.isConnected()){
            startLocationUpdates();
        }
    }

    // create method for location update //
    protected void startLocationUpdates(){
        if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED
                ||ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION)== PackageManager.PERMISSION_GRANTED ) {

            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    protected  void stopLocationUpdate(){
//        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    // Must Declare LocatonListener Methods //
    public void onLocationChanged(Location location){
        if(location!=null){
            lat = location.getLatitude();
            lng = location.getLongitude();
            mapFragment.getMapAsync(this);
        }
    }

    public void onConnectionSuspended(int arg0){

    }
    public void onStatusChange(String provider, int status, Bundle extras){

    }

    // Must Declare Callback Methods //
    public void onConnected(Bundle args0){

        if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED
                ||ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION)== PackageManager.PERMISSION_GRANTED ) {
            mLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if(mLocation!=null){
                lat=mLocation.getLatitude();
                lng = mLocation.getLatitude();
                mapFragment.getMapAsync(this);
            }
            if(mGoogleApiClient.isConnected()){
                startLocationUpdates();
            }
        }
    }

    public static boolean hasPermissions(Context context, String... permissions){

        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.M && context!=null && permissions!=null){
            for(String permission: permissions){
                if(ActivityCompat.checkSelfPermission(context, permission)!=PackageManager.PERMISSION_GRANTED){
                    return  false;
                }
            }
        }
        return true;
    }

    public void onConnectionFailed(ConnectionResult result){

    }


}