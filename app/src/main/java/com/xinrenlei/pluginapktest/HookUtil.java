package com.xinrenlei.pluginapktest;

import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.util.Log;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.util.List;

/**
 * Auth：yujunyao
 * Since: 2020/12/21 5:20 PM
 * Email：yujunyao@xinrenlei.net
 *
 * 插件化最麻烦或者最大的问题就是Android系统的每一个大版本都会有源码改动，
 * 一旦源码改动，所有的hook点或者反射点都要相应的改动，否则兼容有问题，
 * 所以，维护起来成本很高。
 */

public class HookUtil {

    private static final String TARGET_INTENT = "target_intent";

    public static void hookAMS() {

        try {
            Field iActivityManagerSingletonField;
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                Class<?> clazz = Class.forName("android.app.ActivityManagerNative");
                iActivityManagerSingletonField = clazz.getDeclaredField("gDefault");
            } else {
                Class<?> clazz = Class.forName("android.app.ActivityTaskManager");
                iActivityManagerSingletonField = clazz.getDeclaredField("IActivityTaskManagerSingleton");
            }

            iActivityManagerSingletonField.setAccessible(true);
            Object singleton = iActivityManagerSingletonField.get(null);

            Class<?> singletonClazz = Class.forName("android.util.Singleton");
            Field mInstanceField = singletonClazz.getDeclaredField("mInstance");
            mInstanceField.setAccessible(true);
            Object mInstance = mInstanceField.get(singleton);

            Object proxyInstance = Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
                    new Class[]{Class.forName("android.app.IActivityTaskManager")}, (proxy, method, args) -> {

                Log.e("HookUtil--->", method.getName());

                if ("startActivity".equals(method.getName())) {
                    int index = -1;

                    for (int i = 0; i < args.length; i++) {
                        if (args[i] instanceof Intent) {
                            index = i;
                            break;
                        }
                    }

                    //启动插件的
                    Intent intent = (Intent) args[index];

                    Intent proxyIntent = new Intent();
                    proxyIntent.setClassName("com.xinrenlei.pluginapktest", "com.xinrenlei.pluginapktest.ProxyActivity");
                    proxyIntent.putExtra(TARGET_INTENT, intent);

                    args[index] = proxyIntent;

                }

                return method.invoke(mInstance, args);
            });

            mInstanceField.set(singleton, proxyInstance);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    public static void hookHandler() {
        try {
            Class<?> activityThreadClazz = Class.forName("android.app.ActivityThread");
            Field sCurrentActivityThreadField = activityThreadClazz.getDeclaredField("sCurrentActivityThread");
            sCurrentActivityThreadField.setAccessible(true);
            Object sCurrentActivityThread = sCurrentActivityThreadField.get(null);


            Field mHField = activityThreadClazz.getDeclaredField("mH");
            mHField.setAccessible(true);
            final Handler handler = (Handler) mHField.get(sCurrentActivityThread);

            Field mCallbackField = Handler.class.getDeclaredField("mCallback");
            mCallbackField.setAccessible(true);

            Handler.Callback callback = new Handler.Callback() {
                @Override
                public boolean handleMessage(@NonNull Message msg) {

                    switch (msg.what) {
                        case 100:
                            try {
                                Field intentField = msg.obj.getClass().getDeclaredField("intent");
                                intentField.setAccessible(true);
                                // 启动代理Intent
                                Intent proxyIntent = (Intent) intentField.get(msg.obj);
                                // 启动插件的 Intent
                                Intent intent = proxyIntent.getParcelableExtra(TARGET_INTENT);
                                if (intent != null) {
                                    intentField.set(msg.obj, intent);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            break;
                        case 159://API 28开始 启动Activity或者生命周期都是调用这个 在这之前是每个状态码对应一个code码
                            try {
                                Field mActivityCallbacksField = msg.obj.getClass().getDeclaredField("mActivityCallbacks");
                                mActivityCallbacksField.setAccessible(true);
                                List mActivityCallbacks = (List) mActivityCallbacksField.get(msg.obj);

                                for (int i = 0; i< mActivityCallbacks.size(); i++) {

                                    String packageName = mActivityCallbacks.get(i).getClass().getName();
                                    Log.e("HoolUtil--->", packageName);

                                    //LaunchActivityItem 是 ClientTransactionItem 的子类
                                    if (packageName.equals("android.app.servertransaction.LaunchActivityItem")) {
                                        Object launchActivityItem = mActivityCallbacks.get(i);

                                        Field intentField = launchActivityItem.getClass().getDeclaredField("mIntent");
                                        intentField.setAccessible(true);

                                        Intent proxyIntent = (Intent) intentField.get(launchActivityItem);

                                        Intent intent = proxyIntent.getParcelableExtra(TARGET_INTENT);
                                        if (intent != null) {
                                            intentField.set(launchActivityItem, intent);
                                        }
                                    }

                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            break;
                    }

                    return false;
                }
            };

            mCallbackField.set(handler, callback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
