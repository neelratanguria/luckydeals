package com.solutions.prantae.tuttest;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

public class DealAdapter extends RecyclerView.Adapter<DealAdapter.MyViewHolder> {
    private Context mContext;
    private List<Deals> dealsList;

    public DealAdapter(Context mContext, List<Deals> dealsList)
    {
        this.mContext = mContext;
        this.dealsList = dealsList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.deal_card, viewGroup, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        Deals deals = dealsList.get(i);

        myViewHolder.title.setText(deals.getTitle());
        myViewHolder.code.setText(deals.getCode());
        Glide.with(mContext).load(deals.getImgURL()).into(myViewHolder.thumbnail);
    }

    @Override
    public int getItemCount() {
        return dealsList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView title, code;
        public ImageView thumbnail;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            code = itemView.findViewById(R.id.couponCode);
            thumbnail = itemView.findViewById(R.id.thumbnail);

        }
    }
}
