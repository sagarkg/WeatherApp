package com.ganatragmail.sagar.weatherapp.data;

import android.provider.BaseColumns;
import android.text.format.Time;



public class WeatherContract {

    public static long normalizeDate(long startDate){
        //normalize the start date to the beginning of the UTC day
        Time time = new Time();
        time.set(startDate);
        int julianDay = Time.getJulianDay(startDate, time.gmtoff);
        return time.setJulianDay(julianDay);
    }

    public static final class LocationEntry implements BaseColumns{
        public static final String TABLE_NAME = "location";

        public static final String COLUMN_LOCATION_SETTINGS = "location_settings";

        public static final String COLUMN_CITY_NAME = "city_name";

        public static final String COLUMN_COORDINATES_LAT = "latitude";
        public static final String COLUMN_COORDINATES_LONG = "longitude";

    }

    public static final class WeatherEntry implements BaseColumns{
        public static final String TABLE_NAME = "weather";

        public static final String COLUMN_LOC_KEY = "location_id";

        public static final String COLUMN_DATE = "date";

        public static final String COLUMN_WEATHER_ID = "weather_id";

        public static final String COLUMN_SHORT_DESC = "short_desc";

        public static final String COLUMN_MIN_TEMP = "min";
        public static final String COLUMN_MAX_TEMP = "max";

        public static final String COLUMN_HUMIDITY = "humidity";

        public static final String COLUMN_PRESSURE = "pressure";

        public static final String COLUMN_WIND_SPEED = "wind";

        public static final String COLUMN_DEGREES = "degrees";
    }
}
