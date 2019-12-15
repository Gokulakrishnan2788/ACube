package com.jaimovie.acube.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.jaimovie.acube.R;
import com.jaimovie.acube.constants.ErrorMsg;
import com.jaimovie.acube.model.IResult;
import com.jaimovie.acube.network.VolleyService;
import com.jaimovie.acube.utils.ChangeTypeFace;
import com.jaimovie.acube.utils.ConnectionDetector;
import com.jaimovie.acube.utils.Utility;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.jaimovie.acube.constants.AppConfig.BASE;
import static com.jaimovie.acube.constants.AppConstant.KEY_EMAIL;
import static com.jaimovie.acube.constants.AppConstant.KEY_FIRST_NAME;
import static com.jaimovie.acube.constants.AppConstant.KEY_LAST_NAME;
import static com.jaimovie.acube.constants.AppConstant.KEY_MOBILE;
import static com.jaimovie.acube.constants.AppConstant.KEY_PASSWORD;
import static com.jaimovie.acube.constants.AppConstant.KEY_TAG;
import static com.jaimovie.acube.constants.AppConstant.VALUE_REGISTER;
import static com.jaimovie.acube.constants.ErrorMsg.INTERNET_CONNECTION;
import static com.jaimovie.acube.constants.ErrorMsg.SOMETHING_WENT_WORNG;
import static com.jaimovie.acube.utils.Utility.dismissProgDialog;
import static com.jaimovie.acube.utils.Utility.showProgDialog;

public class RegisterScreen extends AppCompatActivity {

    private EditText firstNameEdt, lastNameEdt, addressEdt, emailEdt, mobileEdt, passwordEdt;
    private RelativeLayout totalLayout;
    private String TAG = RegisterScreen.class.getSimpleName();
    private IResult mResultCallback = null;
    private VolleyService mVolleyService;
    private TextView createLabel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
//        overridePendingTransition(R.anim.activity_open_translate, R.anim.activity_close_scale);

        Button registerBtn = findViewById(R.id.btn_register);
        totalLayout = findViewById(R.id.totalLayout);
        firstNameEdt = findViewById(R.id.firstNameEdt);
        lastNameEdt = findViewById(R.id.lastNameEdt);
        addressEdt = findViewById(R.id.addressEdt);
        emailEdt = findViewById(R.id.emailEdt);
        mobileEdt = findViewById(R.id.mobileEdt);
        passwordEdt = findViewById(R.id.passwordEdt);
        createLabel = findViewById(R.id.createLabel);

//        firstNameEdt.setText("Kumanan");
//        lastNameEdt.setText("Chandru");
//        emailEdt.setText("kumanan7@gmail.com");
//        mobileEdt.setText("9566065019");
//        passwordEdt.setText("kumanan1");

