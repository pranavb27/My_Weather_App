package com.example.android.sunshine.activities;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.sunshine.R;

public class ForecastAdapter extends RecyclerView.Adapter<ForecastAdapter.ForecastAdapterViewHolder> {

    private String[] mWeatherData;
    private final ForecastAdapterOnClickHandler mClickHandler;

    //Create an interface for click handler
    public interface ForecastAdapterOnClickHandler{
        void OnClick(String weatherForDay);

    }

    //Create Empty Constructor
    public ForecastAdapter(ForecastAdapterOnClickHandler clickHandler){
        mClickHandler = clickHandler;

    }






    public class ForecastAdapterViewHolder extends  RecyclerView.ViewHolder implements View.OnClickListener {
        public final TextView mWeatherTextView;


        public ForecastAdapterViewHolder(View view) {
            super(view);
            mWeatherTextView =  view.findViewById(R.id.tv_weather_data);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPoition = getAdapterPosition();
            String weatherForDay = mWeatherData[adapterPoition];
            Log.d("ForecastAdapter", "Weather for day: " + weatherForDay);
            mClickHandler.OnClick(weatherForDay);
        }
    }







    @Override
    public ForecastAdapterViewHolder onCreateViewHolder( ViewGroup viewGroup, int i) {
        Context context = viewGroup.getContext();
        int LayoutIdForListItem = R.layout.forecast_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToparentImmediately = false;

        View view = inflater.inflate(LayoutIdForListItem, viewGroup, shouldAttachToparentImmediately);

        return new ForecastAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder( ForecastAdapterViewHolder forecastAdapterViewHolder, int position) {
    String WeatherDataForThisDay = mWeatherData[position];
    forecastAdapterViewHolder.mWeatherTextView.setText(WeatherDataForThisDay);


    }

    @Override
    public int getItemCount() {
        if (mWeatherData == null)
        {return  0;}
        else
        {
            return mWeatherData.length;
        }
    }


    public  void setWeatherData(String[] weatherData){
        mWeatherData = weatherData;
        notifyDataSetChanged();
    }




}

