

package com.xiaofeng.GoSports.core.http.loader;

import android.content.Context;

import com.xuexiang.xhttp2.subsciber.impl.IProgressLoader;

/**
 * 迷你加载框创建工厂
 *
 * @author xiaofeng
 * @since 2022-5-18 23:23
 */
public class MiniProgressLoaderFactory implements IProgressLoaderFactory {

    @Override
    public IProgressLoader create(Context context) {
        return new MiniLoadingDialogLoader(context);
    }

    @Override
    public IProgressLoader create(Context context, String message) {
        return new MiniLoadingDialogLoader(context, message);
    }
}
