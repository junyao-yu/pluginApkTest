package com.xinrenlei.test;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Auth：yujunyao
 * Since: 2020/12/21 10:55 AM
 * Email：yujunyao@xinrenlei.net
 */

public class MainActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.e("MainActivity", "我是插件apk显示的MainActivity");
    }
}
