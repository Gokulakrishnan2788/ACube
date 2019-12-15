package com.jaimovie.acube.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
import com.ct7ct7ct7.androidvimeoplayer.view.VimeoPlayerActivity;
import com.ct7ct7ct7.androidvimeoplayer.view.VimeoPlayerView;
import com.flipkart.youtubeview.YouTubePlayerView;
import com.flipkart.youtubeview.models.ImageLoader;
import com.jaimovie.acube.R;
import com.jaimovie.acube.activity.HomeScreen;
import com.jaimovie.acube.constants.AppConfig;
import com.jaimovie.acube.model.IResult;
import com.jaimovie.acube.network.VolleyService;
import com.jaimovie.acube.utils.ChangeTypeFace;
import com.jaimovie.acube.utils.Utility;
import com.squareup.picasso.Picasso;
import com.zookey.universalpreferences.UniversalPreferences;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.jaimovie.acube.constants.AppConfig.BASE;
import static com.jaimovie.acube.constants.ErrorMsg.SOMETHING_WENT_WORNG;
import static com.jaimovie.acube.constants.PreferenceName.PREFS_USERID;
import static com.jaimovie.acube.utils.Utility.dismissProgDialog;
import static com.jaimovie.acube.utils.Utility.showProgDialog;

public class TrailerDetailsFragment extends Fragment {

    private static final String TAG = TrailerDetailsFragment.class.getSimpleName();
    private Activity mActivity;
    private int playerType = 0;
    private String videoUrl = "", id = "";
    private LinearLayout totalLayout;
    private TextView movieTitleTv, infoTv, castTv, movieLangTv, descTv;
    private IResult mResultCallback = null;
    private VolleyService mVolleyService;
    private VimeoPlayerView videoPlayer;
    private Button watchLaterBtn, shareBtn;
    private String movieNameStr = "", userId;
    public static final int REQUEST_CODE = 100;

    private ImageLoader imageLoader = new ImageLoader() {
        @Override
        public void loadImage(@NonNull ImageView imageView, @NonNull String url, int height, int width) {
            Picasso.with(imageView.getContext()).load(url).resize(width, height).centerCrop().into(imageView);
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_details, container, false);

        mActivity = getActivity();
        videoPlayer = view.findViewById(R.id.vimeoPlayer);
        watchLaterBtn = view.findViewById(R.id.watchLaterBtn);
        shareBtn = view.findViewById(R.id.shareBtn);
        descTv = view.findViewById(R.id.descTv);

        Bundle arguments = getArguments();
        playerType = arguments.getInt("playerType");
        videoUrl = arguments.getString("videoUrl");
        id = arguments.getString("id");

        if (videoUrl != null && !videoUrl.equalsIgnoreCase("")) {
            videoPlayer.initialize(true, Integer.parseInt(videoUrl));
            videoPlayer.setFullscreenVisibility(true);
            videoPlayer.setMenuVisibility(false);
        }

        videoPlayer.setFullscreenClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String requestOrientation = VimeoPlayerActivity.REQUEST_ORIENTATION_LANDSCAPE;
                float startAt = videoPlayer.getCurrentTimeSeconds();

                Log.e(TAG, "--" + startAt);
//                Intent intent = new Intent(mActivity, VimeoPlayerActivity.class);
//                intent.putExtra("EXTRA_ORIENTATION", requestOrientation);
//                intent.putExtra("EXTRA_START_AT", startAt);
//                intent.putExtra("EXTRA_VIDEO_ID", videoUrl);
//                startActivityForResult(intent, REQUEST_CODE);

                startActivityForResult(VimeoPlayerActivity.createIntent(mActivity, requestOrientation, videoPlayer), REQUEST_CODE);
            }
        });

        userId = UniversalPreferences.getInstance().get(PREFS_USERID, "");
        totalLayout = view.findViewById(R.id.totalLayout);
        movieTitleTv = view.findViewById(R.id.movieTitleTv);
        infoTv = view.findViewById(R.id.infoTv);
        castTv = view.findViewById(R.id.castTv);
        movieLangTv = view.findViewById(R.id.movieLangTv);

        ChangeTypeFace.overrideMavenMediumFont(mActivity, totalLayout);
        ChangeTypeFace.overrideMavenBlackFont(mActivity, movieTitleTv);
        ChangeTypeFace.overrideMavenLightFont(mActivity, movieLangTv);
        ChangeTypeFace.overrideMavenBoldFont(mActivity, infoTv);
        ChangeTypeFace.overrideMavenLightFont(mActivity, castTv);
        ChangeTypeFace.overrideMavenLightFont(mActivity, watchLaterBtn);
        ChangeTypeFace.overrideMavenLightFont(mActivity, shareBtn);

        initVolleyCallback();

        mVolleyService = new VolleyService(mResultCallback, mActivity);
        Map<String, String> map = new HashMap<String, String>();
        map.put("trailerid", id);
        map.put("currencycode", "inr");
        map.put("tag", "trailerdetail");
        mVolleyService.postDataVolleyStringRequest("POST", BASE, map, "trailerDetail");

        watchLaterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userId != null && !userId.equalsIgnoreCase("")) {
                    showProgDialog(mActivity, "Please wait..");
                    Map<String, String> map = new HashMap<String, String>();
                    map.put("trailerid", id);
                    map.put("lasttime", "01:07:00");
                    map.put("userid", userId);
                    map.put("tag", "addwatch");
                    mVolleyService.postVolleyDataWithAccessToken("POST", BASE, map, "addWatch");
                } else {
                    Utility.showFailureToast(mActivity, "Please login to add to watch later list");
                }

            }
        });

        shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_SUBJECT, "ACube");
                sendIntent.putExtra(Intent.EXTRA_TEXT,
                        "Watch " + movieNameStr + " Trailer on ACube");
                sendIntent.setType("text/plain");
                startActivity(Intent.createChooser(sendIntent, "Share link!"));
//                startActivity(sendIntent);
            }
        });

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
                Log.e(TAG, " requestType ==>" + "" + response);
                try {
                    JSONObject json = new JSONObject(response);
                    int success = json.getInt("success");
                    String errorMessage = json.getString("error_message");

                    if (success == 200) {
                        JSONObject tempObj = json.getJSONObject("response");
                        String movieId = tempObj.getString("trailerid");
                        movieNameStr = tempObj.getString("title");
                        String movieimage = tempObj.getString("trailerimage");
                        String description = tempObj.getString("description");
                        String cast = tempObj.getString("cast");
                        String genre = tempObj.getString("genre");
                        String musicdirector = tempObj.getString("musicdirector");
                        String director = tempObj.getString("director");
                        String producedby = tempObj.getString("producedby");
                        String certification = tempObj.getString("certification");
                        String pathurl = tempObj.getString("pathurl");
                        String language = tempObj.getString("language");
                        dismissProgDialog();

                        castTv.setText("Cast : " + cast + "\n" + "Director : " + director + "\n" + "Music Director : " + musicdirector + "\n"
                                + "Produced By : " + producedby + "\n" + "Certification : "
                                + certification + "\n" + "Language : " + language + "\n" + "Genre : " + genre);
                        movieLangTv.setText(language);
                        movieTitleTv.setText(movieNameStr);
                        descTv.setText(description);
                        totalLayout.setVisibility(View.VISIBLE);
                        dismissProgDialog();
                    } else {
                        dismissProgDialog();
//                        Utility.showFailureToast(mActivity, errorMessage);
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
