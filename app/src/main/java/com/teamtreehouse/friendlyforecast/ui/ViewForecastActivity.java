package com.teamtreehouse.friendlyforecast.ui;

import android.app.ActionBar;
import android.app.ListActivity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;

import com.teamtreehouse.friendlyforecast.R;
import com.teamtreehouse.friendlyforecast.db.ForecastDataSource;
import com.teamtreehouse.friendlyforecast.db.ForecastHelper;

import java.math.BigDecimal;
import java.math.MathContext;
import java.sql.SQLException;
import java.util.ArrayList;


public class ViewForecastActivity extends ListActivity {

    protected ForecastDataSource mDataSource;
    protected ArrayList<BigDecimal> mTemperatures;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_forecast);

        configureActionBar();

        mDataSource = new ForecastDataSource(this);
        mTemperatures = new ArrayList<BigDecimal>();
    }

    @Override
    protected void onResume() {
        super.onResume();

        try {
            mDataSource.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        Cursor cursor = mDataSource.selectAllTemperatures();
        updateList(cursor);
    }

    @Override
    protected void onPause() {
        super.onPause();

        mDataSource.close();
    }

    protected void updateList(Cursor cursor) {
        mTemperatures.clear();

        cursor.moveToFirst();

        while(! cursor.isAfterLast()){
            //do stuff
            int i = cursor.getColumnIndex(ForecastHelper.COL_TEMPERATURE);
            double temperature = cursor.getDouble(i);

            mTemperatures.add(new BigDecimal(temperature, MathContext.DECIMAL32));

            cursor.moveToNext();
        }

        ArrayAdapter<BigDecimal> adapter = new ArrayAdapter<BigDecimal>(ViewForecastActivity.this,
                android.R.layout.simple_list_item_1,
                mTemperatures);

        setListAdapter(adapter);
    }

    protected void filterTemperatures(String minTemp) {

        Cursor cursor = mDataSource.selectTempsGreaterThan(minTemp);
        updateList(cursor);
    }

    protected void configureActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setCustomView(R.layout.ab_filter);

        final EditText minTempField = (EditText) actionBar.getCustomView().findViewById(R.id.minTempField);
        minTempField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                filterTemperatures(minTempField.getText().toString());
                return false;
            }
        });

        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_SHOW_HOME);
    }
}
