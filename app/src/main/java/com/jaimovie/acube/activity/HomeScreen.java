package com.jaimovie.acube.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.flipkart.youtubeview.models.YouTubePlayerType;
import com.jaimovie.acube.R;
import com.jaimovie.acube.constants.AppConfig;
import com.jaimovie.acube.constants.ErrorMsg;
import com.jaimovie.acube.constants.FragmentNames;
import com.jaimovie.acube.custom.NavigationDrawerCallbacks;
import com.jaimovie.acube.custom.TransitionHelper;
import com.jaimovie.acube.fragment.AllMoviesFragment;
import com.jaimovie.acube.fragment.DashboardFragment;
import com.jaimovie.acube.fragment.MovieDetailsFragment;
import com.jaimovie.acube.fragment.MyMovieFragment;
import com.jaimovie.acube.fragment.NavigationDrawerFragment;
import com.jaimovie.acube.fragment.NewMovieDetailsFragment;
import com.jaimovie.acube.fragment.ProfileFragment;
import com.jaimovie.acube.fragment.SearchFragment;
import com.jaimovie.acube.fragment.TrailerDetailsFragment;
import com.jaimovie.acube.fragment.TrailersFragment;
import com.jaimovie.acube.fragment.WatchLaterFragment;
import com.jaimovie.acube.utils.ChangeTypeFace;
import com.jaimovie.acube.utils.Utility;
import com.zookey.universalpreferences.UniversalPreferences;

import static com.jaimovie.acube.constants.PreferenceName.PREFS_CART_COUNT;
import static com.jaimovie.acube.constants.PreferenceName.PREFS_EMAIL;
import static com.jaimovie.acube.constants.PreferenceName.PREFS_FIRST_NAME;
import static com.jaimovie.acube.constants.PreferenceName.PREFS_LAST_NAME;
import static com.jaimovie.acube.constants.PreferenceName.PREFS_LOCATION_NAME;
import static com.jaimovie.acube.constants.PreferenceName.PREFS_LOGGED_IN;
import static com.jaimovie.acube.constants.PreferenceName.PREFS_TOKEN;
import static com.jaimovie.acube.constants.PreferenceName.PREFS_USERID;

public class HomeScreen extends TransitionHelper.BaseActivity implements NavigationDrawerCallbacks, View.OnClickListener {

