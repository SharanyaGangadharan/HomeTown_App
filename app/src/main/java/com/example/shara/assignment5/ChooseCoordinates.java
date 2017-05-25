package com.example.shara.assignment5;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

public class ChooseCoordinates extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener {

    private GoogleMap mMap;
    public Double lat, lng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_coordinates);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMapClickListener(this);
    }

    public void onMapClick(LatLng location) {
        lat = location.latitude;
        lng = location.longitude;
        Log.d("rew", "new Location " + lat + " longitude " + lng );
        send();
    }

    public void send()
    {
        Intent toPassBack = getIntent();
        toPassBack.putExtra("lat",lat);
        toPassBack.putExtra("lng",lng);
        setResult(1, toPassBack);
        finish();
    }
}
