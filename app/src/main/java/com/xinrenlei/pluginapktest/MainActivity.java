package com.xinrenlei.pluginapktest;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import java.lang.reflect.Method;

/**
 * Auth：yujunyao
 * Since: 2020/12/21 11:00 AM
 * Email：yujunyao@xinrenlei.net
 */

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        printClassLoader();

    }

    /**
     * E/MainActivity--->: classLoader:dalvik.system.PathClassLoader[DexPathList[[zip file "/data/app/com.xinrenlei.pluginapktest-JQefpO9xUi04DSYd7VwsiQ==/base.apk"],nativeLibraryDirectories=[/data/app/com.xinrenlei.pluginapktest-JQefpO9xUi04DSYd7VwsiQ==/lib/arm64, /system/lib64, /system/product/lib64]]]
     * E/MainActivity--->: classLoader:java.lang.BootClassLoader@7c9f95
     * E/Activity--->: classLoader:java.lang.BootClassLoader@7c9f95
     * E/AppCompatActivity--->: classLoader:dalvik.system.PathClassLoader[DexPathList[[zip file "/data/app/com.xinrenlei.pluginapktest-JQefpO9xUi04DSYd7VwsiQ==/base.apk"],nativeLibraryDirectories=[/data/app/com.xinrenlei.pluginapktest-JQefpO9xUi04DSYd7VwsiQ==/lib/arm64, /system/lib64, /system/product/lib64]]]
     */
    private void printClassLoader() {
        ClassLoader classLoader = getClassLoader();
        while (classLoader != null) {
            Log.e("MainActivity--->", "classLoader:" + classLoader);
            classLoader = classLoader.getParent();
        }
        Log.e("Activity--->", "classLoader:" + Activity.class.getClassLoader());
        Log.e("AppCompatActivity--->", "classLoader:" + AppCompatActivity.class.getClassLoader());
    }

    public void button(View view) {
        //调用插件apk的简单类
        try {
            Class<?> clazz = Class.forName("com.xinrenlei.test.Test");
            Method print = clazz.getDeclaredMethod("print");
            print.invoke(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void button2(View view) {
        HookUtil.hookAMS();
        HookUtil.hookHandler();
        Intent intent = new Intent();
        intent.setComponent(new ComponentName("com.xinrenlei.test", "com.xinrenlei.test.MainActivity"));
        startActivity(intent);


    }
}
