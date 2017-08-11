package com.example.anil.achievement.fragments;


import android.graphics.Point;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.BounceInterpolator;
import android.view.animation.Interpolator;
import android.widget.Toast;

import com.example.anil.achievement.R;
import com.example.anil.achievement.services.DataService;
import com.example.anil.achievement.models.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MainFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private GoogleMap mMap;
    private MarkerOptions userMarker;
    private Marker currentLocationMarker;

    public MainFragment() {
        // Required empty public constructor
    }

    public static MainFragment newInstance() {
        MainFragment fragment = new MainFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_main, container, false);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        //use child fragment manager because that calls the manager of this fragment not the activities manager
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Inflate the layout for this fragment
        return view;
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.getUiSettings().setCompassEnabled(true);
        mMap.setBuildingsEnabled(false);

        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        mMap.setOnMarkerClickListener( this );

        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    public void setUserMarker(LatLng latLng, float bearing)
    {
        //refresh and remove everything from map
        mMap.clear();

        userMarker = null;

        //if no marker has been placed before add one to the map
       if(userMarker == null)
        {
            //create marker from passed in location details

            userMarker = new MarkerOptions()
                    .position(latLng)
                    .title("Current Location")
            .icon(BitmapDescriptorFactory.fromResource(R.drawable.arrow));

            currentLocationMarker = mMap.addMarker(userMarker);
            Log.v("MAPS","Current Location - Lat: " + latLng.latitude + " Long: " + latLng.longitude);
        }

        currentLocationMarker.setPosition(latLng);
        //Toast.makeText(getContext(), "Updated position", Toast.LENGTH_LONG).show();

        //handle the tilt view
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(latLng)
                //.target(new LatLng(53, -2.23333))
                .zoom(19)
                .tilt(67.5f)
                .bearing(bearing)
                .build();

        //move camera to current user location
        //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));

        //can do geocoder here to get postcode/zip from long/lat
//        try
//        {
//            Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
//            List<Address> addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
//            String postCode = addresses.get(0).getPostalCode();
//            //call updateMapFromPlaces and pass the postcode to search using google api places search
//        }
//        catch(IOException ex)
//        {
//
//        }

        updateMapFromPlaces(1);
        //mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    private void updateMapFromPlaces(int location)
    {
        ArrayList<Places> locations = DataService.getInstance().getPlacesInLocation(location);

        for( int i = 0; i < locations.size(); i++ )
        {
            Places loc = locations.get(i);

            //creating markers to add to map
            MarkerOptions marker = new MarkerOptions().position( new LatLng( loc.getLatitude(), loc.getLongitude() ) );
            marker.title(loc.getLocationTitle());
            marker.snippet(loc.getLocationAddress());
            marker.icon( BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN) );
            //marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.pin)); //custom icon

            mMap.addMarker(marker);
        }
    }

    @Override
    public boolean onMarkerClick(final Marker marker) {

        //Make the marker bounce
        final Handler handler = new Handler();

        final long startTime = SystemClock.uptimeMillis();
        final long duration = 2000;

        Projection proj = mMap.getProjection();
        final LatLng markerLatLng = marker.getPosition();
        Point startPoint = proj.toScreenLocation(markerLatLng);
        startPoint.offset(0, -100);
        final LatLng startLatLng = proj.fromScreenLocation(startPoint);

        final Interpolator interpolator = new BounceInterpolator();

        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - startTime;
                float t = interpolator.getInterpolation((float) elapsed / duration);
                double lng = t * markerLatLng.longitude + (1 - t) * startLatLng.longitude;
                double lat = t * markerLatLng.latitude + (1 - t) * startLatLng.latitude;
                marker.setPosition(new LatLng(lat, lng));

                if (t < 1.0) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 16);
                }
            }
        });

        //return false; //have not consumed the event
        return true; //have consumed the event
    }

}
