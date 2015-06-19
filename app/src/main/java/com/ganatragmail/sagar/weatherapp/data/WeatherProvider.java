package com.ganatragmail.sagar.weatherapp.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;


public class WeatherProvider extends ContentProvider {

    //The URI Matcher used by this content provider
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private WeatherDbHelper mOpenHelper;

    static final int WEATHER = 100;
    static final int WEATHER_WITH_LOCATION = 101;
    static final int WEATHER_WITH_LOCATION_AND_DATE = 102;
    static final int LOCATION = 300;

    private static final SQLiteQueryBuilder sWeatherByLocationSettingQueryBuilder;

    static {
        sWeatherByLocationSettingQueryBuilder = new SQLiteQueryBuilder();

        //This is an inner join which looks like
        //weather INNER JOIN  location ON weather.location_id = location._id
        sWeatherByLocationSettingQueryBuilder.setTables(
                WeatherContract.LocationEntry.TABLE_NAME + " INNER JOIN " +
                WeatherContract.WeatherEntry.TABLE_NAME + " ON " +
                WeatherContract.WeatherEntry.TABLE_NAME + "." +
                WeatherContract.WeatherEntry.COLUMN_LOC_KEY + " = " +
                WeatherContract.LocationEntry.TABLE_NAME + "." +
                WeatherContract.LocationEntry._ID
        );
    }

    //location.location_setting = ?
    private static final String sLocationSetting = WeatherContract.LocationEntry.TABLE_NAME + "." +
            WeatherContract.LocationEntry.COLUMN_LOCATION_SETTINGS + " = ? ";

    //location.location_setting = ? AND date >= ?
    private  static String sLocationSettingWithStartDateSelection = WeatherContract.LocationEntry.TABLE_NAME + "." +
            WeatherContract.LocationEntry.COLUMN_LOCATION_SETTINGS + " =? AND " +
            WeatherContract.WeatherEntry.COLUMN_DATE + " >= ? ";

    //location.location_setting = ? AND date = ?


    static UriMatcher buildUriMatcher(){
        return null;
    }


    @Override
    public boolean onCreate() {
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return null;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}
