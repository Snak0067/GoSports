package com.xiaofeng.GoSports.activity;

import android.os.Bundle;
import android.view.KeyEvent;

import com.xiaofeng.GoSports.core.BaseActivity;
import com.xiaofeng.GoSports.fragment.other.LoginFragment;
import com.xuexiang.xui.utils.KeyboardUtils;
import com.xuexiang.xui.utils.StatusBarUtils;
import com.xuexiang.xutil.display.Colors;

/**
 * 登录页面
 *
 * @author xiaofeng
 * @since 2022-5-17 22:21
 */
public class LoginActivityByPhone extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        openPage(LoginFragment.class, getIntent().getExtras());
    }

    @Override
    protected boolean isSupportSlideBack() {
        return false;
    }

    @Override
    protected void initStatusBarStyle() {
        StatusBarUtils.initStatusBarStyle(this, false, Colors.WHITE);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return KeyboardUtils.onDisableBackKeyDown(keyCode) && super.onKeyDown(keyCode, event);
    }
}
