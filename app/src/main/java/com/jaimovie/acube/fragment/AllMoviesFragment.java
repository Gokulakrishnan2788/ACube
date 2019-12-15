package com.jaimovie.acube.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.graphics.Typeface;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.iamtheib.infiniterecyclerview.InfiniteAdapter;
import com.jaimovie.acube.R;
import com.jaimovie.acube.activity.HomeScreen;
import com.jaimovie.acube.adapter.MovieInfinityAdapter;
import com.jaimovie.acube.model.GenreList;
import com.jaimovie.acube.model.IResult;
import com.jaimovie.acube.model.MovieList;
import com.jaimovie.acube.network.VolleyService;
import com.jaimovie.acube.utils.ChangeTypeFace;
import com.jaimovie.acube.utils.Utility;
import com.jaredrummler.materialspinner.MaterialSpinner;

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

public class AllMoviesFragment extends Fragment {

    private static final String TAG = AllMoviesFragment.class.getSimpleName();
    private RecyclerView allMoviesList;
    private Activity mActivity;
    private IResult mResultCallback = null;
    private VolleyService mVolleyService;
    private List<MovieList> movieArrList;
    private List<GenreList> genreArrList, languageArrList;
    private MovieInfinityAdapter categoryAdapter;
    private LinearLayout totalLayout;
    private int pageNo = 1;
    private int currSize;
    private ImageView filterTv;
    private MaterialSpinner languageSpinner, genreSpinner;
    private String selectedLanguage = "", selectedGenre = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_movies, container, false);

        mActivity = getActivity();
        allMoviesList = view.findViewById(R.id.upcomingList);
        totalLayout = view.findViewById(R.id.totalLayout);
        filterTv = view.findViewById(R.id.filterTv);


        ChangeTypeFace.overrideMavenBoldFont(mActivity, totalLayout);

        initVolleyCallback();
        mVolleyService = new VolleyService(mResultCallback, mActivity);

        filterTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedLanguage = "";
                selectedGenre = "";
                showFilterDialog();
            }
        });

        return view;
    }

    void initVolleyCallback() {
        mResultCallback = new IResult() {
            @Override
            public void notifyJsonSuccess(String requestType, JSONObject response) {

                dismissProgDialog();
                try {
                    int success = response.getInt("success");
                    String errorMessage = response.getString("error_message");
                    switch (requestType) {
                        case "categorylist":
                            Log.e(TAG, " categorylist ==>" + "" + response);
                            genreArrList = new ArrayList<>();
                            GenreList tempPackages = new GenreList();
                            tempPackages.setId("0");
                            tempPackages.setName("Select Genre");
                            genreArrList.add(tempPackages);
                            JSONArray packageResp = response.getJSONArray("response");
                            if (packageResp.length() > 0) {
                                for (int i = 0; i < packageResp.length(); i++) {
                                    JSONObject tempObj = packageResp.getJSONObject(i);
                                    GenreList tempPackages1 = new GenreList();
                                    tempPackages1.setId(tempObj.getString("id"));
                                    tempPackages1.setName(tempObj.getString("name"));
                                    genreArrList.add(tempPackages1);
                                }
                            }
                            break;

                        case "languagelist":
                            Log.e(TAG, " languagelist ==>" + "" + response);
                            languageArrList = new ArrayList<>();
                            GenreList tempPackages3 = new GenreList();
                            tempPackages3.setId("0");
                            tempPackages3.setName("Select Language");
                            languageArrList.add(tempPackages3);
                            JSONArray packageResp1 = response.getJSONArray("response");
                            if (packageResp1.length() > 0) {
                                for (int i = 0; i < packageResp1.length(); i++) {
                                    JSONObject tempObj = packageResp1.getJSONObject(i);
                                    GenreList tempPackages2 = new GenreList();
                                    tempPackages2.setId(tempObj.getString("id"));
                                    tempPackages2.setName(tempObj.getString("name"));
                                    languageArrList.add(tempPackages2);
                                }
                            }
                            break;
                    }
                } catch (JSONException e) {
                    dismissProgDialog();
                    Utility.showToast(mActivity, SOMETHING_WENT_WORNG);
                }


            }

            @Override
            public void notifyJsonSuccess(String requestType, String response, String type) {

                dismissProgDialog();
                try {
                    JSONObject json = new JSONObject(response);
                    int success = json.getInt("success");
                    String errorMessage = json.getString("error_message");
                    switch (type) {
                        case "movieslist":
                            Log.e(TAG, " movieslist ==>" + "" + response);
                            if (success == 200) {
                                movieArrList = new ArrayList<>();
                                JSONArray packageResp = json.getJSONArray("response");
                                if (packageResp.length() > 0) {

                                    for (int i = 0; i < packageResp.length(); i++) {
                                        JSONObject tempObj = packageResp.getJSONObject(i);
                                        MovieList tempPackages = new MovieList();
                                        tempPackages.setId(tempObj.getString("movieid"));
                                        tempPackages.setName(tempObj.getString("title"));
                                        tempPackages.setImage(tempObj.getString("movieimage"));
                                        tempPackages.setGenre(tempObj.getString("genre"));
                                        tempPackages.setPathurl(tempObj.getString("pathurl"));
                                        movieArrList.add(tempPackages);
                                    }

                                    if (pageNo == 1) {
                                        categoryAdapter = new MovieInfinityAdapter(mActivity, movieArrList);
                                        allMoviesList.setAdapter(categoryAdapter);
                                        GridLayoutManager gridLayout = new GridLayoutManager(getActivity(), 2, GridLayoutManager.VERTICAL, false);
                                        allMoviesList.setLayoutManager(gridLayout);
//                                categoryAdapter.setOnLoadMoreListener(mLoadMoreListener);
                                        if (packageResp.length() == 10) {
                                            pageNo = pageNo + 1;
                                        } else {
                                            categoryAdapter.setShouldLoadMore(false);
                                        }
                                    } else if (pageNo > 1) {
                                        categoryAdapter.moreDataLoaded(currSize, movieArrList.size() - currSize);
                                        categoryAdapter.setShouldLoadMore(false);
                                        if (packageResp.length() == 10) {
                                            pageNo = pageNo + 1;
                                        } else {
                                            categoryAdapter.setShouldLoadMore(false);
                                        }
                                    }
                                }
                            } else {
                                dismissProgDialog();
                                Log.e(TAG, "--Page--" + pageNo);
                                if (pageNo == 1) {
                                    Utility.showFailureToast(mActivity, errorMessage);
                                    totalLayout.setVisibility(View.VISIBLE);
                                } else {
                                    Log.e(TAG, "--Page--1");
                                    if (allMoviesList != null) {
                                        Log.e(TAG, "--Page--2");
                                        if (movieArrList.size() > 0) {
                                            Log.e(TAG, "--Page--3");
                                            categoryAdapter.setShouldLoadMore(false);
                                        }
                                    }
                                }
                            }
                            break;

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

    private InfiniteAdapter.OnLoadMoreListener mLoadMoreListener = new InfiniteAdapter.OnLoadMoreListener() {
        @Override
        public void onLoadMore() {
            Log.e("Main", "Load more fired");
            currSize = movieArrList.size();
            loadData();
        }
    };


    @Override
    public void onResume() {
        super.onResume();
        loadCategory();
        loadLanguage();
        loadData();
    }

    private void loadCategory() {
        String url = BASE + "?tag=categorylist";
        mVolleyService.getDataVolley(url, "categorylist");
    }

    private void loadLanguage() {
        String url = BASE + "?tag=languagelist";
        mVolleyService.getDataVolley(url, "languagelist");
    }

    private void loadData() {
        pageNo = 1;
        movieArrList = new ArrayList<>();
        showProgDialog(mActivity, "Please wait..");
        Map<String, String> map = new HashMap<String, String>();
        map.put("page", String.valueOf(pageNo));
        map.put("limit", "10");
        map.put("search", "");
        map.put("categoryid", selectedGenre);
        map.put("languageid", selectedLanguage);
        map.put("tag", "movieslist");
        mVolleyService.postVolleyData("POST", BASE, map, "movieslist");
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

    private void showFilterDialog() {

        final Dialog dialog = new Dialog(mActivity);
        dialog.setContentView(R.layout.dialog_filter);
        ImageView cancel = dialog.findViewById(R.id.close);

        cancel.setOnClickListener(new View.OnClickListener() {
            @SuppressLint({"LongLogTag", "SetTextI18n"})
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        Typeface myFont = Typeface.createFromAsset(mActivity.getResources().getAssets(), "fonts/Roboto-Medium.ttf");
        languageSpinner = dialog.findViewById(R.id.spinerlocation);
        genreSpinner = dialog.findViewById(R.id.spinerGenre);
        Button okBtn = dialog.findViewById(R.id.okBtn);
        TextView titledialogTXT = dialog.findViewById(R.id.titledialogTXT);
        LinearLayout radioLYT = dialog.findViewById(R.id.radioLYT);
//        Button cancelBtn = dialog.findViewById(R.id.cancelBtn);

        String[] floorStr = new String[languageArrList.size()];
        for (int i = 0; i < languageArrList.size(); i++) {
            floorStr[i] = languageArrList.get(i).getName();
            Log.e("Language=========>", "" + floorStr[i]);
        }
        languageSpinner.setItems(floorStr);
        languageSpinner.setSelectedIndex(0);

        String[] genreStr = new String[genreArrList.size()];
        for (int i = 0; i < genreArrList.size(); i++) {
            genreStr[i] = genreArrList.get(i).getName();
            Log.e("Genre=========>", "" + genreStr[i]);
        }
        genreSpinner.setItems(genreStr);
        genreSpinner.setSelectedIndex(0);

        ChangeTypeFace.overrideMavenRegularFont(mActivity, radioLYT);
        ChangeTypeFace.overrideMavenBoldFont(mActivity, okBtn);
        ChangeTypeFace.overrideMavenBoldFont(mActivity, titledialogTXT);

        languageSpinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
                Log.e("----", "----" + languageArrList.get(position).getName());
                selectedLanguage = languageArrList.get(position).getId();
            }
        });

        genreSpinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
                Log.e("----", "----" + genreArrList.get(position).getName());
                selectedGenre = genreArrList.get(position).getId();
            }
        });

        /*cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });*/

        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("--selectedLanguage--", "----" + selectedLanguage);
                Log.e("--selectedGenre--", "----" + selectedGenre);

                dialog.dismiss();
                loadData();
                if (selectedLanguage.equalsIgnoreCase("")) {
//                    Utility.showFailureToast(mActivity, "Please select a timing");
//                    Log.e("--selectedLanguage--", "----" + selectedLanguage);
//                    Log.e("--selectedGenre--", "----" + selectedGenre);
//                    dialog.dismiss();
                } else {
//                    dialog.dismiss();
                }
            }
        });

        languageSpinner.setTypeface(myFont);
        genreSpinner.setTypeface(myFont);
        dialog.show();
    }
}
