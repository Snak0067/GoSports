

package com.xiaofeng.GoSports.core;

import android.os.Bundle;

import com.xiaofeng.GoSports.utils.XToastUtils;
import com.xuexiang.xrouter.annotation.AutoWired;
import com.xuexiang.xrouter.annotation.Router;
import com.xuexiang.xrouter.launcher.XRouter;
import com.xuexiang.xutil.common.StringUtils;

/**
 * https://xuexiangjys.club/xpage/transfer?pageName=xxxxx&....
 * applink的中转
 *
 * @author xiaofeng
 * @since 2022-05-06 9:37
 */
@Router(path = "/xpage/transfer")
public class XPageTransferActivity extends BaseActivity {

    @AutoWired(name = "pageName")
    String pageName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        XRouter.getInstance().inject(this);

        if (!StringUtils.isEmpty(pageName)) {
            if (openPage(pageName, getIntent().getExtras()) == null) {
                XToastUtils.error("页面未找到！");
                finish();
            }
        } else {
            XToastUtils.error("页面未找到！");
            finish();
        }
    }
}
