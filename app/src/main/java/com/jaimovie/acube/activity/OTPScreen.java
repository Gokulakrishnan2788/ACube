package com.jaimovie.acube.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.jaimovie.acube.R;
import com.jaimovie.acube.constants.AppConfig;
import com.jaimovie.acube.model.IResult;
import com.jaimovie.acube.network.VolleyService;
import com.jaimovie.acube.utils.ChangeTypeFace;
import com.jaimovie.acube.utils.Utility;
import com.zookey.universalpreferences.UniversalPreferences;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.jaimovie.acube.constants.ErrorMsg.EMPTY_OTP;
import static com.jaimovie.acube.constants.ErrorMsg.PARSING_ERROR;
import static com.jaimovie.acube.constants.ErrorMsg.SOMETHING_WENT_WORNG;
import static com.jaimovie.acube.constants.PreferenceName.PREFS_ADDRESS;
import static com.jaimovie.acube.constants.PreferenceName.PREFS_COUNTRY_NAME;
import static com.jaimovie.acube.constants.PreferenceName.PREFS_CUSTOMER_CODE;
import static com.jaimovie.acube.constants.PreferenceName.PREFS_EMAIL;
import static com.jaimovie.acube.constants.PreferenceName.PREFS_FIRST_NAME;
import static com.jaimovie.acube.constants.PreferenceName.PREFS_LAST_NAME;
import static com.jaimovie.acube.constants.PreferenceName.PREFS_LOGGED_IN;
import static com.jaimovie.acube.constants.PreferenceName.PREFS_PHONE;
import static com.jaimovie.acube.constants.PreferenceName.PREFS_TOKEN;
import static com.jaimovie.acube.constants.PreferenceName.PREFS_USERID;
import static com.jaimovie.acube.utils.Utility.dismissProgDialog;
import static com.jaimovie.acube.utils.Utility.showProgDialog;

public class OTPScreen extends AppCompatActivity implements OnClickListener {

    private static final String TAG = "OTP Screen";
    public String name = "", mobile = "";
    private EditText inputOtp;
    private Button verifyBtn;
    private LinearLayout totalLayout;
    private TextView resendTv;
    IResult mResultCallback = null;
    VolleyService mVolleyService;
    private String email, otp;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_otp);

        loadViewComponents();

        initVolleyCallback();
        mVolleyService = new VolleyService(mResultCallback, this);

        Intent intent = getIntent();
        email = intent.getStringExtra("email");
        otp = intent.getStringExtra("otp");

        inputOtp.setText(otp);

        ChangeTypeFace.overrideMavenMediumFont(this, totalLayout);
        ChangeTypeFace.overrideMavenBoldFont(this, verifyBtn);
        verifyBtn.setOnClickListener(this);
        resendTv.setOnClickListener(this);
    }

    void initVolleyCallback() {
        mResultCallback = new IResult() {
            @Override
            public void notifyJsonSuccess(String requestType, JSONObject response) {
                dismissProgDialog();
                try {
                    int success = response.getInt("success");
                    String error_message = response.getString("error_message");
                    if (success == 200) {
                        Utility.showSuccessToast(OTPScreen.this, error_message);
                    } else {
                        Utility.showFailureToast(OTPScreen.this, error_message);
                    }
                } catch (JSONException e) {
                    Utility.showFailureToast(OTPScreen.this, PARSING_ERROR);
                    Log.e(TAG, " Volley ==>" + "" + e.toString());
                }
            }

            @Override
            public void notifyJsonSuccess(String requestType, String response, String type) {
                Log.e("notifyJsonSuccess", "---->" + response);
                dismissProgDialog();
                try {
                    if (response.contains("No mail found")) {
                        String tempWord = "No mail found";
                        response = response.replaceAll(tempWord, "");
                    }
//
                    JSONObject obj = new JSONObject(response);
                    int success = obj.getInt("success");
                    String error_message = obj.getString("error_message");

                    if (success == 200) {
                        JSONObject resp = obj.getJSONObject("response");
                        String firstName = resp.getString("firstname");
                        String lastName = resp.getString("lastname");
                        String emailid = resp.getString("email");
                        String user_id = resp.getString("user_id");
                        String mobileno = resp.getString("phone");
                        String address = resp.getString("address");
                        String access_token = resp.getString("access_token");
                        String customercode = resp.getString("customercode");
                        String countryname = resp.getString("countryname");

                        UniversalPreferences.getInstance().put(PREFS_LOGGED_IN, "yes");
                        UniversalPreferences.getInstance().put(PREFS_TOKEN, access_token);
                        UniversalPreferences.getInstance().put(PREFS_FIRST_NAME, firstName);
                        UniversalPreferences.getInstance().put(PREFS_LAST_NAME, lastName);
                        UniversalPreferences.getInstance().put(PREFS_EMAIL, emailid);
                        UniversalPreferences.getInstance().put(PREFS_COUNTRY_NAME, countryname);
                        UniversalPreferences.getInstance().put(PREFS_CUSTOMER_CODE, customercode);
                        UniversalPreferences.getInstance().put(PREFS_USERID, user_id);
                        UniversalPreferences.getInstance().put(PREFS_PHONE, mobileno);
                        UniversalPreferences.getInstance().put(PREFS_ADDRESS, address);
                        finish();
                    } else {
                        Utility.showFailureToast(OTPScreen.this, "" + error_message);
                    }
                } catch (JSONException e) {
                    Utility.showFailureToast(OTPScreen.this, SOMETHING_WENT_WORNG);
                    Log.e(TAG, " Volley ==>" + "" + e.toString());
                }
            }

            @Override
            public void notifyJsonSuccess(String requestType, JSONObject response, String serviceName) {

            }

            @Override
            public void notifyError(String requestType, VolleyError error) {
                dismissProgDialog();
                Log.e(TAG, " Volley ==>" + "" + error);
            }

            @Override
            public void notifyError(String requestType, String error) {
                dismissProgDialog();
                Utility.showFailureToast(OTPScreen.this, error);
                Log.e(TAG, " Volley ==>" + "" + error);
            }
        };
    }

    private void goHomeActivity() {
        Intent homeIntent = new Intent(this, HomeScreen.class);
        homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(homeIntent);
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private void loadViewComponents() {
        inputOtp = findViewById(R.id.inputOtpEdt);
        verifyBtn = findViewById(R.id.verifyBtn);
        totalLayout = findViewById(R.id.layout_otp);
        resendTv = findViewById(R.id.tv_resend);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.verifyBtn:
                validation();
                break;

            case R.id.tv_resend:
                showProgDialog(OTPScreen.this, "Resending OTP...");
                String phone = UniversalPreferences.getInstance().get(PREFS_PHONE, "");
                String url = AppConfig.BASE + "?mobileno=" + phone + "&tag=reSendRegisterOTP";
                mVolleyService.getDataVolley("GET", url);
                break;
        }
    }

    private void validation() {
        if (inputOtp.getText().toString().equalsIgnoreCase("")) {
            Utility.showFailureToast(this, EMPTY_OTP);
            inputOtp.requestFocus();
        } else {
            showProgDialog(OTPScreen.this, "Verifying...");
            Map<String, String> params = new HashMap<>();
            params.put("tag", "activewithotp");
            params.put("fcmid", "sdovhsadjkbvslkajdbvlsdvisdjvlkdsvnlk");
            params.put("otp", otp);
            params.put("email", email);
            Log.e(TAG, "-----" + params.toString());
            mVolleyService.postDataVolleyStringRequest("POST", AppConfig.BASE, params, "");
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
