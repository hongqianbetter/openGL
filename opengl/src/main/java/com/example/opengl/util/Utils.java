package com.example.opengl.util;

import android.content.Context;
import android.content.ContextWrapper;
import android.os.Environment;
import android.util.Log;

import com.example.opengl.L;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author Lance
 * @date 2018/10/14
 */
public class Utils {

    public static void copyAssets2A(Context context, String path) {


        File dir = new File(context.getCacheDir()+"/A");
        if(!dir.exists()) {
            dir.mkdirs();
        }

        File file = new File(dir, path);
//        if (file.exists()) {
//            return;
//        }
//        boolean newFile=false;
//        try {
//           newFile = file.createNewFile();
//        } catch (IOException e) {
//            e.printStackTrace();
//            Log.e("XXX",e.getMessage()+"-------"+newFile);
//        }
        try {
            FileOutputStream fos = new FileOutputStream(file);
            InputStream is = context.getAssets().open(path);
            int len;
            byte[] b = new byte[2048];
            while ((len = is.read(b)) != -1) {
                fos.write(b, 0, len);
            }
            fos.close();
            is.close();
        } catch (Exception e) {

            Log.e("XXX",e.getMessage()+"-------");
            e.printStackTrace();
        }
        L.detail("copy A wancheng------"+file.getAbsolutePath());

    }

    public static void copyAssets2B(Context context, String path) {


        File dir = new File(context.getCacheDir()+"/B");
        if(!dir.exists()) {
            dir.mkdirs();
        }

        File file = new File(dir, path);

        try {
            FileOutputStream fos = new FileOutputStream(file);
            InputStream is = context.getAssets().open(path);
            int len;
            byte[] b = new byte[2048];
            while ((len = is.read(b)) != -1) {
                fos.write(b, 0, len);
            }
            fos.close();
            is.close();
        } catch (Exception e) {
            Log.e("XXX",e.getMessage()+"-------");
            e.printStackTrace();
        }
        L.detail("copy B wancheng------"+file.getAbsolutePath());
    }



}
