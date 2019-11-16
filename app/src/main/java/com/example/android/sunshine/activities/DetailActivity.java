package com.example.android.sunshine.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ShareCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.android.sunshine.R;

public class DetailActivity extends AppCompatActivity {

    private TextView mWeatherDisplay;
    private String mForecast;
    private static final String FORECAST_SHARE_HASHTAG = " #SunshineApp";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mWeatherDisplay = findViewById(R.id.tv_weatherData_today);

        //Toast.makeText(this, "Entered into Detailed Activity",Toast.LENGTH_SHORT).show();
        //Get the intent that started this activity
        Intent intentThatCreatedActivity = getIntent();
        if(intentThatCreatedActivity != null )
        {
            //Get the weather data for the day using has Extra
            mForecast = intentThatCreatedActivity.getStringExtra(intentThatCreatedActivity.EXTRA_TEXT);
            Log.d("DetailActivity", "Weather Data" + mForecast);
            mWeatherDisplay.setText(mForecast);

        }
    }
    //Use Share Compat class to enable weather sharing functionality
    private Intent createShareIntent(){
        Intent shareIntent;
        shareIntent = ShareCompat.IntentBuilder.from(this)       //Build the required intent for sharing
                .setType("text/plain")                           //Set the data type of what is going to be shared
                .setText(mForecast + FORECAST_SHARE_HASHTAG)     //Give the required data to the intent
                .getIntent();                                    //Get the intent
        return shareIntent;

    }

    //Inflate te menu layout consisting the share option
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        MenuItem menuItem = menu.findItem(R.id.action_share);
        menuItem.setIntent(createShareIntent());

        return true;
    }


}
