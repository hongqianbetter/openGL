#extension GL_OES_EGL_image_external : require
//SurfaceTexture比较特殊  加上openGL的扩展

//指明float数据是什么精度的 高中低 三档
precision mediump float;

//采样点的坐标 (x,Y)
varying vec2 aCoord;

//采样器  正常情况下是Sample2D采样器 但是SurfaceTexture比较特殊
uniform samplerExternalOES vTexture;

void main(){
    //变量 接收像素值
    // texture2D：采样器 采集 aCoord的像素
    //赋值给 gl_FragColor 就可以了
    gl_FragColor = texture2D(vTexture,aCoord);
//    gl_FragColor = vec4(1，0，0，0);
}