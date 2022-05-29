

package com.xiaofeng.GoSports.core;

import android.content.res.Configuration;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import androidx.annotation.NonNull;

import com.umeng.analytics.MobclickAgent;
import com.xuexiang.xaop.annotation.SingleClick;
import com.xuexiang.xpage.base.XPageContainerListFragment;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.xuexiang.xui.widget.actionbar.TitleUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 修改列表样式为主副标题显示
 *
 * @author xiaofeng
 * @since 2022/5/22 上午11:26
 */
public abstract class BaseContainerFragment extends XPageContainerListFragment {

    @Override
    protected void initPage() {
        initTitle();
        initViews();
        initListeners();
    }

    protected TitleBar initTitle() {
        return TitleUtils.addTitleBarDynamic((ViewGroup) getRootView(), getPageTitle(), v -> popToBack());
    }

    @Override
    protected void initData() {
        mSimpleData = initSimpleData(mSimpleData);

        List<Map<String, String>> data = new ArrayList<>();
        for (String content : mSimpleData) {
            Map<String, String> item = new HashMap<>();
            int index = content.indexOf("\n");
            if (index > 0) {
                item.put(SimpleListAdapter.KEY_TITLE, String.valueOf(content.subSequence(0, index)));
                item.put(SimpleListAdapter.KEY_SUB_TITLE, String.valueOf(content.subSequence(index + 1, content.length())));
            } else {
                item.put(SimpleListAdapter.KEY_TITLE, content);
                item.put(SimpleListAdapter.KEY_SUB_TITLE, "");
            }
            data.add(item);
        }

        getListView().setAdapter(new SimpleListAdapter(getContext(), data));
        initSimply();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        onItemClick(view, position);
    }

    @SingleClick
    private void onItemClick(View view, int position) {
        onItemClick(position);
    }

    @Override
    public void onDestroyView() {
        getListView().setOnItemClickListener(null);
        super.onDestroyView();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        //屏幕旋转时刷新一下title
        super.onConfigurationChanged(newConfig);
        ViewGroup root = (ViewGroup) getRootView();
        if (root.getChildAt(0) instanceof TitleBar) {
            root.removeViewAt(0);
            initTitle();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(getPageName());
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(getPageName());
    }
}
