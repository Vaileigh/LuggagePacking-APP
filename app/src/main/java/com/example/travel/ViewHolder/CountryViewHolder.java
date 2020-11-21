package com.example.travel.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.travel.Interface.countryClickListener;
import com.example.travel.R;

public class CountryViewHolder extends RecyclerView.ViewHolder
        implements View.OnClickListener {

    public TextView countryName;
    public ImageView countryImage;
    public countryClickListener listener;

    public CountryViewHolder(@NonNull View itemView) {
        super(itemView);
        countryImage = (ImageView) itemView.findViewById(R.id.country_image);
        countryName = (TextView) itemView.findViewById(R.id.country_name);
    }

    public void setItemClickListener(countryClickListener listener){
        this.listener = listener;
    }
    @Override
    public void onClick(View view) {
        listener.onClick(view, getAdapterPosition(), false);
    }
}
