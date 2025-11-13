package com.example.myprojecttcz.screens;

import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.myprojecttcz.R;

public class Splash extends AppCompatActivity {
    private ProgressBar progressBar;
    private TextView splashtv;
    private ImageView myImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        myImageView = (ImageView) findViewById(R.id.imageView);
        splashtv = (TextView) findViewById(R.id.splahtv);
        Thread mSplashThread = new Thread() {
            @Override
            public void run() {
                try {
                    synchronized (this) {


                        Animation myFadeInAnimation = AnimationUtils.loadAnimation(com.example.myprojecttcz.screens.Splash.this, R.anim.tween);
                        myImageView.startAnimation(myFadeInAnimation);

                        wait(3000);
                    }
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
                finish();

                Intent intent = new Intent(com.example.myprojecttcz.screens.Splash.this, MainActivity.class);
                startActivity(intent);
            }
        };
        mSplashThread.start();

    }
}