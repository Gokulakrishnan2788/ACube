package com.jaimovie.acube.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.jaimovie.acube.R;
import com.jaimovie.acube.activity.HomeScreen;
import com.jaimovie.acube.model.BannerList;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

public class SliderAdapter extends PagerAdapter {

    private Context context;
    private List<String> title;
    private List<String> image;
    private List<BannerList> bannerArrList;

    public SliderAdapter(Context context, List<String> image, List<BannerList> bannerArrList) {
        this.context = context;
        this.image = image;
        this.bannerArrList = bannerArrList;
    }

    @Override
    public int getCount() {
        return image.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.item_slider, null);

        ImageView iv =view.findViewById(R.id.iv);

        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = bannerArrList.get(position).getId();
                if(!id.equalsIgnoreCase("0")){
                    ((HomeScreen) context).setMovieDetailsScreen(id, "0");
                }

            }
        });

        Picasso.with(context)
                .load(image.get(position))
                .noFade()
                .into(iv, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {

                    }
                });


        ViewPager viewPager = (ViewPager) container;
        viewPager.addView(view, 0);

        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ViewPager viewPager = (ViewPager) container;
        View view = (View) object;
        viewPager.removeView(view);
    }
}
