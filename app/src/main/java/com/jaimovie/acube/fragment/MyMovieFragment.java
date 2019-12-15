package com.jaimovie.acube.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.jaimovie.acube.R;
import com.jaimovie.acube.activity.HomeScreen;
import com.jaimovie.acube.adapter.MyMoviesListAdapter;
import com.jaimovie.acube.model.IResult;
import com.jaimovie.acube.model.WatchLaterList;
import com.jaimovie.acube.network.VolleyService;
import com.jaimovie.acube.utils.ChangeTypeFace;
import com.jaimovie.acube.utils.Utility;
import com.zookey.universalpreferences.UniversalPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.jaimovie.acube.constants.AppConfig.BASE;
import static com.jaimovie.acube.constants.ErrorMsg.SOMETHING_WENT_WORNG;
import static com.jaimovie.acube.constants.PreferenceName.PREFS_USERID;
import static com.jaimovie.acube.utils.Utility.dismissProgDialog;
import static com.jaimovie.acube.utils.Utility.showProgDialog;

public class MyMovieFragment extends Fragment {

    private static final String TAG = MyMovieFragment.class.getSimpleName();
    private RecyclerView allMoviesList;
    private Activity mActivity;
    private IResult mResultCallback = null;
    private VolleyService mVolleyService;
    private List<WatchLaterList> trailerArrList;
    private MyMoviesListAdapter categoryAdapter;
    private LinearLayout totalLayout;
    private TextView labelTv, noDataTv;
    private ImageView filterTv;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_movies, container, false);

        mActivity = getActivity();
        allMoviesList = view.findViewById(R.id.upcomingList);
        totalLayout = view.findViewById(R.id.totalLayout);
        labelTv = view.findViewById(R.id.labelTv);
        filterTv = view.findViewById(R.id.filterTv);
        noDataTv = view.findViewById(R.id.noDataTv);

        labelTv.setText("My Movies");
        ChangeTypeFace.overrideMavenBoldFont(mActivity, totalLayout);

        filterTv.setVisibility(View.GONE);
        labelTv.setVisibility(View.GONE);

        final String userId = UniversalPreferences.getInstance().get(PREFS_USERID, "");

        trailerArrList = new ArrayList<>();
        initVolleyCallback();
        showProgDialog(mActivity, "Please wait..");
        mVolleyService = new VolleyService(mResultCallback, mActivity);
        Map<String, String> map = new HashMap<String, String>();
        map.put("currencycode", "inr");
        map.put("userid", userId);
        map.put("tag", "mymovie");
        mVolleyService.postVolleyDataWithAccessToken("POST", BASE, map, "");

        GridLayoutManager gridLayout = new GridLayoutManager(getActivity(), 2, GridLayoutManager.VERTICAL, false);
        allMoviesList.setLayoutManager(gridLayout);
        allMoviesList.setLayoutManager(gridLayout);

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
                dismissProgDialog();
                Log.e(TAG, " requestType ==>" + "" + response);
                try {
                    JSONObject json = new JSONObject(response);
                    int success = json.getInt("success");
                    String errorMessage = json.getString("error_message");
                    if (success == 200) {
                        trailerArrList = new ArrayList<>();

                        JSONArray packageResp = json.getJSONArray("response");
                        if (packageResp.length() > 0) {
                            for (int i = 0; i < packageResp.length(); i++) {
                                JSONObject tempObj = packageResp.getJSONObject(i);
                                WatchLaterList tempPackages = new WatchLaterList();
                                tempPackages.setMovieid(tempObj.getString("movieid"));
                                tempPackages.setTitle(tempObj.getString("title"));
                                tempPackages.setMovieimage(tempObj.getString("movieimage"));
                                tempPackages.setGenre(tempObj.getString("genre"));
                                tempPackages.setPathurl(tempObj.getString("pathurl"));
                                trailerArrList.add(tempPackages);
                            }
                            labelTv.setVisibility(View.VISIBLE);
                            noDataTv.setVisibility(View.GONE);
                            categoryAdapter = new MyMoviesListAdapter(trailerArrList, mActivity);
                            allMoviesList.setAdapter(categoryAdapter);
                        } else {
                            labelTv.setVisibility(View.GONE);
                            noDataTv.setVisibility(View.VISIBLE);
                            allMoviesList.setVisibility(View.GONE);
                            noDataTv.setText(errorMessage);
                        }

                        dismissProgDialog();

                    } else {
                        dismissProgDialog();
                        labelTv.setVisibility(View.GONE);
                        noDataTv.setVisibility(View.VISIBLE);
                        allMoviesList.setVisibility(View.GONE);
                        noDataTv.setText(errorMessage);
                    }
                } catch (JSONException e) {
                    dismissProgDialog();
                    Utility.showToast(mActivity, SOMETHING_WENT_WORNG);
                }
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
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_home, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_home:
                ((HomeScreen) mActivity).setDashboardScreen();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