    private static final String TAG = HomeScreen.class.getSimpleName();
    private NavigationDrawerFragment mNavigationDrawerFragment;
    private boolean doubleBackToExitPressedOnce = false;
    private String userName, userEmail, accessToken, userId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home);

        mNavigationDrawerFragment = (NavigationDrawerFragment) getFragmentManager().findFragmentById(R.id.fragment_drawer);

        Toolbar mToolbar = findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        mNavigationDrawerFragment.setup(R.id.fragment_drawer, (DrawerLayout) findViewById(R.id.drawer), mToolbar);

        mNavigationDrawerFragment.selectedItem(0);

    }


    @Override
    protected void onResume() {
        super.onResume();
        setProfileData();
    }

    public void setProfileData() {
        userName = UniversalPreferences.getInstance().get(PREFS_FIRST_NAME, "") + " " + UniversalPreferences.getInstance().get(PREFS_LAST_NAME, "");
        userEmail = UniversalPreferences.getInstance().get(PREFS_EMAIL, "");
        accessToken = UniversalPreferences.getInstance().get(PREFS_TOKEN, "");
        userId = UniversalPreferences.getInstance().get(PREFS_USERID, "");
        if (mNavigationDrawerFragment != null) {
            mNavigationDrawerFragment.setUserData(userName, userEmail);
        }
    }

    @Override
    public void onClick(View v) {
    }

    @Override
    protected void onPause() {
        super.onPause();
//        overridePendingTransition(R.anim.activity_open_scale, R.anim.activity_close_translate);
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        switch (position) {

            case 0:
                setDashboardScreen();
                break;

            case 1:
                setAllMoviesScreen();
                break;

            case 2:
                setTrailersScreen();
                break;

            case 3:
                Log.e(TAG, "---" + userId);
                if (userId != null && !userId.equalsIgnoreCase("")) {
                    setWatchLaterScreen();
                } else {
                    Utility.showFailureToast(this, "Please login to get watch later list");
                }

                break;

            case 4:
                Log.e(TAG, "---" + userId);
                if (userId != null && !userId.equalsIgnoreCase("")) {
                    setMyMoviesScreen();
                } else {
                    Utility.showFailureToast(this, "Please login to view the list");
                }

                break;

            case 5:
                String isLoggedIn = UniversalPreferences.getInstance().get(PREFS_LOGGED_IN, "");
                if (isLoggedIn.equalsIgnoreCase("yes")) {
                    setProfileScreen();
                } else {
                    Intent loginIntent = new Intent(this, LoginScreen.class);
                    startActivity(loginIntent);
                }

                break;
        }
    }

    public void setDashboardScreen() {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(FragmentNames.FRAGMENT_HOME);
        if (fragment == null) {
            fragment = new DashboardFragment();
        }
        replaceFragment(fragment);
    }

    public void setAllMoviesScreen() {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(FragmentNames.FRAGMENT_MOVIES);
        if (fragment == null) {
            fragment = new AllMoviesFragment();
        }
        replaceFragment(fragment);
    }

    public void setTrailersScreen() {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(FragmentNames.FRAGMENT_TRAILER);
        if (fragment == null) {
            fragment = new TrailersFragment();
        }
        replaceFragment(fragment);
    }

    public void setProfileScreen() {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(FragmentNames.FRAGMENT_PROFILE);
        if (fragment == null) {
            fragment = new ProfileFragment();
        }
        replaceFragment(fragment);
    }

    public void setSearchScreen() {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(FragmentNames.FRAGMENT_SEARCH);
        if (fragment == null) {
            fragment = new SearchFragment();
        }
        replaceFragment(fragment);
    }

    public void setWatchLaterScreen() {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(FragmentNames.FRAGMENT_WATCH_LATER);
        if (fragment == null) {
            fragment = new WatchLaterFragment();
        }
        replaceFragment(fragment);
    }

    public void setMyMoviesScreen() {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(FragmentNames.FRAGMENT_MY_MOVIES);
        if (fragment == null) {
            fragment = new MyMovieFragment();
        }
        replaceFragment(fragment);
    }

    public void setMovieDetailsScreen(String id, String videoUrl) {

        Intent movieDetails = new Intent(this, MovieDetailsScreen.class);
        movieDetails.putExtra("id", id);
        movieDetails.putExtra("videoUrl", videoUrl);
        startActivity(movieDetails);

        /*Fragment fragment = getSupportFragmentManager().findFragmentByTag(FragmentNames.FRAGMENT_MOVIE_DETAILS);
        Bundle b = new Bundle();
        b.putInt("playerType", YouTubePlayerType.STRICT_NATIVE);
        b.putString("id", id);
        b.putString("videoUrl", videoUrl);
        if (fragment == null) {
            fragment = new MovieDetailsFragment();
            fragment.setArguments(b);
        }
        replaceFragment(fragment);*/
    }

    public void setTrailerDetailsScreen(String id, String videoUrl) {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(FragmentNames.FRAGMENT_TRAILER_DETAILS);
        Bundle b = new Bundle();
        b.putInt("playerType", YouTubePlayerType.STRICT_NATIVE);
        b.putString("id", id);
        b.putString("videoUrl", videoUrl);
        if (fragment == null) {
            fragment = new TrailerDetailsFragment();
            fragment.setArguments(b);
        }
        replaceFragment(fragment);
    }

    public void setDownloadScreen() {
        String locationName = UniversalPreferences.getInstance().get(PREFS_LOCATION_NAME, "");
        String url = AppConfig.BASE + locationName;
        Log.e("Url", "------>" + url);
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }


    public void replaceFragment(Fragment fragment) {
        // this for when we remove or add a fragment or change a fragment
        String backStateName = fragment.getClass().getName();
        Log.e("Back Stack Name", "-->" + backStateName);
        boolean fragmentPopped = getSupportFragmentManager().popBackStackImmediate(backStateName, 0);
        Log.e("Fragment Popped Up", "-->" + fragmentPopped);

        if (!fragmentPopped) {
            //fragment not in back stack, create it.
            FragmentTransaction ft = this.getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.container, fragment);
            ft.addToBackStack(backStateName);
            ft.commit();
        }
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
            Log.e("fragmentStack --->", "Previous List");
            getSupportFragmentManager().popBackStack();
        } else {
            if (doubleBackToExitPressedOnce) {
                finish();
            } else {
                this.doubleBackToExitPressedOnce = true;
                Utility.showNormalToast(this, ErrorMsg.BACK_KEY_MSG);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        doubleBackToExitPressedOnce = false;
                    }
                }, 2000);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_splash_screen, menu);
        String countStr = UniversalPreferences.getInstance().get(PREFS_CART_COUNT, "");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_search:
                setSearchScreen();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void showDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_logout);
        ImageView cancel = dialog.findViewById(R.id.close);

        cancel.setOnClickListener(new View.OnClickListener() {
            @SuppressLint({"LongLogTag", "SetTextI18n"})
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        Typeface myFont = Typeface.createFromAsset(getResources().getAssets(), "fonts/Roboto-Medium.ttf");
        Button okBtn = dialog.findViewById(R.id.okBtn);
        TextView titledialogTXT = dialog.findViewById(R.id.titledialogTXT);
        LinearLayout radioLYT = dialog.findViewById(R.id.radioLYT);
        Button cancelBtn = dialog.findViewById(R.id.cancelBtn);

        ChangeTypeFace.overrideMavenBoldFont(HomeScreen.this, radioLYT);
        ChangeTypeFace.overrideMavenBoldFont(HomeScreen.this, titledialogTXT);

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UniversalPreferences.getInstance().clear();
                setProfileData();
                setDashboardScreen();
                Utility.showSuccessToast(HomeScreen.this, "Successfully Logged out");
                dialog.dismiss();
            }
        });

        dialog.show();
    }
}
