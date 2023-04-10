package com.osukastudios.mobileafia.Activity;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import com.airbnb.lottie.LottieAnimationView;
import android.widget.ImageView;
import com.osukastudios.mobileafia.R;

public class SplashScreenActivity extends AppCompatActivity{

    Handler h=new Handler();
    ImageView logo,appName;
    LottieAnimationView lottieAnimationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        logo=findViewById(R.id.logo);
        appName=findViewById(R.id.app_name);

        lottieAnimationView=findViewById(R.id.lottie);
        logo.animate().translationY(1400).setDuration(1000).setStartDelay(6000);
        appName.animate().translationY(1400).setDuration(1000).setStartDelay(6000);
        lottieAnimationView.animate().translationY(1400).setDuration(1000).setStartDelay(6000);

        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i= new Intent(SplashScreenActivity.this, NavigationActivity.class);
                startActivity(i);
                finish();
            }
        },8000);
    }
}