package com.example.opengl;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.VideoView;

public class SecondActivity extends Activity {

    public static void start(Context mContext){
        Intent intent = new Intent(mContext, SecondActivity.class);
        mContext.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);


       VideoView videoView = findViewById(R.id.videoView);
        String path = getCacheDir()+"/a.mp4";
       videoView.setVideoPath(path);

       videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
           @Override
           public void onPrepared(MediaPlayer mp) {
                videoView.start();
           }
       });
    }
}