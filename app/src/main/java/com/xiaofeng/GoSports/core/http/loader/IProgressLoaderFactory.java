

package com.xiaofeng.GoSports.core.http.loader;

import android.content.Context;

import com.xuexiang.xhttp2.subsciber.impl.IProgressLoader;

/**
 * IProgressLoader的创建工厂实现接口
 *
 * @author xiaofeng
 * @since 2022-5-18 23:17
 */
public interface IProgressLoaderFactory {


    /**
     * 创建进度加载者
     *
     * @param context
     * @return
     */
    IProgressLoader create(Context context);


    /**
     * 创建进度加载者
     *
     * @param context
     * @param message 默认提示
     * @return
     */
    IProgressLoader create(Context context, String message);
}
