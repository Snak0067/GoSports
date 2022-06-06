

package com.xiaofeng.GoSports.fragment.trending;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.vlayout.DelegateAdapter;
import com.alibaba.android.vlayout.VirtualLayoutManager;
import com.alibaba.android.vlayout.layout.LinearLayoutHelper;
import com.xiaofeng.GoSports.Model.RunningInfo;
import com.xiaofeng.GoSports.R;
import com.xiaofeng.GoSports.adapter.base.broccoli.BroccoliSimpleDelegateAdapter;
import com.xiaofeng.GoSports.adapter.base.delegate.SimpleDelegateAdapter;
import com.xiaofeng.GoSports.adapter.entity.NewInfo;
import com.xiaofeng.GoSports.core.BaseFragment;
import com.xiaofeng.GoSports.databinding.FragmentNewsBinding;
import com.xiaofeng.GoSports.databinding.FragmentTrendingBinding;
import com.xiaofeng.GoSports.utils.DemoDataProvider;
import com.xiaofeng.GoSports.utils.Utils;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xpage.enums.CoreAnim;
import com.xuexiang.xui.adapter.recyclerview.RecyclerViewHolder;
import com.xuexiang.xui.widget.actionbar.TitleBar;

import me.samlss.broccoli.Broccoli;

/**
 * @author xiaofeng
 * @since 2022-5-30 00:19
 */
@Page(anim = CoreAnim.none)
public class TrendingFragment extends BaseFragment<FragmentTrendingBinding> {
    private SimpleDelegateAdapter<RunningInfo> RunnningAdapter;

    @NonNull
    @Override
    protected FragmentTrendingBinding viewBindingInflate(LayoutInflater inflater, ViewGroup container) {
        return FragmentTrendingBinding.inflate(inflater, container, false);
    }

    /**
     * @return 返回为 null意为不需要导航栏
     */
    @Override
    protected TitleBar initTitle() {
        return null;
    }

    /**
     * 初始化控件
     */
    @Override
    protected void initViews() {
        VirtualLayoutManager virtualLayoutManager = new VirtualLayoutManager(getContext());
        binding.recyclerView.setLayoutManager(virtualLayoutManager);
        RecyclerView.RecycledViewPool viewPool = new RecyclerView.RecycledViewPool();
        binding.recyclerView.setRecycledViewPool(viewPool);
        viewPool.setMaxRecycledViews(0, 7);
        //资讯
        RunnningAdapter = new BroccoliSimpleDelegateAdapter<RunningInfo>(R.layout.adapter_history_record_card_view_list_item, new LinearLayoutHelper(), DemoDataProvider.getEmptyRunningInfo()) {
            @Override
            protected void onBindData(RecyclerViewHolder holder, RunningInfo model, int position) {
                if (model != null) {
                    holder.text(R.id.sports_style, model.getStyle());
                    holder.text(R.id.sports_time, model.getDate());
                    holder.text(R.id.running_total_distance, model.getDistance());
                    holder.text(R.id.running_total_time, model.getDuration());
                    holder.text(R.id.running_total_time, model.getSteps());
//                    holder.click(R.id.card_view, v -> Utils.goWeb(getContext(), model.getDetailUrl()));
                }
            }

            @Override
            protected void onBindBroccoli(RecyclerViewHolder holder, Broccoli broccoli) {
                broccoli.addPlaceholders(
                        holder.findView(R.id.sports_style),
                        holder.findView(R.id.sports_time),
                        holder.findView(R.id.running_total_distance),
                        holder.findView(R.id.running_total_time),
                        holder.findView(R.id.running_total_time)
                );
            }
        };

        DelegateAdapter delegateAdapter = new DelegateAdapter(virtualLayoutManager);
        delegateAdapter.addAdapter(RunnningAdapter);

        binding.recyclerView.setAdapter(delegateAdapter);

    }
}
