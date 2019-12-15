package com.jaimovie.acube.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.Task;
import com.jaimovie.acube.R;
import com.jaimovie.acube.constants.ErrorMsg;
import com.jaimovie.acube.facebookSignIn.FacebookHelper;
import com.jaimovie.acube.facebookSignIn.FacebookResponse;
import com.jaimovie.acube.facebookSignIn.FacebookUser;
import com.jaimovie.acube.model.IResult;
import com.jaimovie.acube.network.VolleyService;
import com.jaimovie.acube.utils.ChangeTypeFace;
import com.jaimovie.acube.utils.KeyboardUtils;
import com.jaimovie.acube.utils.Utility;
import com.zookey.universalpreferences.UniversalPreferences;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.jaimovie.acube.constants.AppConfig.BASE;
import static com.jaimovie.acube.constants.ErrorMsg.PARSING_ERROR;
import static com.jaimovie.acube.constants.ErrorMsg.SOMETHING_WENT_WORNG;
import static com.jaimovie.acube.constants.ErrorMsg.VERIFY_OTP;
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

public class LoginScreen extends AppCompatActivity implements View.OnClickListener, FacebookResponse, GoogleApiClient.OnConnectionFailedListener {

    private final String TAG = LoginScreen.class.getSimpleName();
    private EditText edt_username;
    private EditText passwordEdt;
    private IResult mResultCallback = null;
    private VolleyService mVolleyService;
    private FacebookHelper mFbHelper;
    private ImageView fbIcon, googleIcon;
    private GoogleSignInClient googleSignInClient;
    private Activity mActivity;

//    MD5: 05:D5:3C:D9:03:46:52:29:E2:13:15:FC:4A:14:69:85
//    SHA1: 26:39:B3:30:B6:BB:1F:AE:98:29:B2:49:59:73:A0:24:E4:EB:46:9D
//    Client ID
//    180146372453-91lel5t7i5051btljts7jk8k3k8160cr.apps.googleusercontent.com
//    Client Secret
//    YU_uNIPBpi0Nd02yvPKBcp2o

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loginscreen);
//        overridePendingTransition(R.anim.activity_open_translate, R.anim.activity_close_scale);

        mFbHelper = new FacebookHelper(this,
                "id,name,email,gender,birthday,picture,cover",
                this);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);

        mActivity = this;

        try {
            PackageInfo info = getPackageManager().getPackageInfo("com.jaimovie.acube", PackageManager.GET_SIGNATURES);
            for (android.content.pm.Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String sign = Base64.encodeToString(md.digest(), Base64.DEFAULT);
                Log.e(TAG, " MY KEY HASH:" + sign);
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, " NameNotFoundException:" + e.toString());
        } catch (NoSuchAlgorithmException e) {
            Log.e(TAG, " NoSuchAlgorithmException:" + e.toString());
        }

        RelativeLayout totalLoginLayout = findViewById(R.id.totalLoginLayout);
        Button loginBtn = findViewById(R.id.btn_login);
        TextView signUp = findViewById(R.id.signUp);
        edt_username = findViewById(R.id.edt_username);
        passwordEdt = findViewById(R.id.edt_password);
        fbIcon = findViewById(R.id.fbIcon);
        googleIcon = findViewById(R.id.googleIcon);
        TextView forgotPassTv = findViewById(R.id.forgotPassTv);
        forgotPassTv.setVisibility(View.GONE);

