package com.xinrenlei.pluginapktest;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.lang.reflect.Array;
import java.lang.reflect.Field;

import dalvik.system.DexClassLoader;

/**
 * Auth：yujunyao
 * Since: 2020/12/21 2:25 PM
 * Email：yujunyao@xinrenlei.net
 */

public class LoadUtil {

    //BaseDexClassLoader 的路径
    private static final String BASE_DEX_CLASS_LOADER_PATH = "dalvik.system.BaseDexClassLoader";

    //DexPathList 的路径
    private static final String DEX_PATH_LIST_PATH = "dalvik.system.DexPathList";

    /**
     * 宿主dexElements = 宿主dexElements + 插件dexElements
     *
     * 1.获取宿主dexElements
     * 2.获取插件dexElements
     * 3.合并两个dexElements
     * 4.将新的dexElements 赋值到 宿主dexElements
     *
     * 目标：dexElements  -- DexPathList类的对象 -- BaseDexClassLoader的对象，类加载器
     *
     * 获取的是宿主的类加载器  --- 反射 dexElements  宿主
     *
     * 获取的是插件的类加载器  --- 反射 dexElements  插件
     */
    public static void loadClass(Context context) {

        try {
            Class<?> bootClazz = Class.forName(BASE_DEX_CLASS_LOADER_PATH);
            Field pathListField = bootClazz.getDeclaredField("pathList");
            pathListField.setAccessible(true);

            Class<?> dexPathClazz = Class.forName(DEX_PATH_LIST_PATH);
            Field dexElementsField = dexPathClazz.getDeclaredField("dexElements");
            dexElementsField.setAccessible(true);

            ClassLoader hostClassLoader = context.getClassLoader();

            Object hostPathList = pathListField.get(hostClassLoader);
            Object[] hostDexElements = (Object[]) dexElementsField.get(hostPathList);

            //插件apk存放路径
            String pluginApkPath = getPath(context) + "/test-debug.apk";
            ClassLoader pluginClassLoader = new DexClassLoader(pluginApkPath, context.getCacheDir().getAbsolutePath()
            , null, hostClassLoader);

            Object pluginPathList = pathListField.get(pluginClassLoader);
            Object[] pluginDexElements = (Object[]) dexElementsField.get(pluginPathList);


            //创建一个新的数组
            Object[] newDexElements = (Object[]) Array.newInstance(hostDexElements.getClass().getComponentType(),
                    hostDexElements.length + pluginDexElements.length);

            System.arraycopy(hostDexElements, 0, newDexElements, 0, hostDexElements.length);
            System.arraycopy(pluginDexElements, 0, newDexElements, hostDexElements.length, pluginDexElements.length);

            dexElementsField.set(hostPathList, newDexElements);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static String getPath(Context context) {
        File file = context.getExternalCacheDir();
        Log.e("LoadUtil--->", file.getAbsolutePath());
        return file.getAbsolutePath();
    }

}
