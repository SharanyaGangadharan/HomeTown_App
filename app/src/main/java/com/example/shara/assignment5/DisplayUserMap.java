package com.example.shara.assignment5;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.example.shara.assignment5.models.User;
import com.example.shara.assignment5.utils.Constants;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static android.widget.Toast.LENGTH_LONG;

public class DisplayUserMap extends Fragment implements OnMapReadyCallback{

    private Button mFilter, mClear, mMore, mLess;
    private GoogleMap map;
    MapView mapview;

    int page=0, beforeid=0;
    String filter=null,pageString = "?page=",revString="&reverse=true";
    boolean done;
    private String url = "http://bismarck.sdsu.edu/hometown/users";
    private List<User> userList = new ArrayList<User>();
    DatabaseHelper helper;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

            View v = inflater.inflate(R.layout.activity_display_user_map, container, false);

            mFilter = (Button) v.findViewById(R.id.filter);
            mClear = (Button) v.findViewById(R.id.clear);
            mMore = (Button) v.findViewById(R.id.more);
            mLess = (Button) v.findViewById(R.id.less);
            mFilter.setEnabled(true);
            mClear.setEnabled(true);
            mMore.setEnabled(true);
            mLess.setEnabled(true);

            mapview = (MapView) v.findViewById(R.id.map1);
            mapview.onCreate(savedInstanceState);
            mapview.onResume();
            mapview.getMapAsync(this);

            helper = new DatabaseHelper(getActivity());

        mFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                VolleyQueue.instance(getContext()).queue().cancelAll("Good to go");
                userList.clear();
                UserMapFilter frag = new UserMapFilter();
                FragmentManager manager = getFragmentManager();
                frag.show(manager, "dialog");
            }
        });

        mClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filter=null;
                page=0;
                display(url+pageString+page+revString);
            }
        });

            mMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(page>=0 && userList.size()!=0){
                    page++;
                    display(url+pageString+page+"&"+filter+revString);}
                }
            });
            mLess.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(page>0){
                    page--;
                    display(url+pageString+page+"&"+filter+revString);}
                }
            });

        return v;
    }
    public void setFilter(String filter)
    {
        this.filter = filter;
        done = false;
        page=0;
        display(url+pageString+page+"&"+filter+revString);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        done = false;
        display(url+pageString+page+"&"+filter+revString);
        if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        map.setMyLocationEnabled(true);
    }

    public void display(String url){
        userList.clear();
        final List<User> usersList = new ArrayList<>();
        JsonArrayRequest req = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.i("response", response.length() + "");
                        if(response.length() == 0) {
                            done = true;
                        }
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject obj = response.getJSONObject(i);
                                User user = new User(obj.getString("id"),
                                        obj.getString("nickname"),
                                        obj.getString("country"),
                                        obj.getString("state"),
                                        obj.getString("city"),
                                        obj.getString("year"),
                                        obj.getString("latitude"),
                                        obj.getString("longitude"));
                                beforeid=Integer.parseInt(obj.getString("id"));
                                usersList.add(user);
                            } catch (Exception e) {
                            }
                        }
                        helper.insertUsers(usersList);
                        userList = helper.displayUsers(filter,beforeid);
                        mapInfo();
                        Log.i("DONE", done + "");
                        if(!done);
                           // checkPage();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("Some", "Error: " + error.getMessage());
            }
        });
        VolleyQueue.instance(getContext()).add(req);
    }

    public void mapInfo(){
        Double latitude=null, longitude=null;
        LatLng  loc = null;
        map.clear();
        for(final User user: userList){
            if(user.getLatitude().equals(null) && user.getLongitude().equals(null)) {
                Geocoder locator = new Geocoder(getContext());
                try {
                    List<Address> Locations = locator.getFromLocationName(user.getCity() + ", " + user.getState() + ", " + user.getCountry(), 3);
                    if (Locations.size() > 0) {
                        Address address = Locations.get(0);
                        if (address.hasLatitude())
                            latitude = address.getLatitude();
                        if (address.hasLongitude())
                            longitude = address.getLongitude();
                        loc = new LatLng(latitude, longitude);
                    }
                } catch (Exception error) {
                    Log.e("rew", "Address lookup Error", error);
                }
            }
            else
                loc = new LatLng(Double.parseDouble(user.getLatitude()), Double.parseDouble(user.getLongitude()));

            if(loc != null) {
                final MarkerOptions marker = new MarkerOptions().position(loc);
                map.addMarker(marker.title(user.getNickname()));

                map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                    @Override
                    public void onInfoWindowClick(Marker marker) {
                        callChat(marker.getTitle());
                    }
                });
            }
        }
        mFilter.setEnabled(true);
        mClear.setEnabled(true);
    }

    public void callChat(String nickname)
    {
        FirebaseDatabase.getInstance().getReference().child(Constants.ARG_USERS).child(nickname).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue()!=null) {
                    User user = dataSnapshot.getValue(User.class);
                    if (!TextUtils.equals(user.uid, FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                        goToChatActivity(user);
                    }
                }
                else{
                    Toast.makeText(getActivity(),"User does not exist in Firebase", LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getActivity(),"FAILED!",LENGTH_LONG).show();
            }
        });
    }

    public void goToChatActivity(User user){
        Intent intent = new Intent(getActivity(), ChatActivity.class);
        intent.putExtra(Constants.ARG_RECEIVER, user.nickname);
        intent.putExtra(Constants.ARG_RECEIVER_UID, user.uid);
        startActivity(intent);
    }

}
