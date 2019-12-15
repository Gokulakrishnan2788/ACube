package com.jaimovie.acube.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.jaimovie.acube.R;
import com.kaopiz.kprogresshud.KProgressHUD;

import java.text.SimpleDateFormat;

public class Utility {

    public static final String phonenumber = "^[0-9]{10}$";
    private static KProgressHUD progressBar;

    public static void showFailureLongToast(Context mActivity, String msg) {

        Toast toast = Toast.makeText(mActivity, msg, Toast.LENGTH_SHORT);
        View view = toast.getView();
        view.setBackgroundResource(R.drawable.bg_toast);
        TextView text = (TextView) view.findViewById(android.R.id.message);
        text.setText(msg);
        ChangeTypeFace.overrideMavenBoldFont(mActivity, text);
        toast.show();
    }

    public static void showFailureToast(Context mActivity, String msg) {
//        Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show();
        Toast toast = Toast.makeText(mActivity, msg, Toast.LENGTH_SHORT);
        View view = toast.getView();
        view.setBackgroundResource(R.drawable.bg_toast);
        TextView text = (TextView) view.findViewById(android.R.id.message);
        text.setGravity(Gravity.CENTER);
        /*here you can do anything with text*/
        text.setText(msg);
        ChangeTypeFace.overrideMavenBoldFont(mActivity, text);
        toast.show();
    }

    public static void showNormalToast(Context mActivity, String msg) {
//        Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show();
        Toast toast = Toast.makeText(mActivity, msg, Toast.LENGTH_SHORT);
        View view = toast.getView();
        view.setBackgroundResource(R.drawable.bg_normal_toast);
        TextView text = (TextView) view.findViewById(android.R.id.message);
        text.setGravity(Gravity.CENTER);
        /*here you can do anything with text*/
        text.setText(msg);
        ChangeTypeFace.overrideMavenBoldFont(mActivity, text);
        toast.show();
    }

    public static void showSuccessToast(Context mActivity, String msg) {
        Toast toast = Toast.makeText(mActivity, msg, Toast.LENGTH_SHORT);
        View view = toast.getView();
        view.setBackgroundResource(R.drawable.bg_toast_success);
        TextView text = (TextView) view.findViewById(android.R.id.message);
        /*here you can do anything with text*/
        text.setText(msg);
        ChangeTypeFace.overrideMavenBoldFont(mActivity, text);
        toast.show();
    }

    private static final String TAG = "Util Class";
    //    private static KProgressHUD progressBar;
    private static final long ONE_MINUTE_IN_MILLIS = 60000;//millisecs

    //Email Validation pattern
    public static final String regEx = "\\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}\\b";
    public static final String regExPassword = "((?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{8,20})";

    // Showing Toast message
    public static void showToast(Activity mActivity, String msg) {
        Toast.makeText(mActivity, msg, Toast.LENGTH_SHORT).show();
    }

    public static void showToastLong(Context mActivity, String msg) {
        Toast.makeText(mActivity, msg, Toast.LENGTH_SHORT).show();
    }

    // Addition of two double values
    public static double addProposalValue(double value1, double value2) {
        double totalValue;
        totalValue = value1 + value2;
        return totalValue;
    }


    public static double roundOffValue(double value) {
//        double roundOff = (double) Math.round(value * 100.00) / 100.00;
//        double roundOff = Math.round(Math.floor(value));
//        Log.e(TAG,"-->"+roundOff);
        return Math.round(Math.floor(value));
    }

    public static int getIntegerValue(double value) {
        int output;
        output = (int) value;
//        Log.e(TAG,"-->"+output);
        return output;
    }

    public static boolean isValidPhoneNumber(String mobile) {
        String regEx = "^[0-9]{10}$";
        return mobile.matches(regEx);
    }

    public static boolean isOTPPhoneNumber(String mobile) {
        String regEx = "^[0-9]{4}$";
        return mobile.matches(regEx);
    }

    public static boolean isValidEmailAddress(String email) {
//        String emailPattern = "[a-zA-Z0-9._.-]+@[a-z_-]+\\.+[a-z]+";
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        return email.matches(emailPattern);
    }

    public static boolean isOnlyText(String email) {
        String regEx = "^[a-zA-Z]$";
        return email.matches(regEx);
    }


    public static String formatTime(String formatedTime) {
        String time;
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss aa");
        time = sdf.format(formatedTime);
        return time;
    }

    public static void showProgDialog(Activity mActivity, String message) {
        if (progressBar == null)
            progressBar = KProgressHUD.create(mActivity)
                    .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                    .setLabel(message)
                    .setCancellable(false);

        if (!progressBar.isShowing()) {
            progressBar = KProgressHUD.create(mActivity);
            progressBar.setStyle(KProgressHUD.Style.SPIN_INDETERMINATE);
            progressBar.setLabel(message);
            progressBar.show();
        }
    }

    public static void dismissProgDialog() {
        try {
            if (progressBar != null && progressBar.isShowing())
                progressBar.dismiss();
        } catch (Exception e) {
            Log.e("Exception--->",""+e.toString());
        }
    }

}
