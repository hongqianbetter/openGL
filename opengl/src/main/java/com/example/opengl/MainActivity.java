package com.example.opengl;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.RadioGroup;

import com.example.opengl.widget.RecordButton;

import java.io.File;

public class MainActivity extends Activity {

    private String[] permiss = {Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE};
    OpenGLView douyinView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActivityCompat.requestPermissions(this, permiss, 321);



        douyinView = findViewById(R.id.douyinView);

        RecordButton recordButton = findViewById(R.id.btn_record);
        recordButton.setOnRecordListener(new RecordButton.OnRecordListener() {
            /**
             * 开始录制
             */
            @Override
            public void onRecordStart() {
                    L.detail("开始录制");
                douyinView.startRecord();
            }

            /**
             * 停止录制
             */
            @Override
            public void onRecordStop() {
                L.detail("停止录制");
                douyinView.stopRecord();
                SecondActivity.start(MainActivity.this);
            }
        });
        douyinView.setSpeed(OpenGLView.Speed.MODE_NORMAL);
        RadioGroup radioGroup = findViewById(R.id.rg_speed);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            /**
             * 选择录制模式
             * @param group
             * @param checkedId
             */
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.rb_extra_slow: //极慢
                        douyinView.setSpeed(OpenGLView.Speed.MODE_EXTRA_SLOW);
                        break;
                    case R.id.rb_slow:
                        douyinView.setSpeed(OpenGLView.Speed.MODE_SLOW);
                        break;
                    case R.id.rb_normal:
                        douyinView.setSpeed(OpenGLView.Speed.MODE_NORMAL);
                        break;
                    case R.id.rb_fast:
                        douyinView.setSpeed(OpenGLView.Speed.MODE_FAST);
                        break;
                    case R.id.rb_extra_fast: //极快
                        douyinView.setSpeed(OpenGLView.Speed.MODE_EXTRA_FAST);
                        break;
                }
            }
        });
    }


}