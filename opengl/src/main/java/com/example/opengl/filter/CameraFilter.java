package com.example.opengl.filter;

import android.content.Context;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;

import com.example.opengl.OpenUtil;
import com.example.opengl.R;

/**
 * Created by hongqian.better@outlook.com
 * on 2021/5/1
 */
public class CameraFilter extends AbstractFilter {
    int[] mFrameBuffer;
    int[] mFrameBufferTextures;

    public CameraFilter(Context context) {
        super(context, R.raw.camera_vertex, R.raw.camera_frag);
    }


    //不需要显示到屏幕上
    @Override
    protected void initCoordinate() {

        mGLTextureBuffer.clear();
        //纹理世界坐标系
        //                float[] t = { 0.0f, 1.0f,   左上
        //                        1.0f, 1.0f,          右上
        //                        0.0f, 0.0f,          左下
        //                        1.0f, 0.0f};          右下
        //旋转   180.c
        //                float[] t = {1.0f, 0.0f,
        //                        0.0f, 0.0f,
        //                        1.0f, 1.0f,
        //                        0.0f, 1.0f};
        //镜像  翻转
        float[] TEXTURE = {0.0f, 0.0f,
                1.0f, 0.0f,
                0.0f, 1.0f,
                1.0f, 1.0f
        };

        mGLTextureBuffer.put(TEXTURE);

    }

    @Override
    public void onReady(int width, int height) {
        super.onReady(width, height);
        //FBO的创建(缓存) 离屏屏幕
        mFrameBuffer = new int[1];
        //创建一个fbo  并把FBO的id赋值给数组   0:从数组的第几个元素开始
        GLES20.glGenFramebuffers(mFrameBuffer.length, mFrameBuffer, 0);
        //创建属于FBO的纹理
        mFrameBufferTextures = new int[1]; //用来记录纹理Id
        //创建纹理
        OpenUtil.glGenTextures(mFrameBufferTextures);

        //让fbo与 纹理发生关系
        //创建一个 2d的图像
        // 目标 2d纹理+等级 + 格式 +宽、高+ 格式 + 数据类型(byte) + 像素数据
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,mFrameBufferTextures[0]);
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D,0,GLES20.GL_RGBA,mOutputWidth,mOutputHeight,
                0,GLES20.GL_RGBA,GLES20.GL_UNSIGNED_BYTE, null);
        // 让fbo与纹理绑定起来 ， 后续的操作就是在操作fbo与这个纹理上了
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER,mFrameBuffer[0]);
        GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER,GLES20.GL_COLOR_ATTACHMENT0,
                GLES20.GL_TEXTURE_2D, mFrameBufferTextures[0], 0);
        //解绑
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,0);
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER,0);
    }



    @Override
    public int onDrawFrame(int textureId) {
        //设置显示窗口
        GLES20.glViewport(0, 0, mOutputWidth, mOutputHeight);

        //不调用的话就是默认的操作glsurfaceview中的纹理了。显示到屏幕上了
        //这里我们还只是把它画到fbo中(缓存)
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER,mFrameBuffer[0]);

        //使用着色器
        GLES20.glUseProgram(mGLProgramId);

        //传递坐标
        mGLVertexBuffer.position(0);
        GLES20.glVertexAttribPointer(vPosition, 2, GLES20.GL_FLOAT, false, 0, mGLVertexBuffer);
        GLES20.glEnableVertexAttribArray(vPosition);

        mGLTextureBuffer.position(0);
        GLES20.glVertexAttribPointer(vCoord, 2, GLES20.GL_FLOAT, false, 0, mGLTextureBuffer);
        GLES20.glEnableVertexAttribArray(vCoord);

        //变换矩阵
        GLES20.glUniformMatrix4fv(vMatrix,1,false,matrix,0);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        //因为这一层是摄像头后的第一层，所以需要使用扩展的  GL_TEXTURE_EXTERNAL_OES
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textureId);
        GLES20.glUniform1i(vTexture, 0);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);

        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,0);
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER,0);
        //返回fbo的纹理id
        return mFrameBufferTextures[0];
    }


    private float[] matrix;


    public void setMatrix(float[] matrix) {
        this.matrix = matrix;
    }

    public void destroyFrameBuffers() {
        //删除fbo的纹理
        if (mFrameBufferTextures != null) {
            GLES20.glDeleteTextures(1, mFrameBufferTextures, 0);
            mFrameBufferTextures = null;
        }
        //删除fbo
        if (mFrameBuffer != null) {
            GLES20.glDeleteFramebuffers(1, mFrameBuffer, 0);
            mFrameBuffer = null;
        }
    }

    @Override
    public void release() {
        super.release();
        destroyFrameBuffers();
    }
}
