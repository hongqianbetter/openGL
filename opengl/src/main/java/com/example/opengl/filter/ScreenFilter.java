package com.example.opengl.filter;

import android.content.Context;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;

import com.example.opengl.L;
import com.example.opengl.OpenUtil;
import com.example.opengl.R;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Created by hongqian.better@outlook.com
 * on 2021/4/21
 */
public class ScreenFilter extends AbstractFilter{
    private FloatBuffer mTextureBuffer;
    private FloatBuffer mVertexBuffer;
    private int vTexture;
    private int vMatrix;
    private int vCoord;
    private int vPosition;
    private int mProgram;
    private int mWidth;
    private int mHeight;

    public ScreenFilter(Context context) {
        super(context,R.raw.camera_vertex, R.raw.camera_frag);

    }


    /**
     * 使用着色器程序进行 画画
     */
    public void onDrawFrame(int textureId, float[] mtx) {
        //1、设置窗口大小
        //画画的时候 你的画布可以看成 10x10，也可以看成5x5 等等
        //设置画布的大小，然后画画的时候， 画布越大，你画上去的图像就会显得越小
        // x与y 就是从画布的哪个位置开始画
        GLES20.glViewport(0, 0, mWidth, mHeight);

        //使用着色器程序
        GLES20.glUseProgram(mProgram);

        // 怎么画？ 其实就是传值
        //2：xy两个数据 float的类型
        //1、将顶点数据传入，确定形状
        mVertexBuffer.position(0);
        //通过索引获取变量的    给vPosition坐标传值  xy坐标所以长度是2 每次取两个数据   float类型 stride 指定连续通用顶点属性之间的字节偏移量。 如果stride为0，则通用顶点属性被理解为紧密打包在数组中的。 初始值为0。
        GLES20.glVertexAttribPointer(vPosition, 2, GLES20.GL_FLOAT, false, 0, mVertexBuffer);
        //传了数据之后 激活
        GLES20.glEnableVertexAttribArray(vPosition);

        //2、将纹理坐标传入，采样坐标
        mTextureBuffer.position(0);
        GLES20.glVertexAttribPointer(vCoord, 2, GLES20.GL_FLOAT, false, 0, mTextureBuffer);
//        允许使用顶点坐标数组
        GLES20.glEnableVertexAttribArray(vCoord);

        //3、变换矩阵
        GLES20.glUniformMatrix4fv(vMatrix, 1, false, mtx, 0);

        //片元 vTexture 绑定图像数据到采样器
        //激活图层   绑定纹理
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        // 图像数据
        // 正常：GLES20.GL_TEXTURE_2D
        // surfaceTexure的纹理需要
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textureId);
        //传递参数 0：需要和纹理层GL_TEXTURE0对应 设置uniform采样器采样纹理0的地方
        GLES20.glUniform1i(vTexture, 0);

        //参数传完了 通知opengl 画画 从第0点开始 共4个点  图形绘制
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);

    }

    public void onReady(int width, int height) {
        mWidth = width;
        mHeight = height;
    }

}
