

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

    //赋值给 gl_FragColor 就可以了  变成了灰度图
//        vec4 rgba = texture2D(vTexture,aCoord);
//        gl_FragColor = vec4((rgba.r*0.3+rgba.g *0.59+rgba.b*0.11),(rgba.r*0.3+rgba.g *0.59+rgba.b*0.11),(rgba.r*0.3+rgba.g *0.59+rgba.b*0.11),rgba.a);
}