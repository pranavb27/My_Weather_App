/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.sunshine.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.sunshine.R;
import com.example.android.sunshine.data.SunshinePreferences;
import com.example.android.sunshine.utilities.NetworkUtils;
import com.example.android.sunshine.utilities.OpenWeatherJsonUtils;

import java.net.URL;

public class MainActivity extends AppCompatActivity implements ForecastAdapter.ForecastAdapterOnClickHandler,
        LoaderManager.LoaderCallbacks<String[]> , SharedPreferences.OnSharedPreferenceChangeListener
    {

    //private TextView weatherDisplay;
    private TextView errorMessage;
    private ProgressBar loadingPB;
    private RecyclerView mRecyclerView;
    private ForecastAdapter mForecastAdapter;
    private int FORECAST_LOADER_ID = 0;
    //Initially set the flag to false so whenever this activity is created data will be refreshed and preferences
    //will be updated if they were changed
    private static boolean preferenceFlag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forecast);

        //weatherDisplay = findViewById(R.id.tv_weather_data);
        mRecyclerView = findViewById(R.id.recyclerView_forecast);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);


        mForecastAdapter = new ForecastAdapter(this);
        mRecyclerView.setAdapter(mForecastAdapter);

        errorMessage = findViewById(R.id.error_message_display);
        loadingPB  = findViewById(R.id.loadingProgressBar);

        int LoaderId = FORECAST_LOADER_ID;

        LoaderManager.LoaderCallbacks<String[]> callback = MainActivity.this;
        Bundle bundleForLoader = null;
        getSupportLoaderManager().initLoader(LoaderId, bundleForLoader, callback);


        //Register the Preference Listener
        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);

        //loadWeatherData();
    }
/*
    private void loadWeatherData() {
        showWeatherDataView();

        String location = SunshinePreferences.getPreferredWeatherLocation(this);
        new FetchWeatherData().execute(location);

    }
*/
    private void showWeatherDataView(){
        //Make the error message invisible
        errorMessage.setVisibility(View.INVISIBLE);
        //Show the weather data
        mRecyclerView.setVisibility(View.VISIBLE);

    }

    private void showErrorMessage(){
        //Hide the weather data
        mRecyclerView.setVisibility(View.INVISIBLE);
        //Make the error message visible
        errorMessage.setVisibility(View.VISIBLE);
    }

    @Override
    public void OnClick(String weatherForDay) {
        Log.d("MainActivity", "Weather Today: " + weatherForDay);
        //Toast.makeText(this, weatherForDay, Toast.LENGTH_SHORT).show();
        //Launch Detail Activity
        //Create explicit intent
        Intent intent = new Intent(MainActivity.this, DetailActivity.class );
        //Also pass the weather data to show
        //String Key = ???
        // intent.EXTRA_TEXT acts as key, put Extra accepts key-value pairs
        intent.putExtra(intent.EXTRA_TEXT, weatherForDay);

        startActivity(intent);

    }

    /****************************** Loader Methods ************************************************/

        @NonNull
        @Override
        public  Loader<String[]> onCreateLoader(int i,  Bundle bundle) {
           return new AsyncTaskLoader<String[]>(this){
               //Create an array string to get the weather data after loading is done
               String[] mWeatherData = null;



               @Override
               protected void onStartLoading() {
                   super.onStartLoading();
                   //On Start Loading,  deliver the data once done else show loading indicator
                   if (mWeatherData != null)
                   {
                       deliverResult(mWeatherData);
                   }
                   else
                   {
                       loadingPB.setVisibility(View.VISIBLE);
                       forceLoad();
                   }

               }

               @Override
               public String[] loadInBackground() {
                   String location = SunshinePreferences.getPreferredWeatherLocation(MainActivity.this);
                   URL weatherRequestUrl = NetworkUtils.buildUrl(location);
                   //Now we hav got the URL,  then request for the weather data
                   //The request return data into JSON format so appropriately capture it
                   try{
                       String jsonWatherData= NetworkUtils.getResponseFromHttpUrl(weatherRequestUrl);
                       //Convertt it into simple weather data;
                       String[] simpleJsonWeatherData = OpenWeatherJsonUtils.getSimpleWeatherStringsFromJson(MainActivity.this,jsonWatherData);

                       return simpleJsonWeatherData;

                   }catch (Exception e)
                   {
                       e.printStackTrace();
                       return  null;
                   }
               }

               public void deliverResult(String[] data){
                   mWeatherData = data;
                   super.deliverResult(data);
               }
           };
        }

        @Override
        public void onLoadFinished(@NonNull Loader<String[]> loader, String[] data) {
            //If data is loaded show  the data else error message
            loadingPB.setVisibility(View.INVISIBLE);
            if(data == null)
            {
                showErrorMessage();
            }
            else
            {
                mForecastAdapter.setWeatherData(data);
                showWeatherDataView();
            }

        }

        @Override
        public void onLoaderReset(@NonNull Loader<String[]> loader) {

        }


        private void invalidateData(){
            mForecastAdapter.setWeatherData(null);
        }
