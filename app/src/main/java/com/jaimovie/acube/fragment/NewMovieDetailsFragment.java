package com.jaimovie.acube.fragment;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.ct7ct7ct7.androidvimeoplayer.view.VimeoPlayerActivity;
import com.ct7ct7ct7.androidvimeoplayer.view.VimeoPlayerView;
import com.example.vimeoplayer2.UniversalMediaController;
import com.example.vimeoplayer2.UniversalVideoView;
import com.example.vimeoplayer2.vimeoextractor.OnVimeoExtractionListener;
import com.example.vimeoplayer2.vimeoextractor.VimeoExtractor;
import com.example.vimeoplayer2.vimeoextractor.VimeoVideo;
import com.flipkart.youtubeview.models.ImageLoader;
import com.jaimovie.acube.R;
import com.jaimovie.acube.activity.HomeScreen;
import com.jaimovie.acube.activity.LoginScreen;
import com.jaimovie.acube.model.IResult;
import com.jaimovie.acube.network.VolleyService;
import com.jaimovie.acube.paypal.PayPalConfig;
import com.jaimovie.acube.utils.ChangeTypeFace;
import com.jaimovie.acube.utils.Utility;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.zookey.universalpreferences.UniversalPreferences;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static com.jaimovie.acube.constants.AppConfig.BASE;
import static com.jaimovie.acube.constants.ErrorMsg.SOMETHING_WENT_WORNG;
import static com.jaimovie.acube.constants.PreferenceName.PREFS_FIRST_NAME;
import static com.jaimovie.acube.constants.PreferenceName.PREFS_ISO;
import static com.jaimovie.acube.constants.PreferenceName.PREFS_LAST_NAME;
import static com.jaimovie.acube.constants.PreferenceName.PREFS_USERID;
import static com.jaimovie.acube.utils.Utility.dismissProgDialog;
import static com.jaimovie.acube.utils.Utility.showProgDialog;

public class NewMovieDetailsFragment extends Fragment {

    private static final String TAG = NewMovieDetailsFragment.class.getSimpleName();
    private Activity mActivity;
    private String videoUrl = "", id = "", amount = "0";
    private LinearLayout totalLayout;
    private RelativeLayout previewLayout;
    private TextView movieTitleTv, infoTv, castTv, movieLangTv, genreTv, descTv;
    private IResult mResultCallback = null;
    private VolleyService mVolleyService;
    private Button watchLaterBtn, shareBtn, payNowBtn;
    private String movieNameStr = "";
    private ImageView previewIv, previewPlayIv;
    private String userId, userFirstName = "", userLastName = "", orderId;

    //Paypal Configuration Object
    private static PayPalConfiguration config = new PayPalConfiguration()
            // Start with mock environment.  When ready, switch to sandbox (ENVIRONMENT_SANDBOX)
            // or live (ENVIRONMENT_PRODUCTION)
            .environment(PayPalConfiguration.ENVIRONMENT_SANDBOX)
            .clientId(PayPalConfig.PAYPAL_CLIENT_ID);

    UniversalVideoView mVideoView;
    UniversalMediaController mMediaController;
    View mVideoLayout;
    private int mSeekPosition;
    private int cachedHeight;
    private boolean isFullscreen;

