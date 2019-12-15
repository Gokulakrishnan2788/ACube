package com.jaimovie.acube.adapter.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.jaimovie.acube.R;
import com.singh.daman.proprogressviews.DottedArcProgress;


public class LoadingViewHolder extends RecyclerView.ViewHolder {

    public DottedArcProgress progressBar;

    public LoadingViewHolder(View itemView) {
        super(itemView);
        progressBar =  itemView.findViewById(R.id.progressBar);
    }
}

