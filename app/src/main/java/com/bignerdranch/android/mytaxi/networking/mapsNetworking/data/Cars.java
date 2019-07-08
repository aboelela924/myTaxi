package com.bignerdranch.android.mytaxi.networking.mapsNetworking.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Cars {
    @SerializedName("poiList")
    @Expose
    private List<PoiList> poiList = null;

    public List<PoiList> getPoiList() {
        return poiList;
    }

    public void setPoiList(List<PoiList> poiList) {
        this.poiList = poiList;
    }
}
