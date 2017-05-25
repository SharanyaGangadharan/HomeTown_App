package com.example.shara.assignment5;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;


public class FilterFragment extends Fragment
{
    final ArrayList<String> countryList = new ArrayList<String>();
    String[] countries;
    final ArrayList<String> yearList = new ArrayList<String>();
    String[] states;
    String[] years;
    String selectedCountry = null;
    String selectedState, selectedYear;
    Spinner countrySpinner;
    Spinner stateSpinner;
    Spinner yearSpinner;
    Button mFilter,mClear;

    public FilterFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_filter, container, false);

        mFilter = (Button) v.findViewById(R.id.button3);
        mClear = (Button) v.findViewById(R.id.button4);

        countrySpinner = (Spinner) v.findViewById(R.id.countrySpinner);
        stateSpinner = (Spinner) v.findViewById(R.id.stateSpinner);
        yearSpinner = (Spinner) v.findViewById(R.id.yearSpinner);

        getCountry();
        getYear();

        mFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = getFragmentManager();
                UserListFragment fragm = (UserListFragment)fm.findFragmentById(R.id.content_frame);
                fragm.setCountry(selectedCountry);
                fragm.setState(selectedState);
                fragm.setYear(selectedYear);
                fragm.applyFilter();
            }
        });
        mClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = getFragmentManager();
                UserListFragment fragm = (UserListFragment)fm.findFragmentById(R.id.content_frame);
                fragm.clearFilter();
            }
        });

        return v;
    }

    public void getCountry() {
        Log.i("rew", "Start");
        Response.Listener<JSONArray> success = new Response.Listener<JSONArray>() {
            public void onResponse(JSONArray response) {
                final int numberOfItemsInResp = response.length();
                countryList.add("All");
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
                if (getActivity() != null) {
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, countries);
                    adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
                    countrySpinner.setAdapter(adapter);
                }
                countrySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        selectedCountry = countries[i];
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
                stateList.add("All");
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
                adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
                stateSpinner.setAdapter(adapter);
                stateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        selectedState = states[i];
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
        String url1 = "http://bismarck.sdsu.edu/hometown/states?country="+selectedCountry;
        JsonArrayRequest getRequest = new JsonArrayRequest(url1, success, failure);
        VolleyQueue.instance(this.getContext()).add(getRequest);
    }

    public void getYear() {
        yearList.add("All");
        for (int i = 2017; i >= 1970 ; i--) {
            yearList.add(Integer.toString(i));
        }
        String year = null;
        while ((year) != null) {
            try {
                yearList.add(year);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
        years = new String[yearList.size()];
        years = yearList.toArray(years);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, years);
        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        yearSpinner.setAdapter(adapter);
        yearSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedYear = years[i];
                Log.d("rew", "yearSelected: " + selectedYear);
            }
            public void onNothingSelected(AdapterView<?> adapterView) {
                return;
            }
        });
    }
}