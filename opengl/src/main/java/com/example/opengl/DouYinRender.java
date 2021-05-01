package com.example.opengl;

import android.app.Activity;
import android.content.Context;
import android.graphics.SurfaceTexture;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import com.example.opengl.filter.CameraFilter;
import com.example.opengl.filter.ScreenFilter;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by hongqian.better@outlook.com
 * on 2021/4/21
 */
public class DouYinRender implements GLSurfaceView.Renderer, SurfaceTexture.OnFrameAvailableListener {
    Activity context;
    OpenGLView openGLView;
    CameraHelper cameraHelper;
    int[] textures;
    SurfaceTexture surfaceTexture;
    ScreenFilter screenFilter;
    CameraFilter cameraFilter;

    public DouYinRender(Context context, OpenGLView openGLView) {
        this.context = (Activity) context;
        this.openGLView = openGLView;
        this.openGLView.getContext().getResources();
    }

    //surface被创建后需要做的处理   //异步线程
    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {

        cameraFilter = new CameraFilter(context);
        //必须要再GL线程里面才能操作openGL
        screenFilter = new ScreenFilter(context);

        cameraHelper = new CameraHelper(context);
        //准备好摄像头绘制的画布
        //通过openGL创建一个纹理id
        textures = new int[1]; //可以一次创建多个纹理(画布) 创建纹理id  保存的数组  从数组的哪一个开始
        GLES20.glGenTextures(textures.length, textures, 0);
        surfaceTexture = new SurfaceTexture(textures[0]);
        surfaceTexture.setOnFrameAvailableListener(this);

    }

    //openGL是一个高级画笔 将图像数据进行改变  再输出到屏幕显示

    //异步线程  // 渲染窗口大小发生改变或者屏幕方法发生变化时候回调
    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        cameraHelper.startPreview(surfaceTexture);
        screenFilter.onReady(width, height);
        cameraFilter.onReady(width,height);

    }

    float[] mix = new float[16];

    //onFrameAvailable中的requestRender后走这里  //异步线程
    @Override
    public void onDrawFrame(GL10 gl) { //绘制方法
        //清理屏幕 ：glClearColor()-设置清空屏幕用的颜色，接收四个参数分别是：红色、绿色、蓝色和透明度分量，
        // 0表示透明，1.0f相反；
        GLES20.glClearColor(1, 0, 0, 0);   //Alpha 好像不起作用
        //清空屏幕，清空屏幕后调用glClearColor(）中设置的颜色填充屏幕；
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        //以上代码把底色置为了红色   同时注意window的颜色是黑色的

        //先把摄像头的数据先输出来
        //更新纹理 然后我们才能够使用OpenGL 从SurfaceTexture中获得数据进行渲染
        surfaceTexture.updateTexImage();
        //
        //       当从OpenGL ES的纹理对象取样时，首先应该调用getTransformMatrix()
        //       来转换纹理坐标。每次updateTexImage()被调用时，纹理矩阵都可能发生变化。所以，
        //       每次texture image被更新时，getTransformMatrix ()也应该被调用。

        cameraFilter.setMatrix(mix);
        int id=  cameraFilter.onDrawFrame(textures[0]);
//    screenFilter.onDrawFrame(textures[0], mix);

    }

    //当SurfaceTexture有一个新的图片的时候回调 可能仅仅作用回调时机  省电
    @Override
    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
        openGLView.requestRender();
    }
}