    //Paypal intent request code to track onActivityResult method
    public static final int PAYPAL_REQUEST_CODE = 123;
    public static final int REQUEST_CODE = 100;
    private static final String VIMEO_VIDEO_URL = "https://player.vimeo.com/video/";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_movie_details_new, container, false);

        mActivity = getActivity();
        watchLaterBtn = view.findViewById(R.id.watchLaterBtn);
        shareBtn = view.findViewById(R.id.shareBtn);
        payNowBtn = view.findViewById(R.id.payNowBtn);
        genreTv = view.findViewById(R.id.genreTv);
        previewIv = view.findViewById(R.id.previewIv);
        previewLayout = view.findViewById(R.id.previewLayout);
        previewPlayIv = view.findViewById(R.id.previewPlayIv);
        descTv = view.findViewById(R.id.descTv);
        totalLayout = view.findViewById(R.id.totalLayout);
        movieTitleTv = view.findViewById(R.id.movieTitleTv);
        infoTv = view.findViewById(R.id.infoTv);
        castTv = view.findViewById(R.id.castTv);
        movieLangTv = view.findViewById(R.id.movieLangTv);

        mVideoView = view.findViewById(R.id.videoView);
        mMediaController = view.findViewById(R.id.media_controller);
        mVideoLayout = view.findViewById(R.id.video_layout);
        mVideoView.setMediaController(mMediaController);
        setVideoAreaSize();

        Bundle arguments = getArguments();

        videoUrl = arguments.getString("videoUrl");
        Log.e(TAG, "--videoUrl--" + videoUrl);
        id = arguments.getString("id");
        userId = UniversalPreferences.getInstance().get(PREFS_USERID, "");

        if (videoUrl != null && !videoUrl.equalsIgnoreCase("") && !videoUrl.equalsIgnoreCase("0")) {

            VimeoExtractor.getInstance().fetchVideoWithURL(VIMEO_VIDEO_URL + videoUrl, "ACube", new OnVimeoExtractionListener() {
                @Override
                public void onSuccess(final VimeoVideo video) {
                    String hdStream = null;
                    for (String key : video.getStreams().keySet()) {
                        hdStream = key;
                    }
                    final String hdStreamuRL = video.getStreams().get(hdStream);
                    if (hdStream != null) {
                        mActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // Start the MediaController
                                mVideoView.setMediaController(mMediaController);
                                // Get the URL from String VideoURL
                                Uri video = Uri.parse(hdStreamuRL);

                                mVideoView.setVideoURI(video);

                            }
                        });
                    }
                }

//                setLink(hdStream);
                //...
//            }

                @Override
                public void onFailure(Throwable throwable) {
                    //Error handling here
                }
            });
        }


        ChangeTypeFace.overrideMavenMediumFont(mActivity, totalLayout);
        ChangeTypeFace.overrideMavenBlackFont(mActivity, movieTitleTv);
        ChangeTypeFace.overrideMavenLightFont(mActivity, movieLangTv);
        ChangeTypeFace.overrideMavenBoldFont(mActivity, infoTv);
        ChangeTypeFace.overrideMavenLightFont(mActivity, castTv);
        ChangeTypeFace.overrideMavenLightFont(mActivity, watchLaterBtn);
        ChangeTypeFace.overrideMavenLightFont(mActivity, shareBtn);
        ChangeTypeFace.overrideMavenBoldFont(mActivity, payNowBtn);

        initVolleyCallback();

        mVolleyService = new VolleyService(mResultCallback, mActivity);
        loadMovieDetails();

        watchLaterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (userId != null && !userId.equalsIgnoreCase("")) {
                    showProgDialog(mActivity, "Please wait..");
                    Map<String, String> map = new HashMap<String, String>();
                    map.put("movieid", id);
                    map.put("lasttime", "01:07:00");
                    map.put("userid", userId);
                    map.put("tag", "addwatch");
                    mVolleyService.postVolleyDataWithAccessToken("POST", BASE, map, "addWatch");
                } else {
                    Utility.showFailureToast(mActivity, "Please login to add to watch later list");
                }

            }
        });

        payNowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userId != null && !userId.equalsIgnoreCase("")) {
                    showProgDialog(mActivity, "Please wait..");
                    Map<String, String> map = new HashMap<String, String>();
                    map.put("userid", userId);
                    map.put("movieid", id);
                    map.put("custfirstname", userFirstName);
                    map.put("custlastname", userLastName);
                    map.put("address", "test");
                    map.put("city", "chennai");
                    map.put("state", "Tamilnadu");
                    map.put("countryid", "1");
                    map.put("pincode", "600049");
                    map.put("currencycode", "USD");
                    map.put("tag", "saveorder");
                    mVolleyService.postVolleyDataWithAccessToken("POST", BASE, map, "saveorder");
                } else {
                    Utility.showFailureToast(mActivity, "Please login to add to watch");
                    Intent loginIntent = new Intent(mActivity, LoginScreen.class);
                    mActivity.startActivity(loginIntent);
                }


            }
        });
        previewPlayIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userId != null && !userId.equalsIgnoreCase("")) {
                    Utility.showFailureToast(mActivity, "Please pay to watch this movie");
                } else {
                    Utility.showFailureToast(mActivity, "Please login to add to watch");
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
                        "Watch " + movieNameStr + " movie on ACube");
                sendIntent.setType("text/plain");
                startActivity(Intent.createChooser(sendIntent, "Share link!"));
