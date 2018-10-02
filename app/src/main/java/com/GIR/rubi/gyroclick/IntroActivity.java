package com.GIR.rubi.gyroclick;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;
import com.github.paolorotolo.appintro.model.SliderPage;

public class IntroActivity extends AppIntro {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Note here that we DO NOT use setContentView();

        // Add your slide fragments here.
        // AppIntro will automatically generate the dots indicator and buttons.
//        addSlide(new SampleSlide());
        // Instead of fragments, you can also use our default slide.
        // Just create a `SliderPage` and provide title, description, background and image.
        // AppIntro will do the rest.

        SliderPage sliderPage = new SliderPage();
        sliderPage.setTitle("Welcome to GyroClick app!");
        sliderPage.setDescription("Easy and intuitive control over your mouse\nWith GyroClick, you can control over your mouse in an intuitive, secure and " +
                "fun way.");
        sliderPage.setImageDrawable(R.drawable.logo_splash_white);
//        sliderPage.setBgColor();
        addSlide(AppIntroFragment.newInstance(sliderPage));


        SliderPage sliderPage2 = new SliderPage();
        sliderPage2.setTitle("Start by scanning the QR code");
        sliderPage2.setDescription("In order to connect to a host PC, you will need to open the GyroClick Desktop app " +
                        "and scan the QR code that will be generated for you");
        sliderPage2.setImageDrawable(R.drawable.barcode_splash);
        sliderPage2.setBgColor(Color.parseColor("#BDEEF2"));
        sliderPage2.setDescColor(Color.parseColor("#4d4d4d"));
        sliderPage2.setTitleColor(Color.parseColor("#4d4d4d"));
        addSlide(AppIntroFragment.newInstance(sliderPage2));



        SliderPage sliderPage3 = new SliderPage();
        sliderPage3.setTitle("Rotate to control");
        sliderPage3.setDescription("Rotate your smartphone in all axis and discover the ease of controlling your PC");
        sliderPage3.setImageDrawable(R.drawable.rotate_splash);
        sliderPage3.setBgColor(Color.parseColor("#B9E196"));
        sliderPage3.setDescColor(Color.parseColor("#000000"));
        sliderPage3.setTitleColor(Color.parseColor("#000000"));
        addSlide(AppIntroFragment.newInstance(sliderPage3));


        // OPTIONAL METHODS
        // Override bar/separator color.
//        setBarColor(Color.parseColor("#3F51B5"));

        setSeparatorColor(Color.parseColor("#8ACBEE"));

        // Hide Skip/Done button.
        showSkipButton(true);
        setProgressButtonEnabled(true);

        // Turn vibration on and set intensity.
        // NOTE: you will probably need to ask VIBRATE permission in Manifest.
        setVibrate(false);
//        setVibrateIntensity(30);
    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        // Do something when users tap on Skip button.
        Intent myIntent = new Intent(IntroActivity.this, QRScanner.class);
//        myIntent.putExtra("key", value); //Optional parameters
        IntroActivity.this.startActivity(myIntent);
    }

    @Override
    public void onBackPressed() {
            System.out.println("is it here");
            super.onBackPressed();
            finishAffinity();
        }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        // Do something when users tap on Done button.
        Intent myIntent = new Intent(IntroActivity.this, QRScanner.class);
        IntroActivity.this.startActivity(myIntent);


    }

    @Override
    public void onSlideChanged(@Nullable Fragment oldFragment, @Nullable Fragment newFragment) {
        super.onSlideChanged(oldFragment, newFragment);
        // Do something when the slide changes.
    }
}