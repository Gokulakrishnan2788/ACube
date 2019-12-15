package com.jaimovie.acube.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.iamtheib.infiniterecyclerview.InfiniteAdapter;
import com.jaimovie.acube.R;
import com.jaimovie.acube.activity.HomeScreen;
import com.jaimovie.acube.adapter.viewholder.LoadingViewHolder;
import com.jaimovie.acube.adapter.viewholder.MovieViewHolder;
import com.jaimovie.acube.model.MovieList;
import com.jaimovie.acube.utils.ChangeTypeFace;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MovieInfinityAdapter extends InfiniteAdapter<RecyclerView.ViewHolder> {

    private List<MovieList> getCategoryList;
    private Activity mContext;

    public MovieInfinityAdapter(Activity context, List<MovieList> dummyData) {
        mContext = context;
        getCategoryList = dummyData;
    }

    @Override
    public RecyclerView.ViewHolder getLoadingViewHolder(ViewGroup parent) {
        View loadingView = LayoutInflater.from(mContext).inflate(R.layout.list_loading_view, parent, false);
        return new LoadingViewHolder(loadingView);
    }

    @Override
    public int getCount() {
        return getCategoryList.size();
    }

    @Override
    public int getViewType(int position) {
        return 1;
    }

    @Override
    public RecyclerView.ViewHolder onCreateView(ViewGroup parent, int viewType) {
        View dummyView = LayoutInflater.from(mContext).inflate(R.layout.movies_list_item, parent, false);
        return new MovieViewHolder(dummyView);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof LoadingViewHolder) {
            LoadingViewHolder loadingViewHolder = (LoadingViewHolder) holder;

            loadingViewHolder.progressBar.setVisibility(View.GONE);
            /*ObjectAnimator animator = ObjectAnimator.ofFloat(loadingViewHolder.loadingImage, "rotation", 0, 360);
            animator.setRepeatCount(ValueAnimator.INFINITE);
            animator.setInterpolator(new LinearInterpolator());
            animator.setDuration(1000);
            animator.start();*/
            return;
        } else {
            MovieViewHolder movieHolder = (MovieViewHolder) holder;
            movieHolder.movieTitleTv.setText(getCategoryList.get(position).getName());
            movieHolder.movieLang.setText(getCategoryList.get(position).getGenre());

            ChangeTypeFace.overrideMavenBoldFont(mContext, movieHolder.movieTitleTv);
            ChangeTypeFace.overrideMavenMediumFont(mContext, movieHolder.movieLang);

            Picasso.with(mContext)
                    .load(getCategoryList.get(position).getImage())
                    .noFade()
                    .into(movieHolder.image, new Callback() {
                        @Override
                        public void onSuccess() {
                        }

                        @Override
                        public void onError() {
                        }
                    });


            movieHolder.llay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String id = getCategoryList.get(position).getId();
                    String videoID = getCategoryList.get(position).getPathurl();
                    ((HomeScreen) mContext).setMovieDetailsScreen(id, videoID);
                }
            });
        }

        super.onBindViewHolder(holder, position);
    }

    @Override
    public int getVisibleThreshold() {
        return 2;
    }
}
