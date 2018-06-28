package com.ego.sandbox.sqlitesync.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.ego.sandbox.sqlitesync.R;
import com.ego.sandbox.sqlitesync.dao.StoreDao;
import com.ego.sandbox.sqlitesync.entities.Store;
import com.ego.sandbox.sqlitesync.helpers.StoreAdapter;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
        StoreDao storeDao;
        ListView storeListview;
        StoreAdapter mStoreAdapter;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);
            storeListview = (ListView) findViewById(R.id.listviewStores);
            doGetData();
        }

        @Override
        protected void onResume() {
            super.onResume();
            doGetData();
        }

        @Override
        public boolean onCreateOptionsMenu(Menu menu) {
            // Inflate the menu; this adds items to the action bar if it is present.
            getMenuInflater().inflate(R.menu.menu_main, menu);
            return true;
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            // Handle action bar item clicks here. The action bar will
            // automatically handle clicks on the Home/Up button, so long
            // as you specify a parent activity in AndroidManifest.xml.
            int id = item.getItemId();

            //noinspection SimplifiableIfStatement
            if (id == R.id.action_refresh) {
                doGetData();
                return true;
            }

            return super.onOptionsItemSelected(item);
        }

        private void doGetData(){
            //if device has internet connection get db data
            if(deviceHasInternetConnection()){
                getDBData();
            }
            setList();
        }

        private void setList(){
            if(storeDao == null){
                storeDao = new StoreDao(MainActivity.this);
            }

            ArrayList<Store> stores = storeDao.readAll();

            System.out.println(stores);

            mStoreAdapter = new StoreAdapter(getApplicationContext(), stores);
            storeListview.setAdapter(mStoreAdapter);
            mStoreAdapter.notifyDataSetChanged();
        }

        private void getDBData(){
            //Initialize Progress Dialog properties
            final ProgressDialog prgDialog = new ProgressDialog(this);
            prgDialog.setMessage("Synching SQLite Data with Remote MySQL DB. Please wait...");
            prgDialog.setCancelable(false);
            prgDialog.show();

            // Instantiate the RequestQueue.
            RequestQueue queue = Volley.newRequestQueue(this);
            final String ALL_STORES = "http://service.e-gostudio.com/api/sync_stores/stores.php";

            // Request a string response from the provided URL.
            StringRequest stringRequest = new StringRequest(Request.Method.GET, ALL_STORES,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            ArrayList<Store> stores = mapJsonToStoreObject(response);
                            if(storeDao == null){
                                storeDao = new StoreDao(MainActivity.this);
                            }
                            storeDao.update(stores);
                            prgDialog.hide();
                        }
                    },

                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            prgDialog.hide();
                            Toast.makeText(MainActivity.this, "Something went wrong in getting stores", Toast.LENGTH_SHORT).show();
                        }
                    });

            // Add the request to the RequestQueue.
            queue.add(stringRequest);
        }

        private ArrayList<Store> mapJsonToStoreObject(String jsonArray){
            ObjectMapper mapper = new ObjectMapper();
            ArrayList<Store> stores = new ArrayList<>();
            ArrayList<Map<String, ?>> storeArray = null;
            Store store = null;

            try {
                storeArray = mapper.readValue(jsonArray, ArrayList.class);
                for (Map<String, ?> map : storeArray) {
                    store = new Store(Integer.valueOf((String) map.get("id")), (String) map.get("name"));
                    stores.add(store);
                }
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Er is wat fout gegaan bij het parsen van de json data");
            }
            return stores;
        }

        private boolean deviceHasInternetConnection() {
            ConnectivityManager connectivityManager = (ConnectivityManager)
                    getApplicationContext().getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
            return activeNetwork != null && activeNetwork.isConnected();
        }

}
