package com.bignerdranch.android.mytaxi.networking.mapsNetworking;

import android.location.Location;

import com.bignerdranch.android.mytaxi.networking.mapsNetworking.data.Cars;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MapsNetworking implements Callback<Cars> {
    private static final String BASE_URL = "https://fake-poi-api.mytaxi.com";

    public interface OnFinishLoading{
        void onLoad(Cars cars);
        void onError(String message);
    }

    private OnFinishLoading mPresenter;

    public MapsNetworking(OnFinishLoading presenter) {
        mPresenter = presenter;
    }

    public void start(Location loc1, Location loc2){
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        MapsAPi mapsAPi = retrofit.create(MapsAPi.class);
        Call<Cars> call = mapsAPi.getCarsAround(
                loc1.getLatitude(),
                loc1.getLongitude(),
                loc2.getLatitude(),
                loc2.getLongitude());
        call.enqueue(this);
    }

    @Override
    public void onResponse(Call<Cars> call, Response<Cars> response) {
        if(response.isSuccessful()){
            mPresenter.onLoad(response.body());
        }else{
            mPresenter.onError(response.errorBody().toString());
        }
    }

    @Override
    public void onFailure(Call<Cars> call, Throwable t) {
        mPresenter.onError(t.getMessage());
    }
}
