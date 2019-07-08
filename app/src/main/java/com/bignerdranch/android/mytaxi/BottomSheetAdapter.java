package com.bignerdranch.android.mytaxi;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bignerdranch.android.mytaxi.networking.mapsNetworking.data.PoiList;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;

public class BottomSheetAdapter extends RecyclerView.Adapter<BottomSheetAdapter.BottomSheetViewHolder> {
    private Context mContext;
    private List<PoiList> mPoiLists;
    private GoogleMap mMap;

    public BottomSheetAdapter(Context context, List<PoiList> poiLists, GoogleMap map) {
        mContext = context;
        mPoiLists = poiLists;
        mMap = map;
    }

    @NonNull
    @Override
    public BottomSheetViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View v = inflater.inflate(R.layout.marker_info_layout,viewGroup, false);
        return new BottomSheetViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull BottomSheetViewHolder bottomSheetViewHolder, int i) {
        bottomSheetViewHolder.bind(mPoiLists.get(i));
    }

    @Override
    public int getItemCount() {
        return mPoiLists.size();
    }


    class BottomSheetViewHolder extends RecyclerView.ViewHolder{
        private ImageView mTypeImageView;
        private TextView mIdTextView;
        private TextView mTitleTextView;

        public BottomSheetViewHolder(@NonNull View itemView) {
            super(itemView);
            mTypeImageView = itemView.findViewById(R.id.type_image_view);
            mIdTextView = itemView.findViewById(R.id.car_id_text_view);
            mTitleTextView = itemView.findViewById(R.id.car_type_text_view);

        }

        public void bind(final PoiList poiList){
            mTitleTextView.setText(poiList.getFleetType());
            mIdTextView.setText(String.valueOf(poiList.getId()));
            if(poiList.getFleetType().equals("TAXI")){
                mTypeImageView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.map_taxi));
            }else{
                mTypeImageView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.map_car));
            }

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    LatLng latLng = new LatLng(poiList.getCoordinate().getLatitude(), poiList.getCoordinate().getLongitude());
                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 20);
                    mMap.animateCamera(cameraUpdate);
                }
            });
        }
    }
}
