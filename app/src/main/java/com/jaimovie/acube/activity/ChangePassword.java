package com.jaimovie.acube.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;

import com.android.volley.VolleyError;
import com.jaimovie.acube.R;
import com.jaimovie.acube.constants.ErrorMsg;
import com.jaimovie.acube.model.IResult;
import com.jaimovie.acube.network.VolleyService;
import com.jaimovie.acube.utils.ChangeTypeFace;
import com.jaimovie.acube.utils.ConnectionDetector;
import com.jaimovie.acube.utils.Utility;
import com.jaimovie.acube.view.CleanableEditText;
import com.zookey.universalpreferences.UniversalPreferences;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.jaimovie.acube.constants.AppConfig.BASE;
import static com.jaimovie.acube.constants.ErrorMsg.PARSING_ERROR;
import static com.jaimovie.acube.constants.ErrorMsg.SOMETHING_WENT_WORNG;
import static com.jaimovie.acube.constants.PreferenceName.PREFS_USERID;
import static com.jaimovie.acube.utils.Utility.dismissProgDialog;
import static com.jaimovie.acube.utils.Utility.showProgDialog;

public class ChangePassword extends AppCompatActivity implements View.OnClickListener {

    private LinearLayout totalLayout;
    private Button btnSubmit;
    private Toolbar mToolBar;
    private CleanableEditText newPasswordEdt, confirmPasswordEdt, currPasswordEdt;
    private String accessToken = "";
    private static final String TAG = "Change Password Screen";
    private IResult mResultCallback = null;
    private VolleyService mVolleyService;
    private Activity mActivity;
    private String userId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_change_password);
        mActivity = this;

        loadViewComponents();
        setToolBar();

        initVolleyCallback();
        mVolleyService = new VolleyService(mResultCallback, this);

        userId = UniversalPreferences.getInstance().get(PREFS_USERID, "");

//        currPasswordEdt.setText("kumanan1");
//        newPasswordEdt.setText("123456");
//        confirmPasswordEdt.setText("123456");

        ChangeTypeFace.overrideMavenMediumFont(this, totalLayout);
        ChangeTypeFace.overrideMavenBoldFont(this, btnSubmit);

        btnSubmit.setOnClickListener(this);
    }

    private void setToolBar() {
        setSupportActionBar(mToolBar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        mToolBar.setNavigationIcon(R.mipmap.ic_window_close);
        mToolBar.setTitleTextColor(getResources().getColor(R.color.colorWhite));
        getSupportActionBar().setTitle("Change Password");
    }

    private void loadViewComponents() {
        mToolBar = findViewById(R.id.toolbar_actionbar);
        totalLayout = findViewById(R.id.totalLayout);
        btnSubmit = findViewById(R.id.btn_submit);
        newPasswordEdt = findViewById(R.id.edt_password);
        confirmPasswordEdt = findViewById(R.id.edt_confirm_password);
        currPasswordEdt = findViewById(R.id.edt_currPassword);
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(R.anim.activity_open_scale, R.anim.activity_close_translate);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_submit:
                validation();
                break;
        }
    }

    private void validation() {
        if (currPasswordEdt.getText().toString().equalsIgnoreCase("")) {
            Utility.showToast(this, ErrorMsg.EMPTY_OLD_PASSWORD);
            currPasswordEdt.requestFocus();
        } else if (newPasswordEdt.getText().toString().equalsIgnoreCase("")) {
            Utility.showToast(this, ErrorMsg.EMPTY_PASSWORD);
            newPasswordEdt.requestFocus();
        } else if (confirmPasswordEdt.getText().toString().equalsIgnoreCase("")) {
            Utility.showToast(this, ErrorMsg.PASSWORD_NOT_MATCH);
            confirmPasswordEdt.requestFocus();
        } else if (newPasswordEdt.getText().toString().length() < 6) {
            Utility.showToast(this, ErrorMsg.PASSWORD_LENGTH);
            newPasswordEdt.requestFocus();
        } else if (confirmPasswordEdt.getText().toString().length() < 6) {
            Utility.showToast(this, ErrorMsg.PASSWORD_LENGTH);
            confirmPasswordEdt.requestFocus();
        } else if (!confirmPasswordEdt.getText().toString().equalsIgnoreCase(newPasswordEdt.getText().toString())) {
            Utility.showToast(this, ErrorMsg.PASSWORD_NOT_MATCH);
            confirmPasswordEdt.requestFocus();
        } else {
            if (ConnectionDetector.isConnectingToInternet(getApplicationContext())) {
                showProgDialog(ChangePassword.this, "Please wait..");
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(btnSubmit.getWindowToken(), 0);
                changePassword();
            } else {
                Utility.showToast(this, ErrorMsg.INTERNET_CONNECTION);
            }
        }
    }

    private void changePassword() {

        Map<String, String> map = new HashMap<String, String>();
        map.put("userid", userId);
        map.put("oldpassword", currPasswordEdt.getText().toString());
        map.put("newpassword", confirmPasswordEdt.getText().toString());
        map.put("tag", "changepassword");
        mVolleyService.postDataVolleyWithAccess("POST", BASE, map);

    }

    void initVolleyCallback() {
        mResultCallback = new IResult() {
            @Override
            public void notifyJsonSuccess(String requestType, JSONObject response) {
                Log.e("LOGIN-SCREEN=====>", "Volley JSON post" + response);
                dismissProgDialog();
            }

            @Override
            public void notifyJsonSuccess(String requestType, String response, String type) {
                Log.e(TAG, " response ==>" + "" + response);
                dismissProgDialog();
                try {
                    JSONObject obj = new JSONObject(response);
                    int success = obj.getInt("success");
                    String error_message = obj.getString("error_message");

                    if (success == 200) {
                        Utility.showToast(ChangePassword.this, "Password has been updated");
                        finish();
                    } else {
                        Utility.showFailureToast(ChangePassword.this, "Password not updated, Please try again later");
                    }
                } catch (JSONException e) {
                    dismissProgDialog();
                    Utility.showFailureToast(mActivity, SOMETHING_WENT_WORNG);
                    Log.e(TAG, " Volley ==>" + "" + e.toString());
                }
            }

            @Override
            public void notifyJsonSuccess(String requestType, JSONObject response, String serviceName) {
                Log.e(TAG, " response ==>" + "" + response);
                dismissProgDialog();
                try {
                    int success = response.getInt("success");
                    String error = response.getString("error_message");


                } catch (JSONException e) {
                    Utility.showFailureToast(mActivity, PARSING_ERROR);
                }
            }

            @Override
            public void notifyError(String requestType, VolleyError error) {
                Log.e(TAG, " Volley ==>" + "" + error);
            }

            @Override
            public void notifyError(String requestType, String error) {
                Utility.showFailureToast(mActivity, error);
                Log.e(TAG, " Volley ==>" + "" + error);
            }
        };
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
}
