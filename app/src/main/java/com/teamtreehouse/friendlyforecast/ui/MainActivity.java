package com.teamtreehouse.friendlyforecast.ui;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.teamtreehouse.friendlyforecast.R;
import com.teamtreehouse.friendlyforecast.db.ForecastDataSource;
import com.teamtreehouse.friendlyforecast.services.Forecast;
import com.teamtreehouse.friendlyforecast.services.ForecastService;

import java.sql.SQLException;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class MainActivity extends Activity {

    public static final String TAG = MainActivity.class.getSimpleName();

    protected ForecastDataSource mDataSource;

    protected Button mInsertButton;
    protected Button mSelectButton;
    protected Button mUpdateButton;
    protected Button mDeleteButton;

    protected double[] mTemperatures;
    protected TextView mHighTextView;
    protected TextView mLowTextView;

    protected long mHighTemp;
    protected long mLowTemp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getActionBar().hide();

        mDataSource = new ForecastDataSource(this);


        mHighTextView = (TextView)findViewById(R.id.textView2);
        mLowTextView = (TextView)findViewById(R.id.textView3);

        mInsertButton = (Button)findViewById(R.id.insertButton);
        mInsertButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadForecastData();
            }
        });

        mSelectButton = (Button)findViewById(R.id.selectButton);
        mSelectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, ViewForecastActivity.class));
            }
        });

        mUpdateButton = (Button)findViewById(R.id.updateButton);
        mUpdateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDataSource.updateTemperature(100);
            }
        });

        mDeleteButton = (Button)findViewById(R.id.deleteButton);
        mDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDataSource.deleteAll();
            }
        });

        TextView photoCredit = (TextView)findViewById(R.id.textView);
        photoCredit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.flickr.com/photos/london/71458818"));
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            mDataSource.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mDataSource.close();
    }

    protected void loadForecastData() {
        ForecastService service = new ForecastService();
        service.loadForecastData(mForecastCallback);
    }

    protected Callback<Forecast> mForecastCallback = new Callback<Forecast>() {
        @Override
        public void success(Forecast forecast, Response response) {
            mTemperatures = new double[forecast.hourly.data.size()];
            for (int i = 0; i < forecast.hourly.data.size(); i++) {
                mTemperatures[i] = forecast.hourly.data.get(i).temperature;
                Log.v(TAG, "Temp " + i + ": " + mTemperatures[i]);
            }

            mDataSource.insertForecast(forecast);
            updateHighAndLow();
            enableOtherButtons();
        }

        @Override
        public void failure(RetrofitError error) {
            Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
        }
    };

    private void updateHighAndLow() {
        // TODO: Query for high temp
        // TODO: Query for low temp
        mHighTextView.setText("Upcoming high: " + mHighTemp);
        mLowTextView.setText("Upcoming low: " + mLowTemp);
    }

    private void resetHighAndLow() {
        mHighTemp = 0;
        mLowTemp = 0;
        mHighTextView.setText("");
        mLowTextView.setText("");
    }

    private void enableOtherButtons() {
        mSelectButton.setEnabled(true);
        mUpdateButton.setEnabled(true);
        mDeleteButton.setEnabled(true);
    }
}
