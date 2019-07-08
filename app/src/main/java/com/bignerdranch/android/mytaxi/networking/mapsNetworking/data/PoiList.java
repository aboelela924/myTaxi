package com.bignerdranch.android.mytaxi.networking.mapsNetworking.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PoiList {
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("coordinate")
    @Expose
    private Coordinate coordinate;
    @SerializedName("fleetType")
    @Expose
    private String fleetType;
    @SerializedName("heading")
    @Expose
    private Double heading;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Coordinate getCoordinate() {
        return coordinate;
    }

    public void setCoordinate(Coordinate coordinate) {
        this.coordinate = coordinate;
    }

    public String getFleetType() {
        return fleetType;
    }

    public void setFleetType(String fleetType) {
        this.fleetType = fleetType;
    }

    public Double getHeading() {
        return heading;
    }

    public void setHeading(Double heading) {
        this.heading = heading;
    }

    @Override
    public boolean equals( Object obj) {
        PoiList poi = (PoiList) obj;
        boolean no = this.id.intValue() == poi.id.intValue();
        return no;
    }
}
