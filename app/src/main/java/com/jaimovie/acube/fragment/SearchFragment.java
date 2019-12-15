package com.jaimovie.acube.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.jaimovie.acube.R;
import com.jaimovie.acube.activity.HomeScreen;
import com.jaimovie.acube.adapter.AdapterSectionRecycler;
import com.jaimovie.acube.model.Child;
import com.jaimovie.acube.model.IResult;
import com.jaimovie.acube.model.SearchHeader;
import com.jaimovie.acube.network.VolleyService;
import com.jaimovie.acube.utils.ChangeTypeFace;
import com.jaimovie.acube.utils.Utility;

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

public class SearchFragment extends Fragment {

    private static final String TAG = SearchFragment.class.getSimpleName();
    private RecyclerView allMoviesList;
    private Activity mActivity;
    private IResult mResultCallback = null;
    private VolleyService mVolleyService;
    private LinearLayout totalLayout;
    private TextView labelTv;
    private EditText edt_search;
    private ImageView searchIv;
    private int pageNo = 1;
    private AdapterSectionRecycler adapterRecycler;
    private List<SearchHeader> sectionHeaders;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_search, container, false);

        mActivity = getActivity();
        allMoviesList = view.findViewById(R.id.upcomingList);
        totalLayout = view.findViewById(R.id.totalLayout);
        labelTv = view.findViewById(R.id.labelTv);
        edt_search = view.findViewById(R.id.edt_search);
        searchIv = view.findViewById(R.id.searchIv);

//        labelTv.setText("Trailer");
        ChangeTypeFace.overrideMavenRegularFont(mActivity, totalLayout);
        ChangeTypeFace.overrideMavenBoldFont(mActivity, edt_search);
        initVolleyCallback();

        edt_search.setOnEditorActionListener(
                new EditText.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        // Identifier of the action. This will be either the identifier you supplied,
                        // or EditorInfo.IME_NULL if being called due to the enter key being pressed.
                        if (actionId == EditorInfo.IME_ACTION_SEARCH
                                || actionId == EditorInfo.IME_ACTION_DONE
                                || event.getAction() == KeyEvent.ACTION_DOWN
                                && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {

                            if (!edt_search.getText().toString().equalsIgnoreCase("")) {
                                showProgDialog(mActivity, "Please wait..");
                                mVolleyService = new VolleyService(mResultCallback, mActivity);
                                Map<String, String> map = new HashMap<String, String>();
                                map.put("page", String.valueOf(pageNo));
                                map.put("limit", "10");
                                map.put("search", edt_search.getText().toString());
                                map.put("tag", "search");
                                mVolleyService.postVolleyData("POST", BASE, map);
                            }

                            return true;
                        }
                        // Return true if you have consumed the action, else false.
                        return false;
                    }
                });

        searchIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!edt_search.getText().toString().equalsIgnoreCase("")) {
                    showProgDialog(mActivity, "Please wait..");
                    mVolleyService = new VolleyService(mResultCallback, mActivity);
                    Map<String, String> map = new HashMap<String, String>();
                    map.put("page", String.valueOf(pageNo));
                    map.put("limit", "10");
                    map.put("search", edt_search.getText().toString());
                    map.put("tag", "search");
                    mVolleyService.postVolleyData("POST", BASE, map);
                } else {
                    Utility.showToast(mActivity, "Enter a keyword to search");
                }
            }
        });


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mActivity);
        allMoviesList.setLayoutManager(linearLayoutManager);
        allMoviesList.setHasFixedSize(true);

        return view;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
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
                Log.e(TAG, " Searchlist ==>" + "" + response);
                try {
                    JSONObject json = new JSONObject(response);
                    int success = json.getInt("success");
                    String errorMessage = json.getString("error_message");
                    if (success == 200) {
                        JSONObject resp = json.getJSONObject("response");
                        JSONArray trailerResp = resp.getJSONArray("trailer");
                        sectionHeaders = new ArrayList<>();

                        if (trailerResp.length() > 0) {
                            List<Child> childList = new ArrayList<>();
                            for (int i = 0; i < trailerResp.length(); i++) {
                                JSONObject tempObj = trailerResp.getJSONObject(i);
                                String id = tempObj.getString("trailerid");
                                String name = tempObj.getString("title");
                                String trailerimage = tempObj.getString("trailerimage");
                                String pathurl = tempObj.getString("pathurl");
                                String genre = tempObj.getString("genre");
                                childList.add(new Child(id, name, trailerimage, pathurl, genre, "2"));

                            }
                            sectionHeaders.add(new SearchHeader(childList, "Trailer", 1));
                        }

                        JSONArray movieResp = resp.getJSONArray("movies");
                        if (movieResp.length() > 0) {
                            List<Child> childList1 = new ArrayList<>();
                            for (int i = 0; i < movieResp.length(); i++) {
                                JSONObject tempObj = movieResp.getJSONObject(i);
                                String id = tempObj.getString("movieid");
                                String name = tempObj.getString("title");
                                String trailerimage = tempObj.getString("movieimage");
                                String pathurl = tempObj.getString("pathurl");
                                String genre = tempObj.getString("genre");
                                childList1.add(new Child(id, name, trailerimage, pathurl, genre, "1"));
                            }
                            sectionHeaders.add(new SearchHeader(childList1, "Movies", 2));
                        }

                        adapterRecycler = new AdapterSectionRecycler(mActivity, sectionHeaders);
                        allMoviesList.setAdapter(adapterRecycler);
                        allMoviesList.setVisibility(View.VISIBLE);
                        dismissProgDialog();

                        if(sectionHeaders.size() == 0){
                            Utility.showFailureToast(mActivity, "No movie or trailer found");
                        }

                        Log.e(TAG, "--sectionHeaders size--" + sectionHeaders.size());
                    } else {
                        dismissProgDialog();
                        Utility.showFailureToast(mActivity, errorMessage);
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

    @Override
    public void onResume() {
        super.onResume();

        Log.e(TAG, "----" + edt_search.getText().toString());

        if (!edt_search.getText().toString().equalsIgnoreCase("")) {
            showProgDialog(mActivity, "Please wait..");
            mVolleyService = new VolleyService(mResultCallback, mActivity);
            Map<String, String> map = new HashMap<String, String>();
            map.put("page", String.valueOf(pageNo));
            map.put("limit", "10");
            map.put("search", edt_search.getText().toString());
            map.put("tag", "search");
            mVolleyService.postVolleyData("POST", BASE, map);
        }
    }
}
