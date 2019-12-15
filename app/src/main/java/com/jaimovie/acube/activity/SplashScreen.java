package com.jaimovie.acube.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.jaimovie.acube.R;
import com.jaimovie.acube.utils.ChangeTypeFace;
import com.zookey.universalpreferences.UniversalPreferences;

import static com.jaimovie.acube.constants.PreferenceName.PREFS_ISO;

public class SplashScreen extends AppCompatActivity {

    private final int SPLASH_DISPLAY_LENGTH = 3000;
    private static final String TAG = SplashScreen.class.getSimpleName();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);

        TextView createLabel = findViewById(R.id.createLabel);
        ChangeTypeFace.overrideMavenBoldFont(this, createLabel);
        String iso3Country = this.getResources().getConfiguration().locale.getISO3Country();

        UniversalPreferences.getInstance().put(PREFS_ISO, iso3Country);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                /* Create an Intent that will start the Menu-Activity. */
                Intent mainIntent = new Intent(SplashScreen.this, HomeScreen.class);
                SplashScreen.this.startActivity(mainIntent);
                SplashScreen.this.finish();
            }
        }, SPLASH_DISPLAY_LENGTH);
    }

}
