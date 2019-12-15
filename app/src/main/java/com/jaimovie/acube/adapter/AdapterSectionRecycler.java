package com.jaimovie.acube.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jaimovie.acube.R;
import com.jaimovie.acube.activity.HomeScreen;
import com.jaimovie.acube.adapter.viewholder.SearchChildViewHolder;
import com.jaimovie.acube.adapter.viewholder.SearchViewHolder;
import com.jaimovie.acube.adapter.viewholder.SectionRecyclerViewAdapter;
import com.jaimovie.acube.model.Child;
import com.jaimovie.acube.model.SearchHeader;
import com.jaimovie.acube.utils.ChangeTypeFace;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AdapterSectionRecycler extends SectionRecyclerViewAdapter<SearchHeader, Child, SearchViewHolder, SearchChildViewHolder> {

    private Context context;

    public AdapterSectionRecycler(Context context, List<SearchHeader> sectionHeaderItemList) {
        super(context, sectionHeaderItemList);
        this.context = context;
    }

    @Override
    public SearchViewHolder onCreateSectionViewHolder(ViewGroup sectionViewGroup, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.search_header_item, sectionViewGroup, false);
        return new SearchViewHolder(view);
    }

    @Override
    public SearchChildViewHolder onCreateChildViewHolder(ViewGroup childViewGroup, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.search_child_item, childViewGroup, false);
        return new SearchChildViewHolder(view);
    }

    @Override
    public void onBindSectionViewHolder(SearchViewHolder sectionViewHolder, int sectionPosition, SearchHeader section) {
        sectionViewHolder.name.setText(section.sectionText);
        Log.e("getName---", section.sectionText);
        ChangeTypeFace.overrideMavenBlackFont(context, sectionViewHolder.name);
    }

    @Override
    public void onBindChildViewHolder(SearchChildViewHolder childViewHolder, int sectionPosition, int childPosition, final Child child) {

        childViewHolder.movieTitleTv.setText(child.getName());
        childViewHolder.movieLang.setText(child.getGenre());
        ChangeTypeFace.overrideMavenBoldFont(context, childViewHolder.movieTitleTv);
        ChangeTypeFace.overrideMavenMediumFont(context, childViewHolder.movieLang);

        Picasso.with(context)
                .load(child.getImage())
                .noFade()
                .into(childViewHolder.image, new Callback() {
                    @Override
                    public void onSuccess() {
                    }

                    @Override
                    public void onError() {
                    }
                });

        childViewHolder.llay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String type = child.getType();
                String id = child.getId();
                String videoID = child.getPathurl();
                if (type.equalsIgnoreCase("1")) {
                    ((HomeScreen) context).setMovieDetailsScreen(id, videoID);
                } else {
                    ((HomeScreen) context).setTrailerDetailsScreen(id, videoID);
                }
            }
        });


    }
}
