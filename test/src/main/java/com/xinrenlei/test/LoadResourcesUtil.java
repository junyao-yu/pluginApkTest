package com.xinrenlei.test;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.util.Log;

import java.io.File;
import java.lang.reflect.Method;

/**
 * Auth：yujunyao
 * Since: 2020/12/23 4:59 PM
 * Email：yujunyao@xinrenlei.net
 */

public class LoadResourcesUtil {

    private static Resources mResources;

    public static Resources getResources(Context mContext) {
        if (mResources == null) {
            mResources = loadResources(mContext);
        }

        return mResources;
    }

    private static Resources loadResources(Context mContext) {

        try {
            AssetManager assetManager = AssetManager.class.newInstance();
            Method addAssetPathMethod = AssetManager.class.getMethod("addAssetPath", String.class);
            addAssetPathMethod.invoke(assetManager, getPath(mContext) + "/test-debug.apk");

            Resources resources = mContext.getResources();

            return new Resources(assetManager, resources.getDisplayMetrics(), resources.getConfiguration());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }


    private static String getPath(Context context) {
        File file = context.getExternalCacheDir();
        Log.e("LoadUtil--->", file.getAbsolutePath());
        return file.getAbsolutePath();
    }

}