/*****************************************END******************************************************/


public  void openLocationMap(){
    //Set the address string
    //String locationAddress = "1600 Ampitheatre Parkway, CA";

    String locationAddress = SunshinePreferences.getPreferredWeatherLocation(this);


    //Generate he required Uri
    Uri geoLocation = Uri.parse("geo:0,0?q=" + locationAddress);
    //Create the implicit intent for opening map
    Intent intent = new Intent(Intent.ACTION_VIEW);
    intent.setData(geoLocation);
    //Check if app capable of opening map is present on the device
    if (intent.resolveActivity(getPackageManager()) != null)
    {
        startActivity(intent);
    }
    else
    {
        //Show message that no app found to execute this task
        Toast.makeText(this, "No app found to open Map!", Toast.LENGTH_SHORT).show();
    }


}

        @Override
        public boolean onCreateOptionsMenu(Menu menu) {

            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.forecast,menu);
            //Return true so as to display menu on toolbar
            return true;

        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {

            int id = item.getItemId();

            if(id == R.id.action_refresh){
                mForecastAdapter.setWeatherData(null);
                //loadWeatherData();
                invalidateData();
                getSupportLoaderManager().restartLoader(FORECAST_LOADER_ID, null, this);

                return true;
            } else if( id ==R.id.action_open_map)
            {
                openLocationMap();
                return true;
            } else if (id == R.id.action_settings)
            {
                Context mContext = MainActivity.this;
                Class destinationActivity = SettingsActivity.class;
                Intent settingsIntent = new Intent(mContext, destinationActivity);
                startActivity(settingsIntent);
            }

            return super.onOptionsItemSelected(item);
        }

/****************************** Methods For implementing OnSharedPreferenceChanged Listener *******/

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
            preferenceFlag  = true;

        }
        /**
         * OnStart is called when the Activity is coming into view. This happens when the Activity is
         * first created, but also happens when the Activity is returned to from another Activity. We
         * are going to use the fact that onStart is called when the user returns to this Activity to
         * check if the location setting or the preferred units setting has changed. If it has changed,
         * we are going to perform a new query.
         */

        @Override
        protected void onStart() {
            super.onStart();
            if(preferenceFlag)
            {
                Log.d("MainActivity", "Preferences were updated ");
                getSupportLoaderManager().restartLoader(FORECAST_LOADER_ID, null,this);
                preferenceFlag = false;

            }

    }

        //Unregister the Shared Preference Listener in onDestroy
        @Override
        protected void onDestroy() {
            super.onDestroy();
            PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);
        }


    /****************************************** END ***********************************************/

    /************************************* Async Task Methods **************************************/
/*
        //Async task allows parallel working and is quite useful
    public class FetchWeatherData extends AsyncTask<String, Void, String[]> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            //Show the loading indicator while the data is loading
            loadingPB.setVisibility(View.VISIBLE);
        }

        @Override
        protected String[] doInBackground(String... strings) {
            //If no zip code return
            if (strings.length == 0)
            {
                return null;
            }
            String location = strings[0];
            URL weatherRequestUrl = NetworkUtils.buildUrl(location);
            //Now we hav got the URL,  then request for the weather data
            //The request return data into JSON format so appropriately capture it
            try{
                String jsonWatherData= NetworkUtils.getResponseFromHttpUrl(weatherRequestUrl);
                //Convertt it into simple weather data;
                String[] simpleJsonWeatherData = OpenWeatherJsonUtils.getSimpleWeatherStringsFromJson(MainActivity.this,jsonWatherData);

                return simpleJsonWeatherData;

            }catch (Exception e)
            {
                e.printStackTrace();
                return  null;
            }

        }

        @Override
        protected void onPostExecute(String[] weatherData) {
            super.onPostExecute(weatherData);

            //As weather data is finished loading, make the progress bar invisible
            loadingPB.setVisibility(View.INVISIBLE);

            //If weather Data is not null, iterate through it and display it
            if(weatherData != null)
            {
                //Show the weather data
                showWeatherDataView();

                mForecastAdapter.setWeatherData(weatherData);
            }
            else{
                //That means weather data is null,  hence show error message
                showErrorMessage();
            }
        }
    }

 */
/******************************************* END **************************************************/




}