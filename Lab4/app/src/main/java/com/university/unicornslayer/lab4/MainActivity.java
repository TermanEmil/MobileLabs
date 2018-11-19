package com.university.unicornslayer.lab4;

import android.graphics.drawable.AnimationDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    AnimationDrawable frameAnimation;
    ImageView img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        img = findViewById(R.id.loading_img);
        img.setBackgroundResource(R.drawable.loading_animation);
        frameAnimation = (AnimationDrawable) img.getBackground();
    }

    public void playAnim(View view) {
        frameAnimation.stop();
        frameAnimation.selectDrawable(0);
        frameAnimation.start();
    }
}
