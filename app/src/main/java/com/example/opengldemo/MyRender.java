package com.example.opengldemo;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by hongqian.better@outlook.com
 * on 2021/4/26
 */
public class MyRender implements GLSurfaceView.Renderer {
    Context context;
    GLSurfaceView myGLSurfaceView;

    private Triangle mTriangle;

    public MyRender(Context context, GLSurfaceView myGLSurfaceView) {
        this.context = context;
        this.myGLSurfaceView = myGLSurfaceView;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        //        screenFilter = new ScreenFilter();

        mTriangle = new Triangle();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClearColor(0, 0, 0, 0);   //Alpha 好像不起作用
        //清空屏幕，清空屏幕后调用glClearColor(）中设置的颜色填充屏幕；
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
//        screenFilter.draw();
        mTriangle.draw();
    }


}
