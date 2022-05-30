

package com.xiaofeng.GoSports.adapter.base.broccoli;

import android.view.View;

import androidx.annotation.NonNull;

import com.alibaba.android.vlayout.LayoutHelper;
import com.xiaofeng.GoSports.adapter.base.delegate.SimpleDelegateAdapter;
import com.xiaofeng.GoSports.adapter.base.delegate.XDelegateAdapter;
import com.xuexiang.xui.adapter.recyclerview.RecyclerViewHolder;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import me.samlss.broccoli.Broccoli;

/**
 * 使用Broccoli占位的基础适配器
 *
 * @author xiaofeng
 * @since 2021/1/9 4:52 PM
 */
public abstract class BroccoliSimpleDelegateAdapter<T> extends SimpleDelegateAdapter<T> {

    /**
     * 是否已经加载成功
     */
    private boolean mHasLoad = false;
    private Map<View, Broccoli> mBroccoliMap = new HashMap<>();

    public BroccoliSimpleDelegateAdapter(int layoutId, LayoutHelper layoutHelper) {
        super(layoutId, layoutHelper);
    }

    public BroccoliSimpleDelegateAdapter(int layoutId, LayoutHelper layoutHelper, Collection<T> list) {
        super(layoutId, layoutHelper, list);
    }

    public BroccoliSimpleDelegateAdapter(int layoutId, LayoutHelper layoutHelper, T[] data) {
        super(layoutId, layoutHelper, data);
    }

    @Override
    protected void bindData(@NonNull RecyclerViewHolder holder, int position, T item) {
        Broccoli broccoli = mBroccoliMap.get(holder.itemView);
        if (broccoli == null) {
            broccoli = new Broccoli();
            mBroccoliMap.put(holder.itemView, broccoli);
        }
        if (mHasLoad) {
            broccoli.removeAllPlaceholders();

            onBindData(holder, item, position);
        } else {
            onBindBroccoli(holder, broccoli);
            broccoli.show();
        }
    }


    /**
     * 绑定控件
     *
     * @param holder
     * @param model
     * @param position
     */
    protected abstract void onBindData(RecyclerViewHolder holder, T model, int position);

    /**
     * 绑定占位控件
     *
     * @param holder
     * @param broccoli
     */
    protected abstract void onBindBroccoli(RecyclerViewHolder holder, Broccoli broccoli);

    @Override
    public XDelegateAdapter refresh(Collection<T> collection) {
        mHasLoad = true;
        return super.refresh(collection);
    }

    /**
     * 资源释放，防止内存泄漏
     */
    public void recycle() {
        for (Broccoli broccoli : mBroccoliMap.values()) {
            broccoli.removeAllPlaceholders();
        }
        mBroccoliMap.clear();
        clear();
    }
}
