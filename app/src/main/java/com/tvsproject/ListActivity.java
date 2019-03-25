package com.tvsproject;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ListActivity extends AppCompatActivity implements View.OnClickListener,ListDetailsInterface{
    Context context;
    TextView tvHeading;
    String data;
    ArrayList<UserModel> userModelsList = new ArrayList<>();
    UserListAdapter userListAdapter;
    RecyclerView rvUserList;
    LinearLayoutManager linearLayoutManager;
    Toolbar toolbar;
    Button btChart, btMap;
    ProgressDialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        context         = this;
        tvHeading       = findViewById(R.id.tvHeading);
        rvUserList      = findViewById(R.id.rvUserList);
        toolbar         = findViewById(R.id.toolbar);
        btChart         = findViewById(R.id.btChart);
        btMap           = findViewById(R.id.btMap);

        if (getIntent() != null){
            data = getIntent().getStringExtra("data");
        }

        setSupportActionBar(toolbar);

        linearLayoutManager = new LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false);
        rvUserList.setLayoutManager(linearLayoutManager);
        userListAdapter = new UserListAdapter(context,userModelsList,this);
        rvUserList.setAdapter(userListAdapter);

//        setUserData();
        new UserListTask().execute();

        btMap.setOnClickListener(this);
        btChart.setOnClickListener(this);
    }

    public ArrayList<UserModel> setUserData(){
        try {
            if (data != null) {
                JSONObject jsonObject = new JSONObject(data);
                if (jsonObject.has("TABLE_DATA")) {
                    JSONObject tableData = new JSONObject(jsonObject.getString("TABLE_DATA"));
                    JSONArray jsonArray = tableData.getJSONArray("data");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONArray json = (JSONArray) jsonArray.get(i);
                        UserModel userModel = new UserModel();
                        userModel.setName(json.getString(0));
                        userModel.setDesignation(json.getString(1));
                        userModel.setCity(json.getString(2));
                        userModel.setEmployeeNo(json.getString(3));
                        userModel.setDate(json.getString(4));
                        userModel.setSalary(json.getString(5));
                        LatLng latlng = getLatLng(json.getString(2));
                        if (latlng != null){
                            userModel.setLatLng(latlng);
                            userModelsList.add(userModel);
                        }


                    }

                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return userModelsList;
    }

    public LatLng getLatLng(String city){
        if (city.equals("Sidney")){
            city = "Sydney";
        }
        double latitude=0.0, longitude=0.0;
        if(Geocoder.isPresent()){
            try {
                Geocoder gc = new Geocoder(this);
                List<Address> addresses= gc.getFromLocationName(city, 1); // get the found Address Objects
                List<LatLng> ll = new ArrayList<LatLng>(addresses.size()); // A list to save the coordinates if they are available
                for(Address a : addresses){
                    if(a.hasLatitude() && a.hasLongitude()){
                        ll.add(new LatLng(a.getLatitude(), a.getLongitude()));
                        latitude = a.getLatitude();
                        longitude = a.getLongitude();

                    }
                }
                return new LatLng(latitude,longitude);
            } catch (IOException e) {
                // handle the exception
            }
        }
        return null;
    }
    @Override
    public void listDetails(UserModel userData) {
        Intent intent = new Intent(context,EmployeeDetailsActivity.class);
        intent.putExtra("userDetails",userData);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);

        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();

        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvHeading.setVisibility(View.GONE);
            }
        });
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                tvHeading.setVisibility(View.VISIBLE);
                return false;
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (TextUtils.isEmpty(newText)) {
                    userListAdapter.getFilter().filter("");
                    userListAdapter.notifyDataSetChanged();
                } else {
                    userListAdapter.getFilter().filter(newText);
                }
                return true;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btChart:
                Intent intent1 = new Intent(ListActivity.this,SalaryChartActivity.class);
                intent1.putExtra("userDetails",userModelsList);
                startActivity(intent1);
                break;
            case R.id.btMap:
                Intent intent = new Intent(ListActivity.this,MapsActivity.class);
                intent.putExtra("userDetails",userModelsList);
                startActivity(intent);
                break;
        }
    }

    private class UserListTask extends AsyncTask<Void, Void, ArrayList<UserModel>> {

        UserListTask() {
            dialog = new ProgressDialog(ListActivity.this);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (dialog != null) {
                dialog.setMessage("Loading..");
                dialog.setCancelable(false);
                if (!dialog.isShowing())
                    dialog.show();
            }
        }

        @Override
        protected ArrayList<UserModel> doInBackground(Void... voids) {
//            setUserData();
            return setUserData();
        }

        @Override
        protected void onPostExecute(ArrayList<UserModel> s) {
            super.onPostExecute(s);
            if (dialog.isShowing())
                dialog.dismiss();
            userListAdapter.updateList(s);

        }
    }
}
