package com.xinrenlei.pluginapktest;

import android.app.Application;

/**
 * Auth：yujunyao
 * Since: 2020/12/21 11:02 AM
 * Email：yujunyao@xinrenlei.net
 */

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        LoadUtil.loadClass(this);
    }
}
