package com.example.shara.assignment5;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class CurrentUserLocMap extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    final ArrayList<String> userList = new ArrayList<String>();
    Double lat,lng;
    public String nickname,city;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_user_loc_map);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map1);
        mapFragment.getMapAsync(this);

        Bundle info = getIntent().getExtras();
        nickname = info.getString("nickname");
        lat = info.getDouble("lat");
        lng = info.getDouble("lng");
        city = info.getString("city");
        userList.add(nickname);
        userList.add(city);
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng loc = new LatLng(lat,lng);
        mMap.addMarker(new MarkerOptions().position(loc).title(nickname).snippet(city));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(loc));
    }

    @Override
    public void onBackPressed() {
        Intent go = new Intent(this,LoginActivity.class);
        startActivity(go);
    }
}
