package com.teamtreehouse.friendlyforecast.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.teamtreehouse.friendlyforecast.services.Forecast;

import java.sql.SQLException;

/**
 * Created by Aditya Shirole on 5/28/2015.
 */
public class ForecastDataSource {

    private SQLiteDatabase mDatabase;
    private ForecastHelper mForecastHelper;
    private Context mContext;


    public ForecastDataSource(Context context){
        mContext = context;
        mForecastHelper = new ForecastHelper(mContext);
    }


    //open
    public void open() throws SQLException{
        mDatabase = mForecastHelper.getWritableDatabase();
    }

    //close
    public void close(){
        mDatabase.close();
    }

    //insert
    public void insertForecast(Forecast forecast){
        mDatabase.beginTransaction();

        try {
            for (Forecast.HourData hour : forecast.hourly.data) {
                ContentValues contentValues = new ContentValues();
                contentValues.put(ForecastHelper.COL_TEMPERATURE, hour.temperature);
                mDatabase.insert(ForecastHelper.TABLE_TEMPERATURES, null, contentValues);
            }
            mDatabase.setTransactionSuccessful();
        }
        finally {
            mDatabase.endTransaction();
        }

    }

    //select
    public Cursor selectAllTemperatures() {
        Cursor cursor = mDatabase.query(
                ForecastHelper.TABLE_TEMPERATURES,  //table
                new String[] { ForecastHelper.COL_TEMPERATURE }, //column names
                null, //where clause
                null,   //where parameters
                null, //group by
                null,  //having
                null //order by
        );


        return cursor;
    }

    public Cursor selectTempsGreaterThan(String minTemp) {
        String whereClause = ForecastHelper.COL_TEMPERATURE + " > ?";       //? is parameter

        Cursor cursor = mDatabase.query(
                ForecastHelper.TABLE_TEMPERATURES,  //table
                new String[] { ForecastHelper.COL_TEMPERATURE }, //column names
                whereClause, //where clause
                new String[] { minTemp },   //where parameters
                null, //group by
                null,  //having
                null //order by
        );

        return cursor;
    }

    //update
    public int updateTemperature(double newTemp) {

        ContentValues values = new ContentValues();
        values.put(ForecastHelper.COL_TEMPERATURE,newTemp);
        int rowsUpdated = mDatabase.update(ForecastHelper.TABLE_TEMPERATURES,
                values,
                null,
                null);

        return rowsUpdated;
    }

    //delete
    public void deleteAll() {
        mDatabase.delete(
                ForecastHelper.TABLE_TEMPERATURES,
                null,
                null
        );
    }

}
