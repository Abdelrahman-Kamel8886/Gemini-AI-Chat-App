package com.abdok.geminichat.UI.Splash;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.abdok.geminichat.R;
import com.abdok.geminichat.UI.MainActivity;
import com.abdok.geminichat.databinding.ActivitySplashBinding;

public class SplashActivity extends AppCompatActivity {

    ActivitySplashBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding =  ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Animation rotateAnimation = AnimationUtils.loadAnimation(this, R.anim.rotate);


        rotateAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                // Animation start logic if needed
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // Start your main activity when animation ends
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                // Animation repeat logic if needed
            }
        });

        binding.splashLogo.startAnimation(rotateAnimation);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}