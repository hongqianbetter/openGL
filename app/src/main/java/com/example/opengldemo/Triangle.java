package com.example.opengldemo;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Created by hongqian.better@outlook.com
 * on 2021/4/27
 */
public class Triangle {


    private final String vertexShaderCode =
            "attribute vec4 vPosition;" +  // 应用程序传入顶点着色器的顶点位置
                    "void main() {" +
                    "  gl_Position = vPosition;" + // 设置此次绘制此顶点位置
                    "}";

    /**
     * 片元着色器代码
     */
    private final String fragmentShaderCode =
            "precision mediump float;" +  // 设置工作精度
                    "uniform vec4 vColor;" +  // 应用程序传入着色器的颜色变量
                    "void main() {" +
                    "  gl_FragColor = vColor;" + // 颜色值传给 gl_FragColor内建变量，完成片元的着色
                    "}";

    private final int mProgram;
    // 绘制形状的顶点数量
    private FloatBuffer vertexBuffer;

    // 坐标数组中的顶点坐标个数
    static float triangle[] = {   // 以逆时针顺序;
            0.0f, 1.0f,2f, // top
            -1.0f, -1.0f,3f,// bottom left
            1.0f, -1.0f,4f   // bottom right
    };

    // Set color with red, green, blue and alpha (opacity) values
    float color[] = {1, 0, 0, 1.0f};


    public Triangle() {
        //我们已经完成了顶点的定义，但是，在OpenGL可以存取它们之前，我们仍然需要完成另一步。
        // 主要的问题是这些代码运行的环境与OpenGL运行的环境使用不同的语言。运行在
        // Dalvik虚拟机上的代码不能直接访问本地环境，而OpenGL作为本地系统又是直接运行
        // 在硬件上的；所以这时我们需要使用Java一个特殊的缓冲区类集合，它可以分配本地内存
        // 块，并且把Java的数据复制到本地内存。本地内存就可以被本地环境存取，而且不受垃
        // 圾回收器的管控
        // 因为 ByteBuffer 是将数据移进移出通道的唯一方式使用，

        // 初始化形状中顶点坐标数据的字节缓冲区
        // 通过 ByteBuffer的allocateDirect()方法获取到 ByteBuffer 实例
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(
                // 顶点坐标个数 * 坐标数据类型 float 一个是 4 bytes
                triangle.length * 4
        );
        // 设置缓冲区使用设备硬件的原本字节顺序进行读取;
        byteBuffer.order(ByteOrder.nativeOrder());
        // ByteBuffer 是将数据移进移出通道的唯一方式，这里使用 “as” 方法从 ByteBuffer 中获得一个基本类型缓冲区
        vertexBuffer = byteBuffer.asFloatBuffer();
        // 把顶点坐标信息数组存储到 FloatBuffer
        vertexBuffer.put(triangle);
        // 设置从缓冲区的第一个位置开始读取顶点坐标信息
        vertexBuffer.position(0);
        // 加载编译顶点渲染器
        int vertexShader = GLES20.glCreateShader(GLES20.GL_VERTEX_SHADER);
        //加载shader代码 glShaderSource 和 glCompileShader
        GLES20.glShaderSource(vertexShader, vertexShaderCode);
        GLES20.glCompileShader(vertexShader);
        // 加载编译片元渲染器
        int fragmentShader = GLES20.glCreateShader(GLES20.GL_FRAGMENT_SHADER);
        //加载shader代码 glShaderSource 和 glCompileShader
        GLES20.glShaderSource(fragmentShader, fragmentShaderCode);
        GLES20.glCompileShader(fragmentShader);
        // 创建空的程式 - create empty OpenGL ES Program
        mProgram = GLES20.glCreateProgram();
        // attach shader 代码 - add the vertex shader to program
        GLES20.glAttachShader(mProgram, vertexShader);
        // attach shader 代码 - add the fragment shader to program
        GLES20.glAttachShader(mProgram, fragmentShader);
        //链接着色器程序
        GLES20.glLinkProgram(mProgram);
        int[] status = new int[1];
        //获得程序是否配置成功
        GLES20.glGetProgramiv(mProgram, GLES20.GL_LINK_STATUS, status, 0);
        if (status[0] != GLES20.GL_TRUE) {
            throw new IllegalStateException("ScreenFilter 着色器程序配置失败!");
        }
        //因为已经塞到着色器程序中了，所以删了没关系
        GLES20.glDeleteShader(fragmentShader);
        GLES20.glDeleteShader(vertexShader);
        // 链接GLSL程式 - creates OpenGL ES program executables
    }


    private int mPositionHandle; //变量 用于存取attribute修饰的变量的位置编号
    private int mColorHandle; //变量 用于存取uniform修饰的变量的位置编号

    public void draw() {
        // 使用GLSL程式 - Add program to OpenGL ES environment
        GLES20.glUseProgram(mProgram);
        // 获取shader代码中的变量索引 get handle to vertex shader's vPosition member
        // Java代码中需要获取shader代码中定义的变量索引，用于在后面的绘制代码中进行赋值
        // 变量索引在GLSL程式生命周期内（链接之后和销毁之前）都是固定的，只需获取一次
        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");

        // 绑定vertex坐标值 调用glVertexAttribPointer()告诉OpenGL，它可以在
        // 缓冲区vertexBuffer中获取vPosition的数据
        GLES20.glVertexAttribPointer(mPositionHandle, 2,
                GLES20.GL_FLOAT, false,
        12, vertexBuffer);
        // 启用vertex Enable a handle to the triangle vertices
        GLES20.glEnableVertexAttribArray(mPositionHandle);

        // get handle to fragment shader's vColor member
        mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");

        // Set color for drawing the triangle
        GLES20.glUniform4fv(mColorHandle, 1, color, 0);

        // 通过 GLES20.glDrawArrays 或者 GLES20.glDrawElements 开始绘制
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 3);

        // Disable vertex array
        GLES20.glDisableVertexAttribArray(mPositionHandle);
    }


    //    /**
    //     * 加载并编译着色器代码
    //     * 渲染器类型type={GLES20.GL_VERTEX_SHADER, GLES20.GL_FRAGMENT_SHADER}
    //     * 渲染器代码 GLSL
    //     */
    //    public static int loadShader(int type, String shaderCode) {
    //
    //        int shader = GLES20.glCreateShader(type);
    //        //加载shader代码 glShaderSource 和 glCompileShader
    //        GLES20.glShaderSource(shader, shaderCode);
    //        GLES20.glCompileShader(shader);
    //
    //        return shader;
    //    }

}
