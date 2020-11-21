package com.example.travel.ViewHolder;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.travel.Interface.packlistClickListener;
import com.example.travel.R;

public class PacklistViewHolder extends RecyclerView.ViewHolder
        implements View.OnClickListener {

    public TextView itemName;
    public TextView itemQty;
    public packlistClickListener listener;

    public PacklistViewHolder(@NonNull View itemView) {
        super(itemView);
        itemQty = (TextView) itemView.findViewById(R.id.item_qty);
        itemName = (TextView) itemView.findViewById(R.id.item_name);
    }

    public void setItemClickListener(packlistClickListener listener){
        this.listener = listener;
    }
    @Override
    public void onClick(View view) {
        listener.onClick(view, getAdapterPosition(), false);
    }
}
