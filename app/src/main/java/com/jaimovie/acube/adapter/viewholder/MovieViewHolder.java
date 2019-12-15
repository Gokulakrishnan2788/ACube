package com.jaimovie.acube.adapter.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jaimovie.acube.R;

public class MovieViewHolder extends RecyclerView.ViewHolder {

    public ImageView image;
    public TextView movieLang, movieTitleTv;
    public LinearLayout llay;

    public MovieViewHolder(View itemView) {
        super(itemView);
        movieTitleTv = itemView.findViewById(R.id.movieTitleTv);
        llay = itemView.findViewById(R.id.llay);
        image = itemView.findViewById(R.id.movieIv);
        movieLang = itemView.findViewById(R.id.movieLangTv);
    }
}
