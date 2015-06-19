package com.ganatragmail.sagar.weatherapp.data;

import android.net.Uri;
import android.test.AndroidTestCase;

public class TestWeatherContract extends AndroidTestCase {

    private static final String TEST_WEATHER_LOCATION = "/North Pole";
    private static final long TEST_WEATHER_DATE = 141903300L; //Dec 20th, 2014


    public void testBuildWeatherLocation(){
        Uri locationUri = WeatherContract.WeatherEntry.buildWeatherLocation(TEST_WEATHER_LOCATION);

        assertNotNull("Error: Null uri returned. You must fill in buildWeatherLocation in " + "WeatherContract." + locationUri);

        assertEquals("Error: Weather Location not properly appended to the end of the Uri", TEST_WEATHER_LOCATION, locationUri.getLastPathSegment());

        assertEquals("Error: Weather Location Uri doesn't match expected result", locationUri.toString(), "content://com.ganatragmail.sagar.weatherapp/weather/%2FNorth%20Pole");

    }
}
