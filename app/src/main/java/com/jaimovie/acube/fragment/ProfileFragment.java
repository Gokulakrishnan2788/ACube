package com.jaimovie.acube.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jaimovie.acube.R;
import com.jaimovie.acube.activity.ChangePassword;
import com.jaimovie.acube.activity.HomeScreen;
import com.jaimovie.acube.utils.ChangeTypeFace;
import com.zookey.universalpreferences.UniversalPreferences;

import static com.jaimovie.acube.constants.PreferenceName.PREFS_ADDRESS;
import static com.jaimovie.acube.constants.PreferenceName.PREFS_EMAIL;
import static com.jaimovie.acube.constants.PreferenceName.PREFS_FIRST_NAME;
import static com.jaimovie.acube.constants.PreferenceName.PREFS_LAST_NAME;
import static com.jaimovie.acube.constants.PreferenceName.PREFS_PHONE;
import static com.jaimovie.acube.constants.PreferenceName.PREFS_TOKEN;

public class ProfileFragment extends Fragment implements View.OnClickListener {

    public static final String TAG = "Profile Screen";
    private RelativeLayout totalLayout;
    private TextView changePasswordTv, logoutTv, editPersonalTv,
            editBillingTv, editShippingTv, generalDetailsTv, billingAddressTv, shippingAddressTv, profileName;
    private View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_profile, container, false);
        loadViewComponents();

        getSharedPrefs();

        changePasswordTv.setOnClickListener(this);
        editPersonalTv.setOnClickListener(this);
        editBillingTv.setOnClickListener(this);
        editShippingTv.setOnClickListener(this);
        logoutTv.setOnClickListener(this);
        return view;
    }

    private void loadViewComponents() {
        totalLayout = view.findViewById(R.id.totalLayout);
        changePasswordTv = view.findViewById(R.id.changePassword);
        logoutTv = view.findViewById(R.id.logoutTv);
        editPersonalTv = view.findViewById(R.id.editPersonalTv);
        editBillingTv = view.findViewById(R.id.editBillingTv);
        editShippingTv = view.findViewById(R.id.editShippingTv);
        generalDetailsTv = view.findViewById(R.id.generalDetails);
        billingAddressTv = view.findViewById(R.id.billingAddress);
        shippingAddressTv = view.findViewById(R.id.shippingAddress);
        profileName = view.findViewById(R.id.profileName);
    }

    private void getSharedPrefs() {
        String name = UniversalPreferences.getInstance().get(PREFS_FIRST_NAME, "") + " " + UniversalPreferences.getInstance().get(PREFS_LAST_NAME, "");
        String email = UniversalPreferences.getInstance().get(PREFS_EMAIL, "");
        String accessToken = UniversalPreferences.getInstance().get(PREFS_TOKEN, "");
        String mobile = UniversalPreferences.getInstance().get(PREFS_PHONE, "");
        String address = UniversalPreferences.getInstance().get(PREFS_ADDRESS, "");

        ChangeTypeFace.overrideMavenBlackFont(getActivity(), totalLayout);
        ChangeTypeFace.overrideMavenBoldFont(getActivity(), profileName);
        ChangeTypeFace.overrideMavenBoldFont(getActivity(), generalDetailsTv);
        profileName.setText(name);
        billingAddressTv.setText(address);

        StringBuilder personalDetails = new StringBuilder(name);
        if (!email.equalsIgnoreCase("")) {
            personalDetails.append("\n" + email);
        }
        if (!mobile.equalsIgnoreCase("")) {
            personalDetails.append("\n" + mobile);
        }
        generalDetailsTv.setText("" + personalDetails.toString());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.editBillingTv:
                break;

            case R.id.editShippingTv:
                break;

            case R.id.editPersonalTv:
                break;

            case R.id.changePassword:
                Intent changePwd = new Intent(getActivity(), ChangePassword.class);
                startActivity(changePwd);
                break;

            case R.id.logoutTv:
                ((HomeScreen) getActivity()).showDialog();
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getSharedPrefs();
    }


}
