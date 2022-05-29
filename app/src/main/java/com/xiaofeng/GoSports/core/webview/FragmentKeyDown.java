

package com.xiaofeng.GoSports.core.webview;

import android.view.KeyEvent;

/**
 *
 *
 * @author xiaofeng
 * @since 2022/1/4 下午11:32
 */
public interface FragmentKeyDown {

    /**
     * fragment按键监听
     * @param keyCode
     * @param event
     * @return
     */
    boolean onFragmentKeyDown(int keyCode, KeyEvent event);
}
