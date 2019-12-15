package com.jaimovie.acube.adapter;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jaimovie.acube.R;
import com.jaimovie.acube.activity.HomeScreen;
import com.jaimovie.acube.model.TrailersList;
import com.jaimovie.acube.utils.ChangeTypeFace;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

public class TrailerListAdapter extends RecyclerView.Adapter<TrailerListAdapter.ImageViewHolder> {

    private Activity mActivity;
    private Dialog dialog;
    private List<TrailersList> getCategoryList;

    public TrailerListAdapter(List<TrailersList> getCategoryList, Activity animalItemClickListener) {
        this.getCategoryList = getCategoryList;
        this.mActivity = animalItemClickListener;
    }

    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ImageViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.movies_list_item, parent, false));
    }

    @Override
    public int getItemCount() {
        return getCategoryList.size();
    }

    @Override
    public void onBindViewHolder(final ImageViewHolder holder, final int position) {

        holder.movieTitleTv.setText(getCategoryList.get(position).getName());

        ChangeTypeFace.overrideMavenBoldFont(mActivity, holder.movieTitleTv);
        ChangeTypeFace.overrideMavenMediumFont(mActivity, holder.movieLang);
        Picasso.with(mActivity)
                .load(getCategoryList.get(position).getImage())
                .noFade()
                .into(holder.image, new Callback() {
                    @Override
                    public void onSuccess() {
                    }

                    @Override
                    public void onError() {

                    }
                });
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder {

        private ImageView image;
        private TextView movieLang, movieTitleTv;
        LinearLayout llay;

        public ImageViewHolder(View itemView) {
            super(itemView);

            movieTitleTv = itemView.findViewById(R.id.movieTitleTv);
            llay = itemView.findViewById(R.id.llay);
            image = itemView.findViewById(R.id.movieIv);
            movieLang = itemView.findViewById(R.id.movieLangTv);

            itemView.setOnClickListener(new View.OnClickListener() {
                @TargetApi(Build.VERSION_CODES.LOLLIPOP)
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    String id = getCategoryList.get(pos).getId();
                    String videoID = getCategoryList.get(pos).getPathurl();
                    ((HomeScreen) mActivity).setTrailerDetailsScreen(id, videoID);
                }
            });
        }
    }


}