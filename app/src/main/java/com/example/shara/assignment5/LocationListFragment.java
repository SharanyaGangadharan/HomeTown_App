package com.example.shara.assignment5;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

public class LocationListFragment extends DialogFragment {

    final ArrayList<String> countryList = new ArrayList<String>();
    String[] countries;
    String[] states;
    String selectedCountry = null;
    String selectedState = null;
    Spinner countrySpinner;
    Spinner stateSpinner;
    Button mDone, mCancel;
    NewUserFragment ca;

    public LocationListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_location_list, container, false);
        getDialog().setTitle("Country/State");
        getDialog().getWindow().setBackgroundDrawableResource(R.color.silver);
        getDialog().getWindow().setLayout(150,500);

        mDone = (Button) v.findViewById(R.id.done);
        mCancel = (Button) v.findViewById(R.id.cancel);

        countrySpinner = (Spinner) v.findViewById(R.id.countrySpinner);
        stateSpinner = (Spinner) v.findViewById(R.id.stateSpinner);
        getCountry();

        mDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ca = (NewUserFragment) getActivity();
                ca.setCountry(selectedCountry);
                ca.setState(selectedState);
                getDialog().dismiss();
            }
        });

        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDialog().dismiss();
            }
        });

        return v;

    }

    public void getCountry() {
        Log.i("rew", "Start");
        Response.Listener<JSONArray> success = new Response.Listener<JSONArray>() {
            public void onResponse(JSONArray response) {
                final int numberOfItemsInResp = response.length();
                countryList.add("Country");
                for (int i = 0; i <= numberOfItemsInResp; i++) {
                    try {
                        countryList.add(response.get(i).toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                String country = null;
                while ((country) != null) {
                    countryList.add(country);
                }
                countries = new String[countryList.size()];
                countries = countryList.toArray(countries);
                Log.d("rew", "Country List: " + countryList);
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, countries);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                countrySpinner.setAdapter(adapter);
                countrySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        selectedCountry = countries[i];
                        Toast.makeText(getActivity(),""+selectedCountry,Toast.LENGTH_SHORT);
                        getstate();
                    }

                    public void onNothingSelected(AdapterView<?> adapterView) {
                        return;
                    }
                });

            }
        };
        Response.ErrorListener failure = new Response.ErrorListener() {
            public void onErrorResponse(VolleyError error) {
                Log.d("rew", error.toString());
            }
        };
        String url = "http://bismarck.sdsu.edu/hometown/countries";
        JsonArrayRequest getRequest = new JsonArrayRequest(url, success, failure);
        VolleyQueue.instance(this.getContext()).add(getRequest);

    }

    public void getstate() {
        Log.i("rew", "Start");
        final ArrayList<String> stateList = new ArrayList<String>();
        Response.Listener<JSONArray> success = new Response.Listener<JSONArray>() {
            public void onResponse(JSONArray response) {
                stateList.add("State");
                final int numberOfItemsInResp = response.length();
                for (int i = 0; i <= numberOfItemsInResp; i++) {
                    try {
                        stateList.add(response.get(i).toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                String state = null;
                while ((state) != null) {
                    stateList.add(state);
                }
                states = new String[stateList.size()];
                states = stateList.toArray(states);
                Log.d("rew", "State List: " + stateList);
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, states);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                stateSpinner.setAdapter(adapter);
                stateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        selectedState = states[i];
                        if(selectedState.equals("State"))
                            selectedState = null;
                        Toast.makeText(getActivity(), "" + selectedState, Toast.LENGTH_SHORT);
                        Log.d("rew", "stateSelected: " + selectedState);
                    }

                    public void onNothingSelected(AdapterView<?> adapterView) {
                        return;
                    }
                });
            }
        };
        Response.ErrorListener failure = new Response.ErrorListener() {
            public void onErrorResponse(VolleyError error) {
                Log.d("rew", error.toString());
            }
        };
        if(selectedCountry.equals("Country"))
            selectedCountry = null;
        String url1 = "http://bismarck.sdsu.edu/hometown/states?country=" + selectedCountry;
        JsonArrayRequest getRequest = new JsonArrayRequest(url1, success, failure);
        VolleyQueue.instance(this.getContext()).add(getRequest);
    }
}
