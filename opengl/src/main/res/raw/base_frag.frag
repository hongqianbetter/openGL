

//指明float数据是什么精度的 高中低 三档
precision mediump float;

//采样点的坐标 (x,Y)
varying vec2 aCoord;

uniform sampler2D vTexture;

void main(){
    //变量 接收像素值
    // texture2D：纹理采样函数 采集 aCoord的像素
    //赋值给 gl_FragColor 就可以了
    gl_FragColor = texture2D(vTexture,aCoord);
    //    gl_FragColor = vec4(1，0，0，0);
}