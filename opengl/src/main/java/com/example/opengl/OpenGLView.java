package com.example.opengl;

import android.app.Activity;
import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

/**
 * Created by hongqian.better@outlook.com
 * on 2021/4/21
 */
public class OpenGLView extends GLSurfaceView {
    DouYinRender douYinRender;
    public OpenGLView(Context context) {
        super(context);
    }

    public OpenGLView(Context context, AttributeSet attrs) {
        super(context, attrs);

        _init(context);
    }

    private void _init(Context context) {
        //设置EGL版本
        setEGLContextClientVersion(2);
         douYinRender = new DouYinRender((Activity) context, this);
        setRenderer(douYinRender);


        //设置按需渲染 当我们调用requestRender 请求GLThread 会掉一次 onDrawFrame
        setRenderMode(RENDERMODE_WHEN_DIRTY);


    }

    //默认正常速度
    private Speed mSpeed = Speed.MODE_NORMAL;

    public enum Speed {
        MODE_EXTRA_SLOW, MODE_SLOW, MODE_NORMAL, MODE_FAST, MODE_EXTRA_FAST
    }


    public void setSpeed(Speed speed){
        mSpeed = speed;
    }

    public void startRecord() {
        float speed = 1.f;
        switch (mSpeed) {
            case MODE_EXTRA_SLOW:
                speed = 0.3f;
                break;
            case MODE_SLOW:
                speed = 0.5f;
                break;
            case MODE_NORMAL:
                speed = 1.f;
                break;
            case MODE_FAST:
                speed = 1.5f;
                break;
            case MODE_EXTRA_FAST:
                speed = 3.f;
                break;
        }
        douYinRender.startRecord(speed);
    }

    public void stopRecord() {
        douYinRender.stopRecord();
    }
}
