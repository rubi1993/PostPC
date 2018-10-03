package com.GIR.rubi.gyroclick;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;

import java.sql.Connection;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        System.out.println("onCreate for Splash");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);


        //  Initialize SharedPreferences
        SharedPreferences getPrefs = PreferenceManager
                .getDefaultSharedPreferences(getBaseContext());

        //  Create a new boolean and preference and set it to true
        boolean isFirstStart = getPrefs.getBoolean("firstStart", true);

//        isFirstStart = true;

        //  If the activity has never started before...
        if (isFirstStart) {

            //  Launch app intro

            Intent myIntent = new Intent(SplashScreenActivity.this, MainActivity.class);
            SplashScreenActivity.this.startActivity(myIntent);
            final Intent i = new Intent(SplashScreenActivity.this, IntroActivity.class);
            startActivity(i);
            SharedPreferences.Editor e = getPrefs.edit();
            e.putBoolean("firstStart", false);
            e.apply();
        } else if (ConnectionData.IP_ADDR != "\"0.0.0.0\"") {
            Intent intent = new Intent(getApplicationContext(),
                    MainActivity.class);
            SplashScreenActivity.this.startActivity(intent);
        }
        else {
            Intent intent = new Intent(getApplicationContext(),
                    QRScanner.class);
            SplashScreenActivity.this.startActivity(intent);
        }

        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // finish the splash activity so it can't be returned to
                SplashScreenActivity.this.finish();
                // create an Intent that will start the second activity
                Intent mainIntent = new Intent(SplashScreenActivity.this, SplashScreenActivity.class);
                SplashScreenActivity.this.startActivity(mainIntent);
            }
        }, 3000); // 3000 milliseconds
    }

    @Override
    public void onBackPressed() {
        System.out.println("onBackPressed - SplashScreen");
        super.onBackPressed();
        finishAffinity();
    }
}
