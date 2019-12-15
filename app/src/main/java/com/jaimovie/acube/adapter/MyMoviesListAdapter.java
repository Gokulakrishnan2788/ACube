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
import com.jaimovie.acube.model.WatchLaterList;
import com.jaimovie.acube.utils.ChangeTypeFace;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MyMoviesListAdapter extends RecyclerView.Adapter<MyMoviesListAdapter.ImageViewHolder> {

    private Activity mActivity;
    private Dialog dialog;
    private List<WatchLaterList> getCategoryList;

    public MyMoviesListAdapter(List<WatchLaterList> getCategoryList, Activity animalItemClickListener) {
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

        holder.movieTitleTv.setText(getCategoryList.get(position).getTitle());
        holder.movieLang.setText(getCategoryList.get(position).getGenre());

        ChangeTypeFace.overrideMavenBoldFont(mActivity, holder.movieTitleTv);
        ChangeTypeFace.overrideMavenMediumFont(mActivity, holder.movieLang);
        Picasso.with(mActivity)
                .load(getCategoryList.get(position).getMovieimage())
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
                    String id = getCategoryList.get(pos).getMovieid();
                    String videoID = getCategoryList.get(pos).getPathurl();
                    ((HomeScreen) mActivity).setMovieDetailsScreen(id, videoID);
                }
            });
        }
    }


}