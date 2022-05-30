package com.xiaofeng.GoSports.fragment.news;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.xiaofeng.GoSports.core.BaseFragment;
import com.xiaofeng.GoSports.databinding.FragmentGridItemBinding;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xrouter.annotation.AutoWired;
import com.xuexiang.xrouter.launcher.XRouter;

/**
 * @author xiaofeng
 * @since 2022/6/01 1:21 AM
 */
@Page
public class GridItemFragment extends BaseFragment<FragmentGridItemBinding> {

    public static final String KEY_TITLE_NAME = "title_name";

    /**
     * 自动注入参数，不能是private
     */
    @AutoWired(name = KEY_TITLE_NAME)
    String title;

    @NonNull
    @Override
    protected FragmentGridItemBinding viewBindingInflate(LayoutInflater inflater, ViewGroup container) {
        return FragmentGridItemBinding.inflate(inflater, container, false);
    }

    @Override
    protected void initArgs() {
        // 自动注入参数必须在initArgs里进行注入
        XRouter.getInstance().inject(this);
    }

    @Override
    protected String getPageTitle() {
        return title;
    }


    @Override
    protected void initViews() {

    }


}
