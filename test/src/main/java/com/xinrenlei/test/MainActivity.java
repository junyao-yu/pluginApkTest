package com.xinrenlei.test;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;

import java.lang.reflect.Field;

/**
 * Auth：yujunyao
 * Since: 2020/12/21 10:55 AM
 * Email：yujunyao@xinrenlei.net
 */

public class MainActivity extends AppCompatActivity {

    private Context mContext;

//    @Override
//    public Resources getResources() {
//        Resources resources = LoadResourcesUtil.getResources(getApplication());
//        return resources == null ? super.getResources() : resources;
//    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Resources resources = LoadResourcesUtil.getResources(getApplication());
        mContext = new ContextThemeWrapper(getBaseContext(), 0);

        Class<? extends Context> clazz = mContext.getClass();

        try {
            Field mResourcesField = clazz.getDeclaredField("mResources");
            mResourcesField.setAccessible(true);
            mResourcesField.set(mContext, resources);
        } catch (Exception e) {
            e.printStackTrace();
        }

//        setContentView(R.layout.activity_main);

        View view = LayoutInflater.from(mContext).inflate(R.layout.activity_main, null);
        setContentView(view);
        Log.e("MainActivity", "我是插件apk显示的MainActivity");
    }
}
