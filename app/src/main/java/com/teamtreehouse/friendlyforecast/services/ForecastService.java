package com.teamtreehouse.friendlyforecast.services;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.http.GET;
import retrofit.http.Path;

public class ForecastService {

    private static final String API_URL = "https://api.forecast.io/";

    /*
     * Define a service for getting forecast information using Retrofit by Square
     */
    public interface WeatherService {
        @GET("/forecast/{key}/{latitude},{longitude}")
        public void getForecastAsync(
                @Path("key") String key,
                @Path("latitude") String lat,
                @Path("longitude") String longitude,
                Callback<Forecast> callback
        );
    }

    /*
     * Create an async call to the forecast service
     */
    public void loadForecastData(Callback<Forecast> callback) {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(API_URL)
                .build();

        WeatherService service = restAdapter.create(WeatherService.class);
        service.getForecastAsync("b8de9dd4616fcbd7f77b46ef059bc304", "28.4158", "-81.2989", callback);
    }

}
