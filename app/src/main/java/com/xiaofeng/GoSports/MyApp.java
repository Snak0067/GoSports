

package com.xiaofeng.GoSports;

import android.app.Application;
import android.content.Context;

import androidx.multidex.MultiDex;

import com.xiaofeng.GoSports.utils.sdkinit.ANRWatchDogInit;
import com.xiaofeng.GoSports.utils.sdkinit.UMengInit;
import com.xiaofeng.GoSports.utils.sdkinit.XBasicLibInit;
import com.xiaofeng.GoSports.utils.sdkinit.XUpdateInit;

/**
 * @author xiaofeng
 * @since 2022/5/7 下午1:12
 */
public class MyApp extends Application {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        //解决4.x运行崩溃的问题
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initLibs();
    }

    /**
     * 初始化基础库
     */
    private void initLibs() {
        // X系列基础库初始化
        XBasicLibInit.init(this);
        // 版本更新初始化
        XUpdateInit.init(this);
        // 运营统计数据
        UMengInit.init(this);
        // ANR监控
        ANRWatchDogInit.init();
    }


    /**
     * @return 当前app是否是调试开发模式
     */
    public static boolean isDebug() {
        return BuildConfig.DEBUG;
    }


}
