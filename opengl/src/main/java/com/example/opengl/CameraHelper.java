package com.example.opengl;

import android.app.Activity;
import android.content.Context;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;

import java.util.Iterator;
import java.util.List;

/**
 * Created by hongqian.better@outlook.com
 * on 2021/4/21
 */
public class CameraHelper implements Camera.PreviewCallback {
    Context mContext;
    private byte[] buffer;
    public Camera mCamera;
    //TV should 默认打开device 0
    private int mCameraID = Camera.CameraInfo.CAMERA_FACING_BACK;
    int mWidth = 1920;
    int mHeight = 1080;

    int mCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;

    Activity mActivity;

    public CameraHelper(Activity activity) {
        this.mActivity = activity;
    }

    public void startPreview(SurfaceTexture texture) {
        try {
            //获得camera对象
            mCamera = Camera.open(mCameraId);
            //配置camera的属性
            Camera.Parameters parameters = mCamera.getParameters();
            //设置预览数据格式为nv21
            parameters.setPreviewFormat(ImageFormat.NV21);
            //这是摄像头宽、高
            setPreviewSize(parameters);
            // 设置摄像头 图像传感器的角度、方向
            setPreviewOrientation(parameters);
            mCamera.setParameters(parameters);
            buffer = new byte[mWidth * mHeight * 3 / 2];

//            //数据缓存区  为了给外部回调摄像头数据
//            mCamera.addCallbackBuffer(buffer);
//            mCamera.setPreviewCallbackWithBuffer(this);

            //设置预览画面
            mCamera.setPreviewTexture(texture);
            mCamera.startPreview();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    public void stopPreview() {
        if (mCamera != null) {
            //预览数据回调接口
            mCamera.setPreviewCallback(null);
            //停止预览
            mCamera.stopPreview();
            //释放摄像头
            mCamera.release();
            mCamera = null;
        }
    }

    private void setPreviewSize(Camera.Parameters parameters) {
        //获取摄像头支持的宽、高
        List<Camera.Size> supportedPreviewSizes = parameters.getSupportedPreviewSizes();
        Camera.Size size = supportedPreviewSizes.get(0);
        //选择一个与设置的差距最小的支持分辨率
        // 10x10 20x20 30x30
        // 12x12
        int m = Math.abs(size.height * size.width - mWidth * mHeight);
        supportedPreviewSizes.remove(0);
        Iterator<Camera.Size> iterator = supportedPreviewSizes.iterator();
        //遍历
        while (iterator.hasNext()) {
            Camera.Size next = iterator.next();
            int n = Math.abs(next.height * next.width - mWidth * mHeight);
            if (n < m) {
                m = n;
                size = next;
            }
        }
        mWidth = size.width;
        mHeight = size.height;
        parameters.setPreviewSize(mWidth, mHeight);
        L.detail("设置预览分辨率 width:" + size.width + " height:" + size.height);
    }

    private int mRotation;

    private void setPreviewOrientation(Camera.Parameters parameters) {
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(mCameraId, info);
        mRotation = mActivity.getWindowManager().getDefaultDisplay().getRotation();
        int degrees = 0;
        switch (mRotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90: // 横屏 左边是头部(home键在右边)
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:// 横屏 头部在右边
                degrees = 270;
                break;
        }
        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360; // compensate the mirror
        } else { // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        //设置角度
        mCamera.setDisplayOrientation(result);
    }


//    Camera.PreviewCallback mPreviewCallback;
//
//    public void setPreviewCallback(Camera.PreviewCallback previewCallback) {
//        mPreviewCallback = previewCallback;
//    }
//
//
    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        // data数据依然是倒的
//        mPreviewCallback.onPreviewFrame(data, camera);
//        camera.addCallbackBuffer(buffer);
    }
}
