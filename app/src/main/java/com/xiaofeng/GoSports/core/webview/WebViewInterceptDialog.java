

package com.xiaofeng.GoSports.core.webview;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.xiaofeng.GoSports.R;
import com.xiaofeng.GoSports.utils.XToastUtils;
import com.xuexiang.xui.utils.ResUtils;
import com.xuexiang.xui.widget.dialog.DialogLoader;
import com.xuexiang.xutil.XUtil;
import com.xuexiang.xutil.app.ActivityUtils;

import java.net.URISyntaxException;

/**
 * WebView拦截提示
 *
 * @author xiaofeng
 * @since 2022-5-21 9:51
 */
public class WebViewInterceptDialog extends AppCompatActivity implements DialogInterface.OnDismissListener {

    private static final String KEY_INTERCEPT_URL = "key_intercept_url";

    // TODO: 2022-5-30 这里修改你的applink
    public static final String APP_LINK_HOST = "xuexiangjys.club";
    public static final String APP_LINK_ACTION = "com.xuexiang.xui.applink";


    /**
     * 显示WebView拦截提示
     *
     * @param url 需要拦截处理的url
     */
    public static void show(String url) {
        ActivityUtils.startActivity(WebViewInterceptDialog.class, KEY_INTERCEPT_URL, url);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String url = getIntent().getStringExtra(KEY_INTERCEPT_URL);

        DialogLoader.getInstance().showConfirmDialog(
                this,
                getOpenTitle(url),
                ResUtils.getString(R.string.lab_yes),
                (dialog, which) -> {
                    dialog.dismiss();
                    if (isAppLink(url)) {
                        openAppLink(this, url);
                    } else {
                        openApp(url);
                    }
                },
                ResUtils.getString(R.string.lab_no),
                (dialog, which) -> dialog.dismiss()
        ).setOnDismissListener(this);

    }

    private String getOpenTitle(String url) {
        String scheme = getScheme(url);
        if ("mqqopensdkapi".equals(scheme)) {
            return "是否允许页面打开\"QQ\"?";
        } else {
            return ResUtils.getString(R.string.lab_open_third_app);
        }
    }

    private String getScheme(String url) {
        try {
            Intent intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
            return intent.getScheme();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return "";
    }

    private boolean isAppLink(String url) {
        Uri uri = Uri.parse(url);
        return uri != null
                && APP_LINK_HOST.equals(uri.getHost())
                && (url.startsWith("http") || url.startsWith("https"));
    }


    private void openApp(String url) {
        Intent intent;
        try {
            intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            XUtil.getContext().startActivity(intent);
        } catch (Exception e) {
            XToastUtils.error("您所打开的第三方App未安装！");
        }
    }

    private void openAppLink(Context context, String url) {
        try {
            Intent intent = new Intent(APP_LINK_ACTION);
            intent.setData(Uri.parse(url));
            context.startActivity(intent);
        } catch (Exception e) {
            XToastUtils.error("您所打开的第三方App未安装！");
        }
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        finish();
    }
}
