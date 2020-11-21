package com.example.travel.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.travel.Interface.locationClickListener;
import com.example.travel.R;

public class LocationViewHolder extends RecyclerView.ViewHolder
        implements View.OnClickListener {

    public TextView locationName;
    public ImageView locationImage;
    public locationClickListener listener;

    public LocationViewHolder(@NonNull View itemView) {
        super(itemView);
        locationImage = (ImageView) itemView.findViewById(R.id.location_image);
        locationName = (TextView) itemView.findViewById(R.id.item_name);
    }

    public void setItemClickListener(locationClickListener listener){
        this.listener = listener;
    }
    @Override
    public void onClick(View view) {
        listener.onClick(view, getAdapterPosition(), false);
    }
}