        ChangeTypeFace.overrideMavenMediumFont(this, totalLayout);
        ChangeTypeFace.overrideMavenBoldFont(this, registerBtn);
        ChangeTypeFace.overrideMavenBoldFont(this, createLabel);

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validation();
            }
        });

    }

    private void validation() {
        // Check patter for email id
        Pattern p = Pattern.compile(Utility.regEx);
        Pattern pass = Pattern.compile(Utility.regExPassword);
        Matcher m = p.matcher(emailEdt.getText().toString());
        Matcher passMatcher = pass.matcher(passwordEdt.getText().toString());
        if (firstNameEdt.getText().toString().equalsIgnoreCase("")) {
            Utility.showFailureToast(this, ErrorMsg.EMPTY_FIRST_NAME);
            firstNameEdt.requestFocus();
        } else if (lastNameEdt.getText().toString().equalsIgnoreCase("")) {
            Utility.showFailureToast(this, ErrorMsg.EMPTY_LAST_NAME);
            lastNameEdt.requestFocus();
        } else if (emailEdt.getText().toString().equalsIgnoreCase("")) {
            Utility.showFailureToast(this, ErrorMsg.EMPTY_EMAIL);
            emailEdt.requestFocus();
        } else if (!m.find()) {
            Utility.showFailureToast(this, ErrorMsg.VALID_EMAIL);
            emailEdt.requestFocus();
        } else if (mobileEdt.getText().toString().equalsIgnoreCase("")) {
            Utility.showFailureToast(this, ErrorMsg.EMPTY_NUMBER);
            mobileEdt.requestFocus();
        } else if (passwordEdt.getText().toString().equalsIgnoreCase("")) {
            Utility.showFailureToast(this, ErrorMsg.EMPTY_PASSWORD);
            passwordEdt.requestFocus();
        } else if (passwordEdt.getText().toString().length() < 6) {
            Utility.showToast(this, ErrorMsg.PASSWORD_LENGTH);
            passwordEdt.requestFocus();
        } else {
            initVolleyCallback();
            mVolleyService = new VolleyService(mResultCallback, this);
            if (ConnectionDetector.isConnectingToInternet(getApplicationContext())) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(passwordEdt.getWindowToken(), 0);
                userRegister();
            } else {
                Utility.showFailureToast(this, INTERNET_CONNECTION);
            }
        }
    }

    private void userRegister() {
        showProgDialog(RegisterScreen.this, "Please wait...");
        Map<String, String> params = new HashMap<>();

        params.put(KEY_FIRST_NAME, firstNameEdt.getText().toString().trim());
        params.put(KEY_LAST_NAME, lastNameEdt.getText().toString().trim());
        params.put(KEY_EMAIL, emailEdt.getText().toString().trim());
        params.put(KEY_MOBILE, mobileEdt.getText().toString().trim());
        params.put(KEY_PASSWORD, passwordEdt.getText().toString().trim());
        params.put("address", addressEdt.getText().toString().trim());
        params.put("city", "Chennai");
        params.put("state", "Tamilnadu");
        params.put("pincode", "600049");
        params.put("countryid", "1");
        params.put("is_social", "0");
        params.put(KEY_TAG, VALUE_REGISTER);
        Log.e(TAG, "-----" + params.toString());
        mVolleyService.postDataVolleyStringRequest("POST", BASE, params, "");
    }

    @Override
    protected void onPause() {
        super.onPause();
//        overridePendingTransition(R.anim.activity_open_scale, R.anim.activity_close_translate);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    void initVolleyCallback() {
        mResultCallback = new IResult() {
            @Override
            public void notifyJsonSuccess(String requestType, JSONObject response) {
                dismissProgDialog();
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
                    JSONObject obj = new JSONObject(response);
                    int success = obj.getInt("success");
                    String error_message = obj.getString("error_message");
                    if (success == 200) {
                        String otp = obj.getString("otp");
                        Utility.showSuccessToast(RegisterScreen.this, "Successfully Registered, Your Activation OTP send to Your Email.");
                        Intent otpIntent = new Intent(RegisterScreen.this, OTPScreen.class);
                        otpIntent.putExtra("email", emailEdt.getText().toString());
                        otpIntent.putExtra("otp", otp);
                        startActivity(otpIntent);
                        finish();
                    }else {
                        int error = obj.getInt("error");
                        if(error == 301){
                            Utility.showFailureToast(RegisterScreen.this, error_message);
                            Intent otpIntent = new Intent(RegisterScreen.this, OTPScreen.class);
                            otpIntent.putExtra("email", emailEdt.getText().toString());
                            otpIntent.putExtra("otp", "");
                            startActivity(otpIntent);
                            finish();
                        }else{
                            Utility.showFailureToast(RegisterScreen.this, error_message);
                        }
                    }
                } catch (JSONException e) {
                    Utility.showToast(RegisterScreen.this, SOMETHING_WENT_WORNG);
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
                Utility.showToast(RegisterScreen.this, error);
                Log.e(TAG, " Volley ==>" + "" + error);
            }
        };
    }
}
