package com.jaimovie.acube.adapter.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.jaimovie.acube.R;

public class SearchViewHolder extends RecyclerView.ViewHolder {
    public TextView name;

    public SearchViewHolder(View itemView) {
        super(itemView);
        name = (TextView) itemView.findViewById(R.id.section);
    }
}
