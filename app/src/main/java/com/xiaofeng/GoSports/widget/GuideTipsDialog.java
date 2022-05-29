package com.xiaofeng.GoSports.widget;

import android.content.Context;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatCheckBox;

import com.xiaofeng.GoSports.core.http.api.ApiService;
import com.xiaofeng.GoSports.core.http.callback.NoTipCallBack;
import com.xiaofeng.GoSports.core.http.entity.TipInfo;
import com.xuexiang.constant.TimeConstants;
import com.xiaofeng.GoSports.R;
import com.xiaofeng.GoSports.utils.MMKVUtils;
import com.xuexiang.xaop.annotation.SingleClick;
import com.xuexiang.xhttp2.XHttp;
import com.xuexiang.xhttp2.cache.model.CacheMode;
import com.xuexiang.xhttp2.request.CustomRequest;
import com.xuexiang.xui.widget.dialog.BaseDialog;
import com.xuexiang.xutil.app.AppUtils;
import com.zzhoujay.richtext.RichText;

import java.util.ArrayList;
import java.util.List;

/**
 * 小贴士弹窗
 *
 * @author xiaofeng
 * @since 2022-08-22 17:02
 */
public class GuideTipsDialog extends BaseDialog implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private static final String KEY_IS_IGNORE_TIPS = "com.xiaofeng.GoSports.widget.key_is_ignore_tips_";

    private List<TipInfo> mTips;
    private int mIndex = -1;

    private TextView mTvPrevious;
    private TextView mTvNext;

    private TextView mTvTitle;
    private TextView mTvContent;

    /**
     * 显示提示
     *
     * @param context 上下文
     */
    public static void showTips(final Context context) {
        if (!isIgnoreTips()) {
            showTipsForce(context);
        }
    }

    /**
     * 强制显示提示
     *
     * @param context 上下文
     */
    public static void showTipsForce(Context context) {
        List<TipInfo> tipInfoList = new ArrayList<>();
        tipInfoList.add(new TipInfo("马晓峰CSDN主页", "<a href=\"https://blog.csdn.net/kuLong_x?type=blog\">少年的成长，码出未来</a>"));
        tipInfoList.add(new TipInfo("马晓峰Gitee主页", "点击关注作者，了解最新动态！<br /><a href=\"https://gitee.com/XiaoFengCodeSpace\"><font color=\"#800080\">Xiaofeng_Gitee</font></a>"));
        new GuideTipsDialog(context, tipInfoList).show();
    }

    /**
     * 获取强制弹窗的上下文以及内容列表信息
     *
     * @param context
     * @param tips
     */
    public GuideTipsDialog(Context context, @NonNull List<TipInfo> tips) {
        //集成BaseDialog 基础的弹窗列表
        super(context, R.layout.dialog_guide_tips);
        //初始话弹窗列表信息
        initViews();
        //更新显示的列表信息
        updateTips(tips);
    }

    /**
     * 初始化弹窗
     */
    private void initViews() {
        //弹窗主标题
        mTvTitle = findViewById(R.id.tv_title);
        //弹窗内容
        mTvContent = findViewById(R.id.tv_content);
        //是否确认忽视不再提示弹窗
        AppCompatCheckBox cbIgnore = findViewById(R.id.cb_ignore);
        //右上角的关闭
        ImageView ivClose = findViewById(R.id.iv_close);
        //上一条
        mTvPrevious = findViewById(R.id.tv_previous);
        //下一条
        mTvNext = findViewById(R.id.tv_next);

        if (cbIgnore != null) {
            cbIgnore.setChecked(isIgnoreTips());
            cbIgnore.setOnCheckedChangeListener(this);
        }
        if (ivClose != null) {
            ivClose.setOnClickListener(this);
        }
        mTvPrevious.setOnClickListener(this);
        mTvNext.setOnClickListener(this);
        mTvPrevious.setEnabled(false);
        mTvNext.setEnabled(true);
        setCancelable(false);
        setCanceledOnTouchOutside(true);
    }

    /**
     * 更新提示信息
     *
     * @param tips 提示信息
     */
    private void updateTips(List<TipInfo> tips) {
        mTips = tips;
        if (mTips != null && mTips.size() > 0 && mTvContent != null) {
            mIndex = 0;
            showRichText(mTips.get(mIndex));
        }
    }

    /**
     * 切换提示信息
     *
     * @param index 索引
     */
    private void switchTipInfo(int index) {
        if (mTips != null && mTips.size() > 0 && mTvContent != null) {
            if (index >= 0 && index <= mTips.size() - 1) {
                showRichText(mTips.get(index));
                if (index == 0) {
                    mTvPrevious.setEnabled(false);
                    mTvNext.setEnabled(true);
                } else if (index == mTips.size() - 1) {
                    mTvPrevious.setEnabled(true);
                    mTvNext.setEnabled(false);
                } else {
                    mTvPrevious.setEnabled(true);
                    mTvNext.setEnabled(true);
                }
            }
        }
    }

    /**
     * 显示富文本
     *
     * @param tipInfo 提示信息
     */
    private void showRichText(TipInfo tipInfo) {
        mTvTitle.setText(tipInfo.getTitle());
        RichText.fromHtml(tipInfo.getContent())
                .bind(this)
                .into(mTvContent);
    }


    @SingleClick(300)
    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.iv_close) {
            dismiss();
        } else if (id == R.id.tv_previous) {
            if (mIndex > 0) {
                mIndex--;
                switchTipInfo(mIndex);
            }
        } else if (id == R.id.tv_next) {
            if (mIndex < mTips.size() - 1) {
                mIndex++;
                switchTipInfo(mIndex);
            }
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        setIsIgnoreTips(isChecked);
    }

    @Override
    public void onDetachedFromWindow() {
        RichText.clear(this);
        super.onDetachedFromWindow();
    }


    public static boolean setIsIgnoreTips(boolean isIgnore) {
        return MMKVUtils.put(KEY_IS_IGNORE_TIPS + AppUtils.getAppVersionCode(), isIgnore);
    }

    public static boolean isIgnoreTips() {
        return MMKVUtils.getBoolean(KEY_IS_IGNORE_TIPS + AppUtils.getAppVersionCode(), false);
    }

}
