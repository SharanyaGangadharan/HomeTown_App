package com.example.shara.assignment5;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.example.shara.assignment5.models.User;
import com.example.shara.assignment5.utils.Constants;
import com.example.shara.assignment5.utils.ItemClickSupport;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static android.R.attr.fragment;
import static android.content.ContentValues.TAG;
import static android.widget.Toast.LENGTH_LONG;

public class UserListFragment extends Fragment implements ItemClickSupport.OnItemClickListener{

    private RecyclerView recyclerView;

    String filter=null;
    String selectedCountry = null;
    String selectedState, selectedYear;

    static int page =0;
    int beforeid =0;

    private List<User> userList = new ArrayList<>();

    ListAdapter mAdapter;
    DatabaseHelper helper;

    public UserListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v= inflater.inflate(R.layout.fragment_user_list, container, false);
        getActivity().setTitle("Users");
        recyclerView = (RecyclerView) v.findViewById(R.id.recycler_view);
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));

        helper = new DatabaseHelper(getActivity());

        recyclerView = (RecyclerView) v.findViewById(R.id.recycler_view);
        ItemClickSupport.addTo(recyclerView).setOnItemClickListener(this);

        getList();

        FilterFragment frag = new FilterFragment();
        FragmentManager manager = getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.fragment_holder1, frag, "Test Fragment");
        transaction.commit();

        return v;
    }

    public void applyFilter()
    {
        filterList();
        userList.clear();
        page=0;
        getList();
    }

    public void clearFilter()
    {
        userList.clear();
        FilterFragment frag = new FilterFragment();
        FragmentManager manager = getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.fragment_holder1, frag, "Test Fragment");
        transaction.commit();
        clear();
    }

    public String getCountry() {
        return selectedCountry;
    }

    public void setCountry(String country) {
        this.selectedCountry = country;
    }

    public String getState() {
        return selectedState;
    }

    public void setState(String state) {
        this.selectedState = state;
    }

    public String getYear() {
        return selectedYear;
    }

    public void setYear(String year) {
        this.selectedYear = year;
    }

    public void getList()
    {
        final List<User> usersList = new ArrayList<>();
        Response.Listener<JSONArray> success = new Response.Listener<JSONArray>() {
            public void onResponse(JSONArray response) {
                final int numberOfItemsInResp = response.length();
                for (int i = 0; i <= numberOfItemsInResp - 1; i++) {
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
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                helper.insertUsers(usersList);
                userList = helper.displayUsers(filter,beforeid);
                mAdapter = new ListAdapter(userList);
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
                recyclerView.setLayoutManager(mLayoutManager);
                //if(helper.getUsersCount(filter,beforeid)>25) {
                if(userList.size()>7){
                    recyclerView.addOnScrollListener(new EndlessRecyclerViewScrollListener((LinearLayoutManager) recyclerView.getLayoutManager()) {
                        @Override
                        public void onLoadMore(int page1, int totalItemsCount) {
                            page++;
                            setLoading();
                            getList();
                        }

                        @Override
                        public void setLoaded() {
                            super.setLoaded();
                        }
                    });
               }
                recyclerView.setItemAnimator(new DefaultItemAnimator());
                recyclerView.setAdapter(mAdapter);
                mAdapter.notifyDataSetChanged();
            }
        };
        Response.ErrorListener failure = new Response.ErrorListener() {
            public void onErrorResponse(VolleyError error) {
                Log.d("rew", error.toString());
            }
        };
        String url = "http://bismarck.sdsu.edu/hometown/users?reverse=true&page=" + page + "&"+filter;
        JsonArrayRequest getRequest = new JsonArrayRequest(url, success, failure);
        VolleyQueue.instance(this.getContext()).add(getRequest);
    }

    @Override
    public void onItemClicked(RecyclerView recyclerView, int position, View v) {
        String nickname = ((ListAdapter) recyclerView.getAdapter()).getUser(position).getNickname();
        FirebaseDatabase.getInstance().getReference().child(Constants.ARG_USERS).child(nickname).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    User user = dataSnapshot.getValue(User.class);
                    if (!TextUtils.equals(user.uid, FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                        goToChatActivity(user);
                    }

                } else
                    Toast.makeText(getActivity(), "User does not exist in Firebase", Toast.LENGTH_LONG).show();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getActivity(), "FAILED!!", Toast.LENGTH_LONG).show();
            }
        });

    }

    public void goToChatActivity(User user) {
        Intent intent = new Intent(getActivity(), ChatActivity.class);
        intent.putExtra(Constants.ARG_RECEIVER, user.nickname);
        intent.putExtra(Constants.ARG_RECEIVER_UID, user.uid);
        startActivity(intent);
    }

    public String filterList(){
        filter = "";
        if(selectedCountry != null && !selectedCountry.contains("All"))
            filter = "country="+selectedCountry;
        if(selectedState != null && !selectedState.contains("All"))
            if(filter != null)
                filter =  filter.length() > 0 ?filter+"&state="+selectedState : filter;
        if(selectedYear != null && !selectedYear.contains("All"))
            if(filter != null)
                filter = filter.length() > 0 ? filter+"&year="+selectedYear : "year="+selectedYear;
        return filter;
    }

    public void clear(){
        filter = null;
        page=0;
        userList.clear();
        getList();
    }
}

