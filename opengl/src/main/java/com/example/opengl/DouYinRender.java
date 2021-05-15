package com.example.opengl;

import android.app.Activity;
import android.content.Context;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.opengl.EGL14;
import android.opengl.EGLContext;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.example.opengl.face.FaceTrack;
import com.example.opengl.filter.AbstractFilter;
import com.example.opengl.filter.BigEysFilter;
import com.example.opengl.filter.CameraFilter;
import com.example.opengl.filter.ScreenFilter;
import com.example.opengl.record.MediaRecorder;
import com.example.opengl.util.CameraHelper;
import com.example.opengl.util.OpenGLUtils;
import com.example.opengl.util.Utils;

import java.io.File;
import java.io.IOException;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by hongqian.better@outlook.com
 * on 2021/4/21
 */
public class DouYinRender implements GLSurfaceView.Renderer, SurfaceTexture.OnFrameAvailableListener, Camera.PreviewCallback {
    private ScreenFilter mScreenFilter;
    BigEysFilter mBigEyeFilter;
    private OpenGLView mView;
    private CameraHelper mCameraHelper;
    private SurfaceTexture mSurfaceTexture;
    private float[] mtx = new float[16];
    private int[] mTextures;
    private CameraFilter mCameraFilter;
    Activity context;
    private MediaRecorder mMediaRecorder;
    private FaceTrack mFaceTrack;
//    private BigEyeFilter mBigEyeFilter;
    public DouYinRender(Activity context, OpenGLView douyinView) {
        mView = douyinView;
        this.context=context;

//        OpenGLUtils.copyAssets(context, "lbpcascade_frontalface.xml");
//
//
//        File file = new File(Utils.getExternalFileDir(context),"lbpcascade_frontalface.xml");
//        Log.e("XXX",file.getAbsolutePath());
//        if(!file.exists()) {
//            Log.e("XXX","不存在---");
//            Toast.makeText(context, "不存在", Toast.LENGTH_SHORT).show();
//        }else {
//            Log.e("XXX","存在---");
//            Toast.makeText(context, "存在", Toast.LENGTH_SHORT).show();
//        }


        //初始化跟踪器
//        init(Utils.getExternalFileDir(this)+"/lbpcascade_frontalface.xml");

//        拷贝 模型
        Utils.copyAssets2A(context, "lbpcascade_frontalface.xml");
        Utils.copyAssets2B(context, "seeta_fa_v1.1.bin");
    }

    /**
     * 画布创建好啦
     *
     * @param gl
     * @param config
     */
    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        //初始化的操作
        mCameraHelper = new CameraHelper( context);
        mCameraHelper.setPreviewCallback(this);
        //准备好摄像头绘制的画布
        //通过opengl创建一个纹理id
        mTextures = new int[1];
        //偷懒 这里可以不配置 （当然 配置了也可以）
        GLES20.glGenTextures(mTextures.length, mTextures, 0);
        mSurfaceTexture = new SurfaceTexture(mTextures[0]);
        //
        mSurfaceTexture.setOnFrameAvailableListener(this);
        //注意：必须在gl线程操作opengl
        mCameraFilter = new CameraFilter(mView.getContext());
        mScreenFilter = new ScreenFilter(mView.getContext());
         mBigEyeFilter = new BigEysFilter(mView.getContext());
        //渲染线程的EGL上下文
        EGLContext eglContext = EGL14.eglGetCurrentContext();

        String path = context.getCacheDir()+"/a.mp4";
        L.detail(path);
        mMediaRecorder = new MediaRecorder(mView.getContext(), path, CameraHelper.mHeight, CameraHelper.mWidth, eglContext);
    }

    /**
     * 画布发生了改变
     *
     * @param gl
     * @param width
     * @param height
     */
    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {

//      在这里   创建跟踪器
        mFaceTrack = new FaceTrack(context.getCacheDir()+"/A/lbpcascade_frontalface.xml",
                context.getCacheDir()+"/B/seeta_fa_v1.1.bin", mCameraHelper);
//        启动跟踪器
        mFaceTrack.startTrack();
        //开启预览
        mCameraHelper.startPreview(mSurfaceTexture);
        mCameraFilter.onReady(width,height);
        mBigEyeFilter.onReady(width,height);
        mScreenFilter.onReady(width,height);

    }

    /**
     * 开始画画吧
     *
     * @param gl
     */
    @Override
    public void onDrawFrame(GL10 gl) {
        // 配置屏幕
        //清理屏幕 :告诉opengl 需要把屏幕清理成什么颜色
        GLES20.glClearColor(0, 0, 0, 0);
        //执行上一个：glClearColor配置的屏幕颜色
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        // 把摄像头的数据先输出来
        // 更新纹理，然后我们才能够使用opengl从SurfaceTexure当中获得数据 进行渲染
        mSurfaceTexture.updateTexImage();
        //surfaceTexture 比较特殊，在opengl当中 使用的是特殊的采样器 samplerExternalOES （不是sampler2D）
        //获得变换矩阵
        mSurfaceTexture.getTransformMatrix(mtx);
        //
        mCameraFilter.setMatrix(mtx);
        //责任链
        int id = mCameraFilter.onDrawFrame(mTextures[0]);

        //加效果滤镜
        mBigEyeFilter.setFace(mFaceTrack.getFace());
        id = mBigEyeFilter.onDrawFrame(id);
        // id  = 效果1.onDrawFrame(id);
        // id = 效果2.onDrawFrame(id);
        //....
        //加完之后再显示到屏幕中去
        mScreenFilter.onDrawFrame(id);
        //进行录制
        mMediaRecorder.encodeFrame(id, mSurfaceTexture.getTimestamp());
    }

    /**
     * surfaceTexture 有一个有效的新数据的时候回调
     *
     * @param surfaceTexture
     */
    @Override
    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
        mView.requestRender();
    }

    public void onSurfaceDestroyed() {
        mCameraHelper.stopPreview();
        mFaceTrack.stopTrack();
    }

    public void startRecord(float speed) {
        try {
            mMediaRecorder.start(speed);
        } catch (IOException e) {
            e.printStackTrace();
            L.detail(e.getMessage());
        }
    }

    public void stopRecord() {
        mMediaRecorder.stop();
    }


    //如果是显示到surfaceholder  onPreviewFrame堵塞太久 是不会影响预览的 是开了子线程的

    //但是这里不能耗时太久 影响绘制
    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        //送去进行人脸回调与关键点回调
        mFaceTrack.detector(data);
    }
}