//        edt_username.setText("kumanan10@gmail.com");
//        passwordEdt.setText("kumanan1");
//        edt_username.setText("surajsha_2k@yahoo.co.in");
//        passwordEdt.setText("12345");

        ChangeTypeFace.overrideMavenBoldFont(this, totalLoginLayout);
        ChangeTypeFace.overrideMavenBoldFont(this, loginBtn);
        ChangeTypeFace.overrideMavenMediumFont(this, passwordEdt);
        ChangeTypeFace.overrideMavenMediumFont(this, edt_username);

        initVolleyCallback();
        mVolleyService = new VolleyService(mResultCallback, this);

        fbIcon.setOnClickListener(this);
        googleIcon.setOnClickListener(this);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validation();
            }
        });

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent homeScreen = new Intent(getApplicationContext(), RegisterScreen.class);
                startActivity(homeScreen);
                finish();
            }
        });

        forgotPassTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showForgotPassDialog();
            }
        });
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

    private void showForgotPassDialog() {

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.forgetpassword);
        LinearLayout radioLYT = dialog.findViewById(R.id.radioLYT);
        TextView titledialogTXT = dialog.findViewById(R.id.titledialogTXT);
        ImageView cancel = dialog.findViewById(R.id.close);
        final EditText emailEDt = dialog.findViewById(R.id.emailEDt);
        Button changepwdBtn = dialog.findViewById(R.id.changepwdBtn);

        cancel.setOnClickListener(new View.OnClickListener() {
            @SuppressLint({"LongLogTag", "SetTextI18n"})
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                KeyboardUtils.hideKeyboard(mActivity);
            }
        });

        changepwdBtn.setOnClickListener(new View.OnClickListener() {
            @SuppressLint({"LongLogTag", "SetTextI18n"})
            @Override
            public void onClick(View v) {
                String email = emailEDt.getText().toString().trim();
                Pattern p = Pattern.compile(Utility.regEx);
                Matcher m = p.matcher(email);

                if (email.equalsIgnoreCase("")) {
                    Utility.showFailureToast(mActivity, ErrorMsg.EMPTY_EMAIL);
                    emailEDt.requestFocus();
                } else if (!m.find()) {
                    Utility.showFailureToast(mActivity, ErrorMsg.VALID_EMAIL);
                    emailEDt.requestFocus();
                } else {
                    dialog.dismiss();
                    KeyboardUtils.hideKeyboard(mActivity);
                    showProgDialog(mActivity, "Please wait..");
                }
            }
        });

        ChangeTypeFace.overrideMavenRegularFont(getApplicationContext(), radioLYT);
        ChangeTypeFace.overrideMavenRegularFont(getApplicationContext(), emailEDt);
        dialog.show();
    }

    private void validation() {
        // Check patter for email id
        Pattern p = Pattern.compile(Utility.regEx);
        Matcher m = p.matcher(edt_username.getText().toString());

        if (edt_username.getText().toString().equalsIgnoreCase("")) {
            Utility.showFailureToast(this, ErrorMsg.EMPTY_EMAIL);
            edt_username.requestFocus();
        } else if (!m.find()) {
            Utility.showFailureToast(this, ErrorMsg.VALID_EMAIL);
            edt_username.requestFocus();
        } else if (passwordEdt.getText().toString().equalsIgnoreCase("")) {
            Utility.showFailureToast(this, ErrorMsg.EMPTY_PASSWORD);
            passwordEdt.requestFocus();
        } else if (passwordEdt.getText().toString().length() < 6) {
            Utility.showToast(this, ErrorMsg.PASSWORD_LENGTH);
            passwordEdt.requestFocus();
        } else {
            showProgDialog(mActivity, "Please wait..");
            Map<String, String> map = new HashMap<String, String>();
            map.put("username", edt_username.getText().toString());
            map.put("password", passwordEdt.getText().toString());
            map.put("fcmid", "fkodbnkdnbnbdkfsdkvnsdkjbv564sv654sdv5");
            map.put("tag", "login");
            mVolleyService.postDataVolleyStringRequest("POST", BASE, map, "Login");
        }
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
                        dismissProgDialog();
                        finish();
                    } else if (success == 401) {
                        String phone = obj.getString("response");
                        UniversalPreferences.getInstance().put(PREFS_PHONE, phone);
                        Utility.showFailureToast(mActivity, "" + VERIFY_OTP);
                    } else {
                        Utility.showFailureToast(mActivity, "" + error_message);
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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fbIcon:
                mFbHelper.performSignIn(this);
                break;

            case R.id.googleIcon:
                Intent signInIntent = googleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, 101);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 1549520352021856
    }

    @Override
    public void onFbSignInFail() {
//        Toast.makeText(this, "Facebook sign in failed.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onFbSignInSuccess() {
    }


    @Override
    public void onFbProfileReceived(FacebookUser facebookUser) {

        showProgDialog(mActivity, "Please wait..");

        Map<String, String> map = new HashMap<String, String>();
        String[] data = facebookUser.name.split(" ");
        map.put("firstname", data[0]);
        map.put("lastname", data[1]);
        map.put("email", facebookUser.email);
        map.put("is_social", "1");
        map.put("fcmid", "fkodbnkdnbnbdkfsdkvnsdkjbv564sv654sdv5");
        map.put("phone", "");
        map.put("tag", "loginwithsocial");
        mVolleyService.postDataVolleyStringRequest("POST", BASE, map, "SocialLogin");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mFbHelper.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 101:
                try {
                    // The Task returned from this call is always completed, no need to attach
                    // a listener.
                    Utility.showToast(mActivity, "101");
                    Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                    GoogleSignInAccount account = task.getResult(ApiException.class);
                    onLoggedIn(account);
                } catch (ApiException e) {
//                    Utility.showToast(mActivity, "ApiException" + e.getStatusCode());
                    // The ApiException status code indicates the detailed failure reason.
                    Log.e(TAG, "signInResult:failed code=" + e.getStatusCode());
                }
                break;
        }
    }

    private void onLoggedIn(GoogleSignInAccount googleSignInAccount) {
//        Utility.showToast(mActivity, "--googleSignInAccount--" + googleSignInAccount.getDisplayName());

        String[] data = googleSignInAccount.getDisplayName().split(" ", 2);
        showProgDialog(mActivity, "Please wait..");
        Map<String, String> map = new HashMap<>();
        map.put("firstname", data[0]);
        map.put("lastname", data[1]);
        map.put("email", googleSignInAccount.getEmail());
        map.put("is_social", "1");
        map.put("fcmid", "fkodbnkdnbnbdkfsdkvnsdkjbv564sv654sdv5");
        map.put("phone", "");
        map.put("tag", "loginwithsocial");
        mVolleyService.postVolleyData("POST", BASE, map);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Utility.showToast(mActivity, "Google Connection Failed");
    }
}
