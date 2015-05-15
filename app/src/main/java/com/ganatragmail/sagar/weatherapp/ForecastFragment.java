package com.ganatragmail.sagar.weatherapp;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;

public class ForecastFragment extends Fragment {

    public static final String INTENT_EXTRA = "com.ganatrasagar.MainActivity";
    private static final String LOG_TAG = ForecastFragment.class.getSimpleName();
    private ArrayAdapter<String> mForecastAdapter;


    public ForecastFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        inflater.inflate(R.menu.forecastfragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        if(id == R.id.action_refresh){
            updateWeatherData();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);


        mForecastAdapter = new ArrayAdapter(getActivity(), R.layout.list_item_forecast, R.id.list_item_forecast_textView);


        ListView listView = (ListView) rootView.findViewById(R.id.listview_forecast);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String forecast = mForecastAdapter.getItem(position);
                Intent intent = new Intent(getActivity(), DetailActivity.class);
                intent.putExtra(INTENT_EXTRA, forecast);
                startActivity(intent);
            }
        });

        listView.setAdapter(mForecastAdapter);

        return rootView;
    }

    @Override
    public void onStart(){
        super.onStart();
        updateWeatherData();
    }

    public void onResume(){
        super.onResume();
        updateWeatherData();
    }

    public class FetchWeatherTask extends AsyncTask<String, Void, String[]>{

        private final String LOG_TAG = FetchWeatherTask.class.getSimpleName();

        private static final String ZIP_CODE = "q";
        private static final String MODE = "mode";
        private static final String UNITS = "units";
        private static final String COUNT_DAYS = "cnt";


        @Override
        protected String[] doInBackground(String... params) {

            //Network call for forecast data
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String forecastJsonStr = null;

            String zip;

            if(params.length == 0) {
                zip = "94043";
            }else {
                zip = params[0];
            }

            try {

                Uri.Builder builder = new Uri.Builder();
                builder.scheme("http")
                        .authority("api.openweathermap.org")
                        .appendPath("data")
                        .appendPath("2.5")
                        .appendPath("forecast")
                        .appendPath("daily")
                        .appendQueryParameter(ZIP_CODE, zip)
                        .appendQueryParameter(MODE, "json")
                        .appendQueryParameter(UNITS, "metric")
                        .appendQueryParameter(COUNT_DAYS, "7");

                String myUrl = builder.build().toString();

                URL url = new URL(myUrl);

                Log.d(LOG_TAG, "Uri built url: " + myUrl);


                urlConnection = (HttpURLConnection)url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if(inputStream == null){
                    return null;
                }

                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while((line = reader.readLine())!= null){
                    buffer.append(line + "\n");
                }

                if(buffer.length() == 0){
                    return null;
                }

                forecastJsonStr = buffer.toString();
            }catch(IOException e){
                Log.e(LOG_TAG, "Error ", e);
            } finally {
                if(urlConnection!= null){
                    urlConnection.disconnect();
                }
                if(reader != null){
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream " + e);
                    }
                }
            }

            //Log.v(LOG_TAG, "forecastJsonStr: " +  forecastJsonStr.toString());

            //return forecastJsonStr;
            String[] resultStrs = null;
            try {
                resultStrs = getWeatherDataFromJson(forecastJsonStr, 7);
            } catch (JSONException e) {

                Log.e(LOG_TAG, "Error getting Json data " + e);
            }

            /*
            for (String s : resultStrs) {
                    Log.v(LOG_TAG, "Forecast entry: " + s);
            }
            */

            return resultStrs;

        }
       //End of doInBackground


       //on PostExeute
        @Override
        protected void onPostExecute(String[] result) {
            super.onPostExecute(result);

            if(result != null){
                mForecastAdapter.clear();
                for(String perDayData : result){
                    mForecastAdapter.add(perDayData);
                }
            }

        }
        //End of onPostExecute
    }

    //JSON Parsing Code

    private String getReadableDateString(long time){
        // Because the API returns a unix timestamp (measured in seconds),
        // it must be converted to milliseconds in order to be converted to valid date.
        SimpleDateFormat shortenedDateFormat = new SimpleDateFormat("EEE MMM dd");
        return shortenedDateFormat.format(time);
    }

    // Prepare the weather high/lows for presentation.

    private String formatHighLows(double high, double low) {
        // For presentation, assume the user doesn't care about tenths of a degree.
        long roundedHigh = Math.round(high);
        long roundedLow = Math.round(low);

        String highLowStr = roundedHigh + "/" + roundedLow;
        return highLowStr;
    }

    private String formatHighLowsFahrenheit(double high, double low) {
        // For presentation, assume the user doesn't care about tenths of a degree.
        long roundedHigh = Math.round((high*9/5)+32);
        long roundedLow = Math.round((low*9/5)+32);


        String highLowStr = roundedHigh + "/" + roundedLow;
        return highLowStr;
    }

    //Creates an array of weather data
    private String[] getWeatherDataFromJson(String forecastJsonStr, int numDays)
            throws JSONException {

        // These are the names of the JSON objects that need to be extracted.
        final String OWM_LIST = "list";
        final String OWM_WEATHER = "weather";
        final String OWM_TEMPERATURE = "temp";
        final String OWM_MAX = "max";
        final String OWM_MIN = "min";
        final String OWM_DESCRIPTION = "main";

        JSONObject forecastJson = new JSONObject(forecastJsonStr);
        JSONArray weatherArray = forecastJson.getJSONArray(OWM_LIST);

        // OWM returns daily forecasts based upon the local time of the city that is being
        // asked for, which means that we need to know the GMT offset to translate this data
        // properly.

        // Since this data is also sent in-order and the first day is always the
        // current day, we're going to take advantage of that to get a nice
        // normalized UTC date for all of our weather.

        Time dayTime = new Time();
        dayTime.setToNow();

        // we start at the day returned by local time. Otherwise this is a mess.
        int julianStartDay = Time.getJulianDay(System.currentTimeMillis(), dayTime.gmtoff);

        // now we work exclusively in UTC
        dayTime = new Time();

        String[] resultStrs = new String[numDays];
        for(int i = 0; i < weatherArray.length(); i++) {
            // For now, using the format "Day, description, hi/low"
            String day;
            String description;
            String highAndLow;

            // Get the JSON object representing the day
            JSONObject dayForecast = weatherArray.getJSONObject(i);

            // The date/time is returned as a long.  We need to convert that
            // into something human-readable, since most people won't read "1400356800" as
            // "this saturday".
            long dateTime;
            // Cheating to convert this to UTC time, which is what we want anyhow
            dateTime = dayTime.setJulianDay(julianStartDay+i);
            day = getReadableDateString(dateTime);

            // description is in a child array called "weather", which is 1 element long.
            JSONObject weatherObject = dayForecast.getJSONArray(OWM_WEATHER).getJSONObject(0);
            description = weatherObject.getString(OWM_DESCRIPTION);

            // Temperatures are in a child object called "temp".  Try not to name variables
            // "temp" when working with temperature.  It confuses everybody.
            JSONObject temperatureObject = dayForecast.getJSONObject(OWM_TEMPERATURE);
            double high = temperatureObject.getDouble(OWM_MAX);
            double low = temperatureObject.getDouble(OWM_MIN);

            //Check metric type set

            String setMetricType = PreferenceManager.getDefaultSharedPreferences(getActivity())
                    .getString(getString(R.string.prefs_units_key), "");

            Log.d(LOG_TAG, "Metric Value: " + setMetricType);

            if(setMetricType.equals("fahrenheit")) {
                highAndLow = formatHighLowsFahrenheit(high, low);
            } else {
                highAndLow = formatHighLows(high, low);
            }


            //highAndLow = formatHighLowsFahrenheit(high,low);
            resultStrs[i] = day + " - " + description + " - " + highAndLow;
        }

        for (String s : resultStrs) {
            Log.v(LOG_TAG, "Forecast entry: " + s);
        }

        return resultStrs;

    }

    //END of JSON Parsing code
    public void updateWeatherData(){
        String location = PreferenceManager.getDefaultSharedPreferences(getActivity())
                .getString(getString(R.string.prefs_location_key), null);
        //Log.v(LOG_TAG, "location is: " + location);
        FetchWeatherTask task =  new FetchWeatherTask();
        task.execute(location);
    }
    /*
    public void updateMetricUnits(String metric){
        String setMetric = PreferenceManager.getDefaultSharedPreferences(getActivity())
                .getString(getString(R.string.prefs_metric_key), "celsius");
        if(setMetric == "celsius"){

        }
    }
    */
}