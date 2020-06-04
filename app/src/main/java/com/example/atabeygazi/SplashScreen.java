package com.example.atabeygazi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class SplashScreen extends AppCompatActivity {

    private ImageView icon;
    private RelativeLayout splashScreen;
    private CountDownTimer countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        icon = findViewById(R.id.icon);
        splashScreen = findViewById(R.id.splashScreen);
        splashScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(countDownTimer != null) {
                    countDownTimer.cancel();
                    startMain();
                }
            }
        });
        Animation aniRotate = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.pulse);
        icon.startAnimation(aniRotate);
        countDownTimer = new CountDownTimer(5000, 1000) {
            public void onTick(long millisUntilFinished) {

            }
            public void onFinish() {
                startMain();
            }
        }.start();
    }

    private void startMain(){
        Intent intent = new Intent(SplashScreen.this, MainActivity.class);
        startActivity(intent);
        this.finish();
    }



    @Override
    public void onBackPressed() {
        System.exit(0);
    }
}
