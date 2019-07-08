package com.bignerdranch.android.mytaxi;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RotateDrawable;
import android.location.Location;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.bignerdranch.android.mytaxi.networking.mapsNetworking.data.Cars;
import com.bignerdranch.android.mytaxi.networking.mapsNetworking.data.PoiList;
import com.bignerdranch.android.mytaxi.presenter.mapPresenter.MapsPresenter;
import com.bignerdranch.android.mytaxi.presenter.mapPresenter.MapsView;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, MapsView {

    private static final int LOCATION_PERMISION_CONSTANT = 0;
    private static final String TAG = "MapsActivity";

    private GoogleMap mMap;
    private GoogleApiClient mClient;
    private Location mCurrentLocation;
    private static final String[] LOCATION_PERMISSIONS = new String[]{
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
    };
    private MapsPresenter mPresenter;
    private LocationCallback mLocationCallback;
    private List<PoiList> mAdapterData = new ArrayList<>();
    private List<PoiList> mAllPoiList = new ArrayList<>();
    private BottomSheetBehavior mSheetBehavior;
    private LinearLayout mBottomSheet;
    private RadioGroup mRadioGroup;
    private BottomSheetAdapter mAdapter;
    private RadioButton mAllRadioButton, mTaxiRadioButton, mPoolingRadioButtoln;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mPresenter = new MapsPresenter(this);
        mLocationCallback = new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                BitmapDescriptor icon = BitmapDescriptorFactory.fromBitmap(resize(getResources().getDrawable(R.drawable.map_taxi)));
                Location lastLocation = locationResult.getLastLocation();
                Location upper = calculateUpper(lastLocation);
                Location lower = calculateLower(lastLocation);

                mPresenter.getNearCars(upper,lower);
                LatLng currentLocation = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15));

                RecyclerView bottomSheetRecyclerView = findViewById(R.id.bottom_sheet_recycler_view);
                bottomSheetRecyclerView.setLayoutManager(
                        new LinearLayoutManager(
                                MapsActivity.this,
                                LinearLayoutManager.HORIZONTAL,
                                false));
                mAdapter = new BottomSheetAdapter(MapsActivity.this, mAdapterData, mMap);
                bottomSheetRecyclerView.setAdapter(mAdapter);

                mRadioGroup = findViewById(R.id.car_types_radio_group);

                mAllRadioButton = findViewById(R.id.all);
                mAllRadioButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mAdapterData.clear();
                        mAdapterData.addAll(mAllPoiList);
                        mAdapter.notifyDataSetChanged();
                    }
                });
                mTaxiRadioButton = findViewById(R.id.taxi);
                mTaxiRadioButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        List<PoiList> taxis = new ArrayList<>();
                        for (PoiList poiList: mAllPoiList) {
                            if(poiList.getFleetType().equals("TAXI")){
                                taxis.add(poiList);
                            }
                        }
                        mAdapterData.clear();
                        mAdapterData.addAll(taxis);
                        mAdapter.notifyDataSetChanged();
                    }
                });
                mPoolingRadioButtoln = findViewById(R.id.pooling);
                mPoolingRadioButtoln.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        List<PoiList> poolings = new ArrayList<>();
                        for (PoiList poiList: mAllPoiList) {
                            if(!poiList.getFleetType().equals("TAXI")){
                                poolings.add(poiList);
                            }
                        }
                        mAdapterData.clear();
                        mAdapterData.addAll(poolings);
                        mAdapter.notifyDataSetChanged();
                    }
                });

                mBottomSheet = findViewById(R.id.bottom_sheet);
                mSheetBehavior = BottomSheetBehavior.from(mBottomSheet);
                mSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
                    @Override
                    public void onStateChanged(@NonNull View view, int newState) {
                        switch (newState) {
                            case BottomSheetBehavior.STATE_HIDDEN:
                                break;
                            case BottomSheetBehavior.STATE_EXPANDED: {

                            }
                            break;
                            case BottomSheetBehavior.STATE_COLLAPSED: {

                            }
                            break;
                            case BottomSheetBehavior.STATE_DRAGGING:
                                break;
                            case BottomSheetBehavior.STATE_SETTLING:
                                break;
                        }
                    }

                    @Override
                    public void onSlide(@NonNull View view, float v) {

                    }
                });
            }

            @Override
            public void onLocationAvailability(LocationAvailability locationAvailability) {
                super.onLocationAvailability(locationAvailability);
            }
        };

        mClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .build();

        if(checkForLocationPermission()){
            createLocationRequest();
        }else{
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(LOCATION_PERMISSIONS,LOCATION_PERMISION_CONSTANT);
            }
        }
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode){
            case LOCATION_PERMISION_CONSTANT:

            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mClient.disconnect();
    }

    private boolean checkForLocationPermission(){
        int result = ContextCompat
                .checkSelfPermission(this, LOCATION_PERMISSIONS[0]);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    public void createLocationRequest() throws SecurityException{
        LocationRequest request = LocationRequest.create();
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        request.setNumUpdates(1);
        request.setInterval(0);
        LocationServices
                .getFusedLocationProviderClient(this)
                .requestLocationUpdates(request,mLocationCallback, null);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }

    private Bitmap resize(Drawable image) {
        Bitmap b = ((BitmapDrawable)image).getBitmap();
        Bitmap bitmapResized = Bitmap.createScaledBitmap(b, 150, 150, false);
        return bitmapResized;
    }

    private Location calculateUpper(Location currentLocation){
        double newLat = currentLocation.getLatitude() + (-1*Math.sqrt(2)/(2*6378)) * (180/Math.PI);
        double newLng = currentLocation.getLongitude()
                +(-1*Math.sqrt(2)/(2*6378))
                * (180/Math.PI)
                / Math.cos(currentLocation.getLatitude()*(180/Math.PI));
        Location location = new Location("");
        location.setLatitude(newLat);
        location.setLongitude(newLng);
        return location;
    }

    private Location calculateLower(Location currentLocation){
        double newLat = currentLocation.getLatitude() + (Math.sqrt(2)/(2*6378)) * (180/Math.PI);
        double newLng = currentLocation.getLongitude()
                +(Math.sqrt(2)/(2*6378))
                * (180/Math.PI)
                / Math.cos(currentLocation.getLatitude()*(180/Math.PI));
        Location location = new Location("");
        location.setLatitude(newLat);
        location.setLongitude(newLng);
        return location;
    }

    @Override
    public void onLoad(Cars cars) {
        final List<PoiList> poiList = cars.getPoiList();
        mAllPoiList.addAll(poiList);
        mMap.clear();
        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                PoiList temp = new PoiList();
                temp.setId(Integer.parseInt(marker.getTitle()));
                int index = poiList.indexOf(temp);
                PoiList temp2 = poiList.get(index);
                LayoutInflater inflater = LayoutInflater.from(MapsActivity.this);
                View v = inflater.inflate(R.layout.marker_info_layout, null);
                TextView idTextView = v.findViewById(R.id.car_id_text_view);
                TextView typeTextView = v.findViewById(R.id.car_type_text_view);
                ImageView typeImageView = v.findViewById(R.id.type_image_view);
                idTextView.setText(marker.getTitle());
                typeTextView.setText(marker.getSnippet());
                if(temp2.getFleetType().equals("TAXI")){
                    typeImageView.setImageDrawable(getResources().getDrawable(R.drawable.map_taxi));
                }else{
                    typeImageView.setImageDrawable(getResources().getDrawable(R.drawable.map_car));
                }
                return v;
            }

            @Override
            public View getInfoContents(Marker marker) {
                return null;
            }
        });
        for (PoiList car: poiList){
            BitmapDescriptor icon;
            if(car.getFleetType().equals("TAXI")){
                Bitmap original = resize(getResources().getDrawable(R.drawable.map_taxi));
                Matrix matrix = new Matrix();
                matrix.postRotate(Float.valueOf(String.valueOf(car.getHeading())));
                Bitmap rotated = Bitmap.createBitmap(original, 0, 0, original.getWidth(), original.getHeight(), matrix, true);
                icon = BitmapDescriptorFactory.fromBitmap(rotated);
            }else{
                Bitmap original = resize(getResources().getDrawable(R.drawable.map_car));
                Matrix matrix = new Matrix();
                matrix.postRotate(Float.valueOf(String.valueOf(car.getHeading())));
                Bitmap rotated = Bitmap.createBitmap(original, 0, 0, original.getWidth(), original.getHeight(), matrix, true);
                icon = BitmapDescriptorFactory.fromBitmap(rotated);
            }

            LatLng latLng = new LatLng(car.getCoordinate().getLatitude(), car.getCoordinate().getLongitude());
            Marker marker = mMap.addMarker(new MarkerOptions().position(latLng).title(String.valueOf(car.getId())).icon(icon).flat(true));
            marker.setSnippet(car.getFleetType());
        }
    }

    @Override
    public void onError(String message) {

    }
}
