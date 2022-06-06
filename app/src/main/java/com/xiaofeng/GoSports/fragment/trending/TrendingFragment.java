package com.xiaofeng.GoSports.fragment.trending;

import static com.xiaofeng.GoSports.utils.TimerUtils.timeConversion;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.vlayout.DelegateAdapter;
import com.alibaba.android.vlayout.VirtualLayoutManager;
import com.alibaba.android.vlayout.layout.LinearLayoutHelper;
import com.xiaofeng.GoSports.R;
import com.xiaofeng.GoSports.adapter.base.broccoli.BroccoliSimpleDelegateAdapter;
import com.xiaofeng.GoSports.adapter.base.delegate.SimpleDelegateAdapter;
import com.xiaofeng.GoSports.core.BaseFragment;
import com.xiaofeng.GoSports.databinding.FragmentTrendingBinding;
import com.xiaofeng.GoSports.fragment.Walking.RecordActivity;
import com.xiaofeng.GoSports.fragment.Walking.RecordShowActivity;
import com.xiaofeng.GoSports.utils.DemoDataProvider;
import com.xiaofeng.GoSports.utils.path.DbAdapter;
import com.xiaofeng.GoSports.utils.path.PathRecord;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xpage.enums.CoreAnim;
import com.xuexiang.xui.adapter.recyclerview.RecyclerViewHolder;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.xuexiang.xui.widget.layout.XUIFrameLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import me.samlss.broccoli.Broccoli;

/**
 * @author xiaofeng
 * @since 2022-6-30 00:19
 */
@Page(anim = CoreAnim.none)
public class TrendingFragment extends BaseFragment<FragmentTrendingBinding> {
    private int requestNum = 0;
    private SimpleDelegateAdapter<PathRecord> runningAdapter;
    private static DbAdapter mDataBaseHelper;
    private List<PathRecord> mAllRunningRecord = new ArrayList<PathRecord>();
    public static final String RECORD_ID = "record_id";

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
        mDataBaseHelper = new DbAdapter(getContext());
        mDataBaseHelper.open();
        searchAllRecordFromDB();
        VirtualLayoutManager virtualLayoutManager = new VirtualLayoutManager(getContext());
        binding.recyclerView.setLayoutManager(virtualLayoutManager);
        RecyclerView.RecycledViewPool viewPool = new RecyclerView.RecycledViewPool();
        binding.recyclerView.setRecycledViewPool(viewPool);
        viewPool.setMaxRecycledViews(0, 7);
        //资讯
        runningAdapter = new BroccoliSimpleDelegateAdapter<PathRecord>(R.layout.adapter_history_record_card_view_list_item, new LinearLayoutHelper(), DemoDataProvider.getEmptyPathRecord()) {
            @Override
            protected void onBindData(RecyclerViewHolder holder, PathRecord model, int position) {
                if (model != null) {
                    holder.text(R.id.sports_time, model.getDate());
                    holder.text(R.id.running_total_distance, model.getDistance());
                    holder.text(R.id.running_total_time, model.getDuration());
                    holder.text(R.id.running_mAveragespeed, model.getAveragespeed());
                    holder.click(R.id.history_record_card_view, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getActivity(), RecordShowActivity.class);
                            intent.putExtra(RECORD_ID, model.getId());
                            startActivity(intent);
                        }
                    });
                }
            }

            @Override
            protected void onBindBroccoli(RecyclerViewHolder holder, Broccoli broccoli) {
                broccoli.addPlaceholders(
                        holder.findView(R.id.sports_time),
                        holder.findView(R.id.running_total_distance),
                        holder.findView(R.id.running_total_time),
                        holder.findView(R.id.running_mAveragespeed)
                );
            }
        };

        DelegateAdapter delegateAdapter = new DelegateAdapter(virtualLayoutManager);
        delegateAdapter.addAdapter(runningAdapter);

        binding.recyclerView.setAdapter(delegateAdapter);

    }

    @Override
    protected void initListeners() {
        //下拉刷新
        binding.refreshLayout.setOnRefreshListener(refreshLayout -> {
            refreshLayout.getLayout().postDelayed(() -> {
                requestNum = 0;
                runningAdapter.refresh(getPartRecord(requestNum));
                refreshLayout.finishRefresh();
            }, 1000);
        });
        //上拉加载
        binding.refreshLayout.setOnLoadMoreListener(refreshLayout -> {
            refreshLayout.getLayout().postDelayed(() -> {
                requestNum++;
                runningAdapter.loadMore(getPartRecord(requestNum));
                refreshLayout.finishLoadMore();
            }, 1000);
        });
        binding.refreshLayout.autoRefresh();//第一次进入触发自动刷新，演示效果
    }

    /**
     * 在数据库中遍历所有的跑步的信息
     */
    private void searchAllRecordFromDB() {
        mAllRunningRecord = mDataBaseHelper.queryRecordAll();
        for (int i = 0; i < mAllRunningRecord.size(); i++) {
            PathRecord path = mAllRunningRecord.get(i);
            path.setAveragespeed(getAverageSpeed(Double.parseDouble(path.getDistance()), Double.parseDouble(path.getDuration())));
            path.setDistance(convertDistance(path.getDistance()));
            path.setDuration(convertTime(path.getDuration()));
            mAllRunningRecord.set(i, path);
        }
    }

    /**
     * 获取跑步距离及总时间进行平均配速的计算
     *
     * @param distance
     * @param time
     * @return
     */
    private String getAverageSpeed(double distance, double time) {
        //算出每秒配速
        double average = distance / time;
        if (average == 0) {
            return "00'00''公里";
        } else {
            //算出该配速下跑一公里需要多久
            double minkmTime = 1000.0 / average;
            int minute = (int) (minkmTime / 60);
            int second = (int) (minkmTime % 60);
            String strMin = minute > 9 ? String.valueOf(minute) : "0" + minute;
            String strSec = second > 9 ? String.valueOf(second) : "0" + second;
            return (strMin + "’" + strSec + "''/公里");
        }
    }

    /**
     * 将最原始的以米为计数单位的String的距离字段转换为以公里为单位的String类型的距离字段
     *
     * @param distanceStr
     * @return
     */
    private String convertDistance(String distanceStr) {
        java.text.DecimalFormat df = new java.text.DecimalFormat("#00.00");
        double distance = Double.parseDouble(distanceStr);
        return df.format(distance / 1000) + " 公里";
    }

    /**
     * 将总时间转换为标准化时间输出
     *
     * @param timeStr
     * @return
     */
    private String convertTime(String timeStr) {
        int time = (int) Double.parseDouble(timeStr);
        return timeConversion(time);
    }

    /**
     * 进行分页加载,6个为一页
     *
     * @param index
     * @return
     */
    private List<PathRecord> getPartRecord(int index) {
        List<PathRecord> localRecord = new ArrayList<>();
        if (index * 6 > mAllRunningRecord.size()) {
            return null;
        } else {
            int startIndex = index * 6;
            int endIndex = Math.min((index + 1) * 6, mAllRunningRecord.size());
            for (int i = startIndex; i < endIndex; i++) {
                localRecord.add(mAllRunningRecord.get(i));
            }
            return localRecord;
        }
    }
}
