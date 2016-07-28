package com.mohamedibrahim.sunshine;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class ForecastFragment extends Fragment implements AsyncResponse {

    ArrayAdapter<String> mForecastAdapter;
    ListView listView;
    private String location;
    SharedPreferences preferences;

    public ForecastFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_forecast, container, false);
        mForecastAdapter = new ArrayAdapter<String>(
                getActivity(),
                R.layout.list_item_forecast,
                R.id.list_item_forecast_textview,
                new ArrayList<String>()
        );
        listView = (ListView) rootView.findViewById(R.id.listview_forecast);
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        updateWeather();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.forecastfragment, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                updateWeather();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void processFinish(String[] AsyncTaskResult) {

        List<String> weekForecast = new ArrayList<String>(Arrays.asList(AsyncTaskResult));
        mForecastAdapter.clear();
        for (String s : weekForecast) {
            mForecastAdapter.add(s);
        }
        listView.setAdapter(mForecastAdapter);
//        mForecastAdapter.notifyDataSetChanged();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                String forecast = mForecastAdapter.getItem(position);
                Intent detailIntent = new Intent(getActivity(), DetailActivity.class);
                detailIntent.putExtra(Intent.EXTRA_TEXT, forecast);
                startActivity(detailIntent);
            }
        });
    }

    private void updateWeather() {
        preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        location = preferences.getString(getString(R.string.pref_location_key),
                getString(R.string.pref_location_default));

        FetchWeatherTask fetchWeatherTask = new FetchWeatherTask(this,getActivity());
        fetchWeatherTask.execute(location);
    }
}