//                startActivity(sendIntent);
            }
        });


        return view;
    }

    private void setVideoAreaSize() {
        mVideoLayout.post(new Runnable() {
            @Override
            public void run() {
                int width = mVideoLayout.getWidth();
                cachedHeight = (int) (width * 405f / 720f);
                ViewGroup.LayoutParams videoLayoutParams = mVideoLayout.getLayoutParams();
                videoLayoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
                videoLayoutParams.height = cachedHeight;
                mVideoLayout.setLayoutParams(videoLayoutParams);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        userId = UniversalPreferences.getInstance().get(PREFS_USERID, "");
        userFirstName = UniversalPreferences.getInstance().get(PREFS_FIRST_NAME, "");
        userLastName = UniversalPreferences.getInstance().get(PREFS_LAST_NAME, "");

    }

    @Override
    public void onDestroy() {
        mActivity.stopService(new Intent(mActivity, PayPalService.class));
        super.onDestroy();
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
                    JSONObject json = new JSONObject(response);
                    int success = json.getInt("success");
                    String errorMessage = json.getString("error_message");

                    switch (type) {
                        case "movieDetails":
                            Log.e(TAG, " Movie Details ==>" + "" + response);
                            dismissProgDialog();
                            if (success == 200) {
                                JSONObject tempObj = json.getJSONObject("response");
                                String movieId = tempObj.getString("movieid");
                                movieNameStr = tempObj.getString("title");
                                String movieimage = tempObj.getString("movieimage");
                                String description = tempObj.getString("description");
                                String cast = tempObj.getString("cast");
                                String genre = tempObj.getString("genre");
                                String musicdirector = tempObj.getString("musicdirector");
                                String director = tempObj.getString("director");
                                String producedby = tempObj.getString("producedby");
                                String certification = tempObj.getString("certification");

                                if (videoUrl == null && videoUrl.equalsIgnoreCase("") &&
                                        !videoUrl.equalsIgnoreCase("0")) {
                                    videoUrl = tempObj.getString("pathurl");
                                }

                                String isFree = tempObj.getString("isFree");
                                String language = tempObj.getString("language");
                                amount = tempObj.getString("amount");
                                String currencycode = tempObj.getString("currencycode");
                                String localcurrencycode = tempObj.getString("localcurrencycode");
                                String localCurrencyAmount = tempObj.getString("localamount");
                                String paid = tempObj.getString("paid");
                                String currency = "";
                                if (currencycode.equalsIgnoreCase("USD")) {
                                    currency = "$";
                                }
//                                Typeface tf = Typeface.createFromAsset(mActivity.getAssets(), "fonts/Rupee_Foradian.ttf");
//                                payNowBtn.setTypeface(tf);

                                castTv.setText("Cast : " + cast + "\n" + "Director : " + director + "\n" + "Music Director : " + musicdirector + "\n"
                                        + "Produced By : " + producedby + "\n" + "Certification : "
                                        + certification + "\n" + "Language : " + language + "\n" + "Genre : " + genre);
                                movieLangTv.setText(language);
                                movieTitleTv.setText(movieNameStr);
                                descTv.setText(description);
                                genreTv.setText(genre);

                                if (isFree.equalsIgnoreCase("0")) {
                                    if (paid.equalsIgnoreCase("1")) {
                                        payNowBtn.setVisibility(View.GONE);
                                        previewIv.setVisibility(View.GONE);
                                        previewLayout.setVisibility(View.GONE);
                                        mVideoLayout.setVisibility(View.VISIBLE);
                                    } else {
                                        Picasso.with(mActivity)
                                                .load(movieimage)
                                                .noFade()
                                                .into(previewIv, new Callback() {
                                                    @Override
                                                    public void onSuccess() {
                                                    }

                                                    @Override
                                                    public void onError() {
                                                    }
                                                });
                                        previewIv.setVisibility(View.VISIBLE);
                                        previewLayout.setVisibility(View.VISIBLE);
                                        mVideoLayout.setVisibility(View.GONE);
                                        payNowBtn.setVisibility(View.VISIBLE);
                                        payNowBtn.setText("Pay " + currency + amount);
                                    }
                                } else {
                                    mVideoLayout.setVisibility(View.VISIBLE);
                                    previewIv.setVisibility(View.GONE);
                                    payNowBtn.setVisibility(View.GONE);
                                }
                                totalLayout.setVisibility(View.VISIBLE);
                            } else {
                                Utility.showFailureToast(mActivity, errorMessage);
                            }
                            break;

                        case "addWatch":
                            Log.e(TAG, " Add Watch ==>" + "" + response);
                            dismissProgDialog();
                            if (success == 200) {
                                String respMsg = json.getString("response");
                                Utility.showSuccessToast(mActivity, respMsg);
                            } else {
                                Utility.showFailureToast(mActivity, errorMessage);
                            }
                            break;

                        case "saveorder":
                            Log.e(TAG, " Save Order==>" + "" + response);
                            dismissProgDialog();
                            if (success == 200) {
                                JSONObject tempObj = json.getJSONObject("response");
                                orderId = tempObj.getString("orderid");
                                Intent intentService = new Intent(mActivity, PayPalService.class);
                                intentService.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
                                mActivity.startService(intentService);

                                //Creating a paypalpayment
                                PayPalPayment payment = new PayPalPayment(new BigDecimal(String.valueOf(amount)), "USD", "" + movieNameStr + " Subscription fee",
                                        PayPalPayment.PAYMENT_INTENT_SALE);

                                //Creating Paypal Payment activity intent
                                Intent intent = new Intent(mActivity, PaymentActivity.class);

                                //putting the paypal configuration to the intent
                                intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);

                                //Puting paypal payment to the intent
                                intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payment);

                                //Starting the intent activity for result
                                //the request code will be used on the method onActivityResult
                                startActivityForResult(intent, PAYPAL_REQUEST_CODE);
                            } else {
                                Utility.showSuccessToast(mActivity, errorMessage);
                            }

                            break;

                        case "updateorderstatus":
                            Log.e(TAG, " Update Save Order==>" + "" + response);
                            if (success == 200) {
                                String respMsg = json.getString("response");
                                Utility.showSuccessToast(mActivity, "Your scription for the movie has been completed successfully.");
                            } else {
                                Utility.showFailureToast(mActivity, errorMessage);
                            }
                            loadMovieDetails();
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

    private void loadMovieDetails() {

        String iso = UniversalPreferences.getInstance().get(PREFS_ISO, "");
        Log.e(TAG, "---" + iso);
        showProgDialog(mActivity, "Please wait..");
        Map<String, String> map = new HashMap<String, String>();
        map.put("movieid", id);
        if (iso.equalsIgnoreCase("IND")) {
            map.put("currencycode", "USD");
        } else {
            map.put("currencycode", iso);
        }

        map.put("tag", "moviesdetail");

        if (!userId.equalsIgnoreCase("")) {
            map.put("userid", userId);
            mVolleyService.postVolleyDataWithAccessToken("POST", BASE, map, "movieDetails");
        } else {
            mVolleyService.postDataVolleyStringRequest("POST", BASE, map, "movieDetails");
        }
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PAYPAL_REQUEST_CODE) {

            //If the result is OK i.e. user has not canceled the payment
            if (resultCode == Activity.RESULT_OK) {
                //Getting the payment confirmation
                PaymentConfirmation confirm = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);

                //if confirmation is not null
                if (confirm != null) {
                    try {
                        //Getting the payment details
                        String paymentDetails = confirm.toJSONObject().toString(4);
                        Log.e("paymentExample", paymentDetails);
                        JSONObject jsonDetails = new JSONObject(paymentDetails);
                        JSONObject resp = jsonDetails.getJSONObject("response");
                        String tranId = resp.getString("id");
                        String state = resp.getString("state");

                        if (state.equalsIgnoreCase("success") || state.equalsIgnoreCase("approved")) {
                            showProgDialog(mActivity, "Please wait..");
                            Map<String, String> map = new HashMap<String, String>();
                            map.put("userid", userId);
                            map.put("orderid", orderId);
                            map.put("paypal_status", state);
                            map.put("Paypal_tans_id", tranId);
                            map.put("tag", "updateorderstatus");
                            mVolleyService.postVolleyDataWithAccessToken("POST", BASE, map, "updateorderstatus");
                        } else {
                            showProgDialog(mActivity, "Please wait..");
                            Map<String, String> map = new HashMap<String, String>();
                            map.put("userid", userId);
                            map.put("orderid", orderId);
                            map.put("paypal_status", state);
                            map.put("Paypal_tans_id", tranId);
                            map.put("tag", "updateorderstatus");
                            mVolleyService.postVolleyDataWithAccessToken("POST", BASE, map, "updateorderstatus");
                        }

                        //Starting a new activity for the payment details and also putting the payment details with intent
                        /*startActivity(new Intent(mActivity, ConfirmationActivity.class)
                                .putExtra("PaymentDetails", paymentDetails)
                                .putExtra("PaymentAmount", amount));*/

                    } catch (JSONException e) {
                        Log.e("paymentExample", "an extremely unlikely failure occurred: ", e);
                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Log.e("paymentExample", "The user canceled.");
                showProgDialog(mActivity, "Please wait..");
                Map<String, String> map = new HashMap<String, String>();
                map.put("userid", userId);
                map.put("orderid", orderId);
                map.put("paypal_status", "Cancelled");
                map.put("Paypal_tans_id", "");
                map.put("tag", "updateorderstatus");
                mVolleyService.postVolleyDataWithAccessToken("POST", BASE, map, "updateorderstatus");
            } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
                Log.e("paymentExample", "An invalid Payment or PayPalConfiguration was submitted. Please see the docs.");
            }
        }

        /*else if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE) {

            float playAt = data.getFloatExtra(VimeoPlayerActivity.RESULT_STATE_VIDEO_PLAY_AT, 0f);
            vimeoPlayer.seekTo(playAt);

            String playerState = data.getStringExtra(VimeoPlayerActivity.RESULT_STATE_PLAYER_STATE);

            Log.e(TAG, "--playAt---" + playAt);
            Log.e(TAG, "--player state---" + playerState);

            if (playerState.equalsIgnoreCase("PLAYING")) {
                vimeoPlayer.play();
            } else if (playerState.equalsIgnoreCase("PAUSED")) {
                vimeoPlayer.pause();
            }
            *//*var playAt = data!!.getFloatExtra(VimeoPlayerActivity.RESULT_STATE_VIDEO_PLAY_AT, 0f)
            vimeoPlayer.seekTo(playAt)

            var playerState = PlayerState.valueOf(data!!.getStringExtra(VimeoPlayerActivity.RESULT_STATE_PLAYER_STATE))
            when (playerState) {
                PlayerState.PLAYING -> vimeoPlayer.play()
                PlayerState.PAUSED -> vimeoPlayer.pause()
            }*//*
        }*/
    }

    protected void displayResultText(String result) {
        Toast.makeText(
                mActivity,
                result, Toast.LENGTH_LONG)
                .show();
    }

}
