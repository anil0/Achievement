package com.example.anil.achievement.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.example.anil.achievement.R;
import com.example.anil.achievement.fragments.MainFragment;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

public class MapsActivity extends FragmentActivity implements GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks, com.google.android.gms.location.LocationListener {

    //allows us to get user location and such
    private GoogleApiClient mGoogleApiClient;

    final int PERMISSION_LOCATION = 11;

    private MainFragment mainFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        //build api client
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this,this)//manage this client for us
                .addConnectionCallbacks(this)//calls the overrided methods we defined for us
                .addApi(LocationServices.API)//we need the location services to get user data and such
                .build();

        mainFragment = (MainFragment) getSupportFragmentManager().findFragmentById(R.id.container_main);

        if(mainFragment == null)
        {
            //assign an instance and swap in the assigned fragment
            mainFragment = mainFragment.newInstance();
            getSupportFragmentManager().beginTransaction().add(R.id.container_main, mainFragment).commit();
        }

    }

    public void startLocationServices()
    {
        Log.v("MAPS","Starting location services called");

        try
        {
            LocationRequest request = LocationRequest.create().setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, request, this);
        }
        catch (SecurityException ex)
        {
            //we shouldn't see this code run as we aleady handle it within the onRequestPermissionsResult method

            //show something to user to say we cant get location until permission given
            Log.v("MAPS",ex.toString());
        }
    }

    @Override
    protected void onStart()
    {
        //make sure we are connected when the application runs
        mGoogleApiClient.connect();
        super.onStart();
}

    @Override
    protected void onStop()
    {
        //disconnect so no background usage is occuring when we stop
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        //request code is our defined constant passed
        switch (requestCode)
        {
            case PERMISSION_LOCATION:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    //if the permissions are granted then start location services
                    startLocationServices();
                    Log.v("MAPS","Permission granted - starting services");
                }
                else
                {
                    //show something to say you denied permission, we need permission
                    Log.v("MAPS","Permission not granted");
                }
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult)
    {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle)
    {
        //google play services is now connected
        //only start the services if the user has given permission
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            //we need to request permissions because permissions are not granted yet
            //permission location is just an id defined to use to represent the permission request
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_LOCATION);
            Log.v("MAPS","Requesting Permissions");
        }
        else
        {
            Log.v("MAPS","starting location services from onConnected()");
            startLocationServices();
        }

    }

    @Override
    public void onConnectionSuspended(int i)
    {

    }

    @Override
    public void onLocationChanged(Location location)
    {
        Log.v("MAPS","Long: " + location.getLongitude() + "- Lat: " + location.getLatitude());
        Toast.makeText(getApplicationContext(), "Lat: " + location.getLatitude() + " - Long: " + location.getLongitude(), Toast.LENGTH_LONG).show();
        Log.v("LOCATION CHANGED", location.getLatitude() + "");
        Log.v("LOCATION CHANGED", location.getLongitude() + "");
        mainFragment.setUserMarker(new LatLng(location.getLatitude(), location.getLongitude()));
    }


}
