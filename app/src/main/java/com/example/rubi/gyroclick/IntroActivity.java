package com.example.rubi.gyroclick;

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
        sliderPage.setTitle("title");
        sliderPage.setDescription("desc");
//        sliderPage.setImageDrawable("image");
//        sliderPage.setBgColor();
        addSlide(AppIntroFragment.newInstance(sliderPage));


        SliderPage sliderPage2 = new SliderPage();
        sliderPage2.setTitle("title");
        sliderPage2.setDescription("desc");
//        sliderPage.setImageDrawable("image");
        sliderPage2.setBgColor(Color.rgb(1,12,22));
        addSlide(AppIntroFragment.newInstance(sliderPage2));



        // OPTIONAL METHODS
        // Override bar/separator color.
        setBarColor(Color.parseColor("#3F51B5"));
        setSeparatorColor(Color.parseColor("#2196F3"));

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
            super.onBackPressed();
            finish();
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