package com.jaimovie.acube.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.jaimovie.acube.R;
import com.jaimovie.acube.activity.HomeScreen;
import com.jaimovie.acube.adapter.DashboardListAdapter;
import com.jaimovie.acube.adapter.DashboardTrailerAdapter;
import com.jaimovie.acube.adapter.SliderAdapter;
import com.jaimovie.acube.model.BannerList;
import com.jaimovie.acube.model.IResult;
import com.jaimovie.acube.model.MovieList;
import com.jaimovie.acube.model.TrailersList;
import com.jaimovie.acube.network.VolleyService;
import com.jaimovie.acube.utils.ChangeTypeFace;
import com.jaimovie.acube.utils.Utility;
import com.jaimovie.acube.view.AutoScrollViewPager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.jaimovie.acube.constants.AppConfig.BASE;
import static com.jaimovie.acube.constants.ErrorMsg.SOMETHING_WENT_WORNG;
import static com.jaimovie.acube.utils.Utility.dismissProgDialog;
import static com.jaimovie.acube.utils.Utility.showProgDialog;

public class DashboardFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = DashboardFragment.class.getSimpleName();
    private AutoScrollViewPager viewPager;

    private ArrayList<String> bannerImages;
    private Activity mActivity;
    private LinearLayout totalLayout;
    private RecyclerView trailerList, movieList;
    private DashboardListAdapter movieAdapter;
    private DashboardTrailerAdapter trailerAdapter;
    private IResult mResultCallback = null;
    private VolleyService mVolleyService;

    private List<MovieList> movieArrList;
    private List<TrailersList> trailersArrList;
    private List<BannerList> bannerArrList;
    private TextView trailerViewAll, movieViewAll;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        mActivity = getActivity();
        viewPager = view.findViewById(R.id.pager);
        totalLayout = view.findViewById(R.id.totalLayout);
        trailerList = view.findViewById(R.id.trailersList);
        movieList = view.findViewById(R.id.upcomingList);
        trailerViewAll = view.findViewById(R.id.trailerViewAll);
        movieViewAll = view.findViewById(R.id.movieViewAll);

        trailerViewAll.setOnClickListener(this);
        movieViewAll.setOnClickListener(this);

        movieArrList = new ArrayList<>();
        trailersArrList = new ArrayList<>();
        bannerArrList = new ArrayList<>();
        initVolleyCallback();
        mVolleyService = new VolleyService(mResultCallback, mActivity);

        showProgDialog(mActivity, "Please wait..");
        Map<String, String> map = new HashMap<>();
        map.put("currencycode", "IND");
        map.put("limit", "5");
        map.put("tag", "dashboardinfo");
        mVolleyService.postVolleyData("POST", BASE, map);

        GridLayoutManager gridLayout = new GridLayoutManager(getActivity(), 1, GridLayoutManager.HORIZONTAL, false);
        trailerList.setLayoutManager(gridLayout);

        GridLayoutManager gridLayout1 = new GridLayoutManager(getActivity(), 1, GridLayoutManager.HORIZONTAL, false);
        movieList.setLayoutManager(gridLayout1);

        ChangeTypeFace.overrideMavenBoldFont(mActivity, totalLayout);
        ChangeTypeFace.overrideMavenRegularFont(mActivity, trailerViewAll);
        ChangeTypeFace.overrideMavenRegularFont(mActivity, movieViewAll);

        return view;
    }

    void initVolleyCallback() {
        mResultCallback = new IResult() {
            @Override
            public void notifyJsonSuccess(String requestType, JSONObject response) {
                dismissProgDialog();

            }

            @Override
            public void notifyJsonSuccess(String requestType, String response, String type) {
                try {
                    Log.e(TAG, "---" + response);
                    JSONObject json = new JSONObject(response);
                    int success = json.getInt("success");
                    String errorMessage = json.getString("error_message");
                    if (success == 200) {
                        movieArrList = new ArrayList<>();

                        JSONArray packageResp = json.getJSONArray("response");
                        if (packageResp.length() > 0) {
                            JSONObject bannerObj = packageResp.getJSONObject(0);

                            JSONArray bannerArr = bannerObj.getJSONArray("banners");
                            JSONArray trailersArr = bannerObj.getJSONArray("trailers");
                            JSONArray moviesArr = bannerObj.getJSONArray("movies");

                            bannerImages = new ArrayList<>();
                            for (int i = 0; i < bannerArr.length(); i++) {
                                JSONObject tempObj = bannerArr.getJSONObject(i);
                                BannerList tempPackages = new BannerList();
                                tempPackages.setId(tempObj.getString("movieid"));
                                tempPackages.setImage(tempObj.getString("bannerimage"));
                                bannerArrList.add(tempPackages);
                                bannerImages.add(tempObj.getString("bannerimage"));
                            }

                            for (int i = 0; i < trailersArr.length(); i++) {
                                JSONObject tempObj = trailersArr.getJSONObject(i);
                                TrailersList tempPackages = new TrailersList();
                                tempPackages.setId(tempObj.getString("trailerid"));
                                tempPackages.setName(tempObj.getString("title"));
                                tempPackages.setImage(tempObj.getString("trailerimage"));
                                tempPackages.setPathurl(tempObj.getString("pathurl"));
                                tempPackages.setGenre(tempObj.getString("genre"));
                                trailersArrList.add(tempPackages);
                            }
                            for (int i = 0; i < moviesArr.length(); i++) {
                                JSONObject tempObj = moviesArr.getJSONObject(i);
                                MovieList tempPackages = new MovieList();
                                tempPackages.setId(tempObj.getString("movieid"));
                                tempPackages.setName(tempObj.getString("title"));
                                tempPackages.setImage(tempObj.getString("movieimage"));
                                tempPackages.setPathurl(tempObj.getString("pathurl"));
                                tempPackages.setGenre(tempObj.getString("genre"));
                                movieArrList.add(tempPackages);
                            }
                        }

                        trailerAdapter = new DashboardTrailerAdapter(trailersArrList, mActivity);
                        trailerList.setAdapter(trailerAdapter);

                        movieAdapter = new DashboardListAdapter(movieArrList, mActivity);
                        movieList.setAdapter(movieAdapter);

                        viewPager.setAdapter(new SliderAdapter(mActivity, bannerImages, bannerArrList));
                        new Handler().postDelayed(new Runnable() {

                            @Override
                            public void run() {
                                // This method will be executed once the timer is over
                                // Start your app main activity
                                viewPager.startAutoScroll();
                                viewPager.setInterval(3000);
                                viewPager.setDirection(AutoScrollViewPager.RIGHT);
                            }
                        }, 5000);
                        totalLayout.setVisibility(View.VISIBLE);
                        dismissProgDialog();
                    } else {
                        dismissProgDialog();
                        Utility.showFailureToast(mActivity, errorMessage);
                    }
                } catch (JSONException e) {
                    dismissProgDialog();
                    Utility.showToast(mActivity, SOMETHING_WENT_WORNG);
                }
                dismissProgDialog();
            }

            @Override
            public void notifyJsonSuccess(String requestType, JSONObject response, String serviceName) {
                dismissProgDialog();
                try {
                    Log.e(TAG, " response ==>" + "" + response);
                    int success = response.getInt("success");
                    String errorMessage = response.getString("error_message");
                    String cart_items = response.getString("cart_items");
                    Utility.showFailureToast(mActivity, errorMessage);
                    Log.e(TAG, " Volley ==>" + "" + success);
                } catch (JSONException e) {
                    Utility.showToast(mActivity, SOMETHING_WENT_WORNG);
                    Log.e(TAG, " Volley ==>" + "" + e.toString());
                }
            }

            @Override
            public void notifyError(String requestType, VolleyError error) {
                dismissProgDialog();
                Log.e(TAG, " Volley error==>" + "" + error);
            }

            @Override
            public void notifyError(String requestType, String error) {
                dismissProgDialog();
                Utility.showFailureToast(mActivity, error);
                Log.e(TAG, " Volley requestType==>" + "" + error);

            }
        };
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.trailerViewAll:
                ((HomeScreen) mActivity).setTrailersScreen();
                break;

            case R.id.movieViewAll:
                ((HomeScreen) mActivity).setAllMoviesScreen();
                break;
        }
    }
}
