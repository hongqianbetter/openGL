package com.example.opengl.face;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;

import com.example.opengl.L;
import com.example.opengl.util.CameraHelper;


/**
 * Created by Lance on 2017/11/10.
 * 人脸 与 关键点的 定位追踪 api 类
 */
public class FaceTrack {


    static {
        System.loadLibrary("native-lib");
    }

    private CameraHelper mCameraHelper;

    private Handler mHandler;
    private HandlerThread mHandlerThread;

    private long self;
    //结果
    private Face mFace;

    public FaceTrack(String model, String seeta, CameraHelper cameraHelper) {
        mCameraHelper = cameraHelper;
        self = native_create(model, seeta);
        mHandlerThread = new HandlerThread("track");
        mHandlerThread.start();
        mHandler = new Handler(mHandlerThread.getLooper()) {
            @Override
            public void handleMessage(Message msg) {
                //子线程 耗时再久 也不会对其他地方 (如：opengl绘制线程) 产生影响
                synchronized (FaceTrack.this) {
                    //定位 线程中检测
                    mFace = native_detector(self, (byte[]) msg.obj,
                            mCameraHelper.mCameraId, CameraHelper.mWidth, CameraHelper.mHeight);

                    if(null!=mFace) {
                        L.detail(mFace.toString()+"检测到人脸");
                    }

                }
            }
        };
    }

    public void startTrack() {
        native_start(self);
    }

    public void stopTrack() {
        synchronized (this) {
            mHandlerThread.quitSafely();
            mHandler.removeCallbacksAndMessages(null);
            native_stop(self);
            self = 0;
        }
    }

    // 并不是每一张图像都会被检测到
    //当前正在检测(执行handleMessage) 又来了一个

    public void detector(byte[] data) {
        //把积压的 11号任务移除掉
        mHandler.removeMessages(11);
        //加入新的11号任务
        Message message = mHandler.obtainMessage(11);
        message.obj = data;
        mHandler.sendMessage(message);
    }

    public Face getFace() {
        return mFace;
    }


    private native long native_create(String model, String seeta);

    private native void native_start(long self);

    private native void native_stop(long self);

    private native Face native_detector(long self, byte[] data, int cameraId, int width, int
            height);


}
