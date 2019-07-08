package com.bignerdranch.android.mytaxi.networking.mapsNetworking;

import com.bignerdranch.android.mytaxi.networking.mapsNetworking.data.Cars;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;


public interface MapsAPi {
    @GET("/")
    Call<Cars> getCarsAround(@Query("p1Lat") double lat1,
                             @Query("p1Lon") double lon1,
                             @Query("p2Lat") double lat2,
                             @Query("p2Lon") double lon2);
}
