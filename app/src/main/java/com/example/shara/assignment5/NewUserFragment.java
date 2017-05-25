package com.example.shara.assignment5;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.example.shara.assignment5.models.User;
import com.example.shara.assignment5.utils.Constants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.util.List;

public class NewUserFragment extends FragmentActivity {

    private DatabaseHelper myDb;


    public NewUserFragment() {
    }

    EditText mEmail,mNickname,mPwd,mCity,mYear;
    TextView mLocation,mCoordinates;
    String country, state;
    Double lat,lng;
    private static final int INTENT_EXAMPLE_REQUEST = 123;
    private static final int INTENT_SAMPLE_REQUEST = 123;
    Boolean isPressed =false;
    Boolean isLocationSelected = false;
    boolean valid = true, isAccountCraeted=true;
    Button b1;
    private FirebaseAuth mAuth;
    int time = (int) (System.currentTimeMillis());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_new_user);
        mAuth = FirebaseAuth.getInstance();

        mEmail = (EditText) findViewById(R.id.editEmail);
        mNickname = (EditText) findViewById(R.id.editName);
        mPwd = (EditText) findViewById(R.id.editPwd);
        mLocation = (TextView) findViewById(R.id.dispLocation);
        mCity = (EditText) findViewById(R.id.editCity);
        mYear = (EditText) findViewById(R.id.editYear);
        mCoordinates = (TextView) findViewById(R.id.dispCoordinates);
        b1 = (Button) findViewById(R.id.selectLocation);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == INTENT_SAMPLE_REQUEST) {
            switch (resultCode) {
                case 1:
                    lat = data.getDoubleExtra("lat",0.0);
                    lng = data.getDoubleExtra("lng",0.0);
                    mCoordinates.setText(lng+"/"+lat);
                    Log.d("rew","Lat"+ lat);
                    Log.d("rew","Long"+ lng);
                    break;
                case 0:
                    break;
            }
        }
    }

    public void addUserToFirebase(FirebaseUser firebaseuser) {

        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        String nickname = mNickname.getText().toString().trim();
        String email = mEmail.getText().toString().trim();
        User user = new User(firebaseuser.getUid(), nickname,
                email);
        database.child(Constants.ARG_USERS)
                .child(nickname)
                .setValue(user)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // mOnUserDatabaseListener.onSuccess(context.getString(R.string.user_successfully_added));
                       /* Toast.makeText(getApplicationContext(), "Added to Firebase",
                                Toast.LENGTH_LONG).show();*/
                        } else {
                            // mOnUserDatabaseListener.onFailure(context.getString(R.string.user_unable_to_add));
                            Toast.makeText(getApplicationContext(), "Submit Failed",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
    public boolean createAccount()
    {
        String email = mEmail.getText().toString().toLowerCase().trim();
        String password = mPwd.getText().toString().trim();
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.i("rew", "createUserWithEmail:onComplete:" + task.isSuccessful());

                        if (!task.isSuccessful()) {
                            Toast.makeText(NewUserFragment.this, task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                            isAccountCraeted = false;
                        }
                            else {
                            addUserToFirebase(task.getResult().getUser());
                            call();
                        }
                    }
                });
        return isAccountCraeted;
    }


    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
        mLocation.setText(new StringBuilder().append(country).append("/").append(state).append(" "));
    }

    public void setLocation(View view){
        isLocationSelected = true;
        LocationListFragment frag = new LocationListFragment();
        FragmentManager manager = getSupportFragmentManager();
        frag.show(manager, "dialog");
    }

    public void setCoordinates(View view){
        isPressed = true;
        Intent go1 = new Intent(this,ChooseCoordinates.class);
        go1.putExtra("country",country);
        go1.putExtra("state",state);
        go1.putExtra("city",mCity.getText().toString());
        startActivityForResult(go1, INTENT_SAMPLE_REQUEST);
        //frag1.show(manager1, "map");
    }

    public boolean isYearEmpty(){
        return mYear.getText().toString().length() > 0;
    }

    public void showMap(){
        Intent go2 = new Intent(this,CurrentUserLocMap.class);
        go2.putExtra("nickname", mNickname.getText().toString());
        go2.putExtra("lat", lat);
        go2.putExtra("lng", lng);
        go2.putExtra("city", mCity.getText().toString());
        startActivity(go2);
    }

    public void getRequest() {
        String url ="http://bismarck.sdsu.edu/hometown/nicknameexists?name=";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url+mNickname.getText().toString().toLowerCase(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Boolean result = Boolean.valueOf(response);
                        if(result) {
                            mNickname.setError("Nickname exists");
                            valid = false;
                        }
                        else
                            valid = true;
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        VolleyQueue.instance(this).add(stringRequest);
    }

    public void submit(View view) {
        //email validation
        if(mEmail.getText().toString().toLowerCase().trim().equals(""))
        {
            mEmail.setError("Email required");
            valid = false;
        }
        else{
            valid = true;
        }

        //name validation
        if (mNickname.getText().toString().toLowerCase().trim().equals("")) {
                mNickname.setError("Nickname required");
                valid=false;
        }
        else {
            getRequest();
        }

        //password validation
        if(valid){
            if (mPwd.getText().toString().trim().equals("")) {
                mPwd.setError("Password required");
                valid=false;
            }
            else
                valid = true;
        }

        if(valid){
            if (mPwd.getText().toString().length() < 3) {
                mPwd.setError("Password must be more than 3 characters");
                valid=false;
            }
            else
                valid = true;
        }

        //country validation
        if(valid){
            if(!isLocationSelected) {
                Toast.makeText(getApplicationContext(),"Country/State Required!",Toast.LENGTH_SHORT).show();
                valid=false;
            }
            else
                valid = true;
        }

        //city validation
        if(valid){
            if (mCity.getText().toString().trim().equals("")) {
                mCity.setError("City required");
                valid=false;
            }
            else
                valid = true;
        }

        if(valid){
            if(mYear.getText().toString().trim().length() > 0)
                valid = true;
        }

        if(valid){
            if (mYear.getText().toString().length()>0 && Integer.parseInt(mYear.getText().toString().trim()) >= 1970 && Integer.parseInt(mYear.getText().toString().trim()) <= 2017) {
                valid = true;
            }
            else{
                mYear.setError("Year out of range (1970-2017)");
                valid = false;
            }
        }

        if(valid)
        {
            if(!isPressed){
                generateLocFromInfo();
            }
        }

        if(valid) {
            JSONObject obj = new JSONObject();
            try {
                obj.put("nickname", mNickname.getText().toString());
                obj.put("password", mPwd.getText().toString());
                obj.put("year", Integer.valueOf(mYear.getText().toString()));
                obj.put("country", country);
                obj.put("state", state);
                obj.put("city", mCity.getText().toString());
                if (lat != null & lng != null) {
                    obj.put("longitude", lng);
                    obj.put("latitude", lat);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                valid = false;
            }
            //if (createAccount()) {
                String url = "http://bismarck.sdsu.edu/hometown/adduser";
                JsonObjectRequest postRequest = new JsonObjectRequest(url, obj,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_SHORT).show();
                                createAccount();
                                //call();
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        valid = false;
                        Log.i("rew", error.toString());
                    }
                });
                VolleyQueue.instance(getApplicationContext()).add(postRequest);
            }
        //}

    }

    public void call()
    {
         //if(valid){
            //showMap();
       /* myDb = (new DatabaseHelper(this));*/
        //SQLiteDatabase nameDb = myDb.getWritableDatabase();
        /*new GetDatabaseTask().execute(myDb);*/
        /*nameDb.execSQL("CREATE TABLE IF NOT EXISTS " + "USERS" + " ("
                + "NICKNAME" + " TEXT PRIMARY KEY NOT NULL,"
                + "CITY" + " TEXT," + "LONGITUDE" + " REAL DEFAULT (NULL),"
                + "STATE" + " TEXT," + "YEAR" + " INTEGER,"
                + "ID" + " INTEGER," + "LATITUDE" + " REAL DEFAULT (NULL),"
                + "TIMESTAMP" + " DATETIME," + "COUNTRY" + " TEXT"
                + ");");*/
        Intent login = new Intent(this,LoginActivity.class);
            startActivity(login);
        //}
    }

    public class GetDatabaseTask extends AsyncTask<SQLiteOpenHelper,Void, Void> {
        @Override
        protected Void doInBackground(SQLiteOpenHelper... params) {
            params[0].getWritableDatabase();
            return null;
        }
    }

    public void generateLocFromInfo()
    {
        Geocoder locator = new Geocoder(this);
        try {
            List<Address> Locations =
                    locator.getFromLocationName(mCity.getText().toString()+","+state+","+country,3);
            for (Address userLocation: Locations) {
                if (userLocation.hasLatitude()){
                    lat = userLocation.getLatitude();
                    Log.d("rew", "Lat " + userLocation.getLatitude());}
                if (userLocation.hasLongitude()){
                    lng = userLocation.getLongitude();
                    Log.d("rew", "Long " + userLocation.getLongitude());}
            }
        } catch (Exception error) {
            Log.e("rew", "Address lookup Error", error);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

}
