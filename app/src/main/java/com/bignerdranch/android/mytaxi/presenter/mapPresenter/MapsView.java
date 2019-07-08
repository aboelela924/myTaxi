package com.bignerdranch.android.mytaxi.presenter.mapPresenter;

import com.bignerdranch.android.mytaxi.networking.mapsNetworking.data.Cars;

public interface MapsView {
    void onLoad(Cars cars);
    void onError(String message);
}
