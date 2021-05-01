package com.example.opengl;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

/**
 * Created by hongqian.better@outlook.com
 * on 2021/4/21
 */
public class OpenGLView extends GLSurfaceView {

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
        setRenderer(new DouYinRender(context,this));


        //设置按需渲染 当我们调用requestRender 请求GLThread 会掉一次 onDrawFrame
        setRenderMode(RENDERMODE_WHEN_DIRTY);


    }
}
