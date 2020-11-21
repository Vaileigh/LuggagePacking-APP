package com.example.travel;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.VideoView;

public class MainActivity extends AppCompatActivity {
    private static int SPLASH_TIME_OUT = 6300;
    private Handler _handler = new Handler();
    VideoView videov;
    ImageView imagev;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //video
        videov = (VideoView) findViewById(R.id.videoView);
        imagev = (ImageView) findViewById(R.id.imageView);
        String videopath = "android.resource://com.example.travel/"+R.raw.intro;
        Uri uri = Uri.parse(videopath);
        videov.setVideoURI(uri);
        videov.start();
        videov.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                imagev.setVisibility(View.VISIBLE);
                return true;
            }
        });
        _handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                toIdentityActivity();
            }
        },SPLASH_TIME_OUT);

    }

    public void jump(View view) {
        _handler.removeCallbacksAndMessages(null);
        toIdentityActivity();
    }
    public void toIdentityActivity(){
        Intent homeIntent =
                new Intent(MainActivity.this, IdentityActivity.class);
        startActivity(homeIntent);
        finish();
    }
}
