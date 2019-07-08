package com.bignerdranch.android.mytaxi.presenter.mapPresenter;

import android.location.Location;

import com.bignerdranch.android.mytaxi.networking.mapsNetworking.MapsNetworking;
import com.bignerdranch.android.mytaxi.networking.mapsNetworking.data.Cars;

public class MapsPresenter implements MapsNetworking.OnFinishLoading {
    private MapsView mView;
    private MapsNetworking mModel;

    public MapsPresenter(MapsView view) {
        mView = view;
        mModel = new MapsNetworking(this);
    }

    public void getNearCars(Location loc1, Location loc2){
        mModel.start(loc1,loc2);
    }

    @Override
    public void onLoad(Cars cars) {
        mView.onLoad(cars);
    }

    @Override
    public void onError(String message) {
        mView.onError(message);
    }
}
