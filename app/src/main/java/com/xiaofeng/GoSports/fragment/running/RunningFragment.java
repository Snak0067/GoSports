package com.xiaofeng.GoSports.fragment.running;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.xiaofeng.GoSports.core.BaseFragment;
import com.xiaofeng.GoSports.databinding.FragmentGridItemBinding;
import com.xiaofeng.GoSports.databinding.FragmentRunningBinding;
import com.xuexiang.xrouter.annotation.AutoWired;
import com.xuexiang.xrouter.launcher.XRouter;

public class RunningFragment extends BaseFragment<FragmentRunningBinding> {
    public static final String KEY_TITLE_NAME = "title_name";

    /**
     * 自动注入参数，不能是private
     */
    @AutoWired(name = KEY_TITLE_NAME)
    String title;

    @NonNull
    @Override
    protected FragmentRunningBinding viewBindingInflate(LayoutInflater inflater, ViewGroup container) {
        return FragmentRunningBinding.inflate(inflater, container, false);
    }

    @Override
    protected void initViews() {
        // 自动注入参数必须在initArgs里进行注入
        XRouter.getInstance().inject(this);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.basicmap_activity);//设置对应的XML布局文件

        MapView mapView = (MapView) findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);// 此方法必须重写
        AMap aMap = mapView.getMap();

    }
}
