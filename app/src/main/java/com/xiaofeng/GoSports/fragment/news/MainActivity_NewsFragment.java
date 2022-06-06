

package com.xiaofeng.GoSports.fragment.news;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.vlayout.DelegateAdapter;
import com.alibaba.android.vlayout.VirtualLayoutManager;
import com.alibaba.android.vlayout.layout.GridLayoutHelper;
import com.alibaba.android.vlayout.layout.LinearLayoutHelper;
import com.xiaofeng.GoSports.core.BaseFragment;
import com.xiaofeng.GoSports.fragment.Walking.WalkingActivity;
import com.xiaofeng.GoSports.fragment.history.HistoryActivity;
import com.xiaofeng.GoSports.fragment.pushUp.PushUpActivity;
import com.xiaofeng.GoSports.fragment.running.RunningActivity;
import com.xiaofeng.GoSports.fragment.skipping.SkippingActivity;
import com.xiaofeng.GoSports.utils.Utils;
import com.xiaofeng.GoSports.R;
import com.xiaofeng.GoSports.adapter.base.broccoli.BroccoliSimpleDelegateAdapter;
import com.xiaofeng.GoSports.adapter.base.delegate.SimpleDelegateAdapter;
import com.xiaofeng.GoSports.adapter.base.delegate.SingleDelegateAdapter;
import com.xiaofeng.GoSports.adapter.entity.NewInfo;
import com.xiaofeng.GoSports.databinding.FragmentNewsBinding;
import com.xiaofeng.GoSports.utils.DemoDataProvider;
import com.xiaofeng.GoSports.utils.XToastUtils;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xpage.enums.CoreAnim;
import com.xuexiang.xui.adapter.recyclerview.RecyclerViewHolder;
import com.xuexiang.xui.adapter.simple.AdapterItem;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.xuexiang.xui.widget.banner.widget.banner.SimpleImageBanner;
import com.xuexiang.xui.widget.imageview.ImageLoader;
import com.xuexiang.xui.widget.imageview.RadiusImageView;
import com.xuexiang.xutil.app.ActivityUtils;

import me.samlss.broccoli.Broccoli;

/**
 * 首页动态
 *
 * @author xiaofeng
 * @since 2022-5-30 00:15
 */
@Page(anim = CoreAnim.none)
public class MainActivity_NewsFragment extends BaseFragment<FragmentNewsBinding> {

    private SimpleDelegateAdapter<NewInfo> mNewsAdapter;

    @NonNull
    @Override
    protected FragmentNewsBinding viewBindingInflate(LayoutInflater inflater, ViewGroup container) {
        return FragmentNewsBinding.inflate(inflater, container, false);
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

        //轮播条
        SingleDelegateAdapter bannerAdapter = new SingleDelegateAdapter(R.layout.include_head_view_banner) {
            @Override
            public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position) {
                SimpleImageBanner banner = holder.findViewById(R.id.sib_simple_usage);
                banner.setSource(DemoDataProvider.getBannerList()).setOnItemClickListener((view, item, newPosition) -> XToastUtils.toast(item.title)).startScroll();
            }
        };

        //九宫格菜单
        GridLayoutHelper gridLayoutHelper = new GridLayoutHelper(4);
        gridLayoutHelper.setPadding(0, 16, 0, 0);
        gridLayoutHelper.setVGap(10);
        gridLayoutHelper.setHGap(0);
        SimpleDelegateAdapter<AdapterItem> commonAdapter = new SimpleDelegateAdapter<AdapterItem>(R.layout.adapter_common_grid_item, gridLayoutHelper, DemoDataProvider.getGridItems(getContext())) {
            @Override
            protected void bindData(@NonNull RecyclerViewHolder holder, int position, AdapterItem item) {
                if (item != null) {
                    RadiusImageView imageView = holder.findViewById(R.id.riv_item);
                    imageView.setCircle(true);
                    ImageLoader.get().loadImage(imageView, item.getIcon());
                    holder.text(R.id.tv_title, item.getTitle().toString().substring(0, 1));
                    holder.text(R.id.tv_sub_title, item.getTitle());

                    holder.click(R.id.ll_container, v -> {
                        XToastUtils.toast("点击了：" + item.getTitle());
                        // 注意: 这里由于NewsFragment是使用Viewpager加载的，并非使用XPage加载的，因此没有承载Activity， 需要使用openNewPage。
                        switch (item.getTitle().toString()) {
                            case "跳绳":
                                ActivityUtils.startActivity(SkippingActivity.class);
                                break;
                            case "俯卧撑":
                                ActivityUtils.startActivity(PushUpActivity.class);
                                break;
                            case "走路":
                                ActivityUtils.startActivity(WalkingActivity.class);
                                break;
                            case "跑步":
                                ActivityUtils.startActivity(RunningActivity.class);
                                break;
                            case "历史记录":
                                ActivityUtils.startActivity(HistoryActivity.class);
                                break;
                            case "饮食安排":
                                Utils.goWeb(getContext(), "https://zhuanlan.zhihu.com/p/24889097#:~:text=1.%E8%86%B3%E9%A3%9F%E7%9A%84%E5%AE%89%E6%8E%92%20%E5%88%9D%E5%AD%A6%E8%80%85%E9%87%87%E7%94%A8%E2%80%9C%E6%97%A5%E9%A3%9F5%E9%A4%90%E6%B3%95%E2%80%9D%E8%BE%83%E4%B8%BA%E5%90%88%E9%80%82%EF%BC%9A%E5%8D%B3%E6%AF%8F%E6%97%A5%E5%90%835%E6%AC%A1%E3%80%82,5%E9%A4%90%E7%9A%84%E6%AF%94%E4%BE%8B%E4%B8%BA%E6%97%A9%E9%A4%90%E5%8D%A0%E5%85%A8%E5%A4%A9%E6%80%BB%E9%87%8F%E7%9A%8420%25%EF%BC%8C%E4%B8%8A%E5%8D%88%E5%8A%A0%E9%A4%90%E5%8D%A010%25%EF%BC%8C%E5%8D%88%E9%A4%90%E5%8D%A030%25%EF%BC%8C%E4%B8%8B%E5%8D%88%E5%8A%A0%E9%A4%90%E5%8D%A010%25%EF%BC%8C%E6%99%9A%E9%A4%90%E5%8D%A030%25%E3%80%82%202.%E8%86%B3%E9%A3%9F%E7%9A%84%E7%BB%84%E6%88%90");
                                break;
                            case "心理测评":
                                Utils.goWeb(getContext(), "https://xin.httpcn.com/fenlei/mianfei/");
                                break;
                            case "助眠音乐":
                                Utils.goWeb(getContext(), "https://music.163.com/#/djradio?id=966559685");
                                break;
                            default:
                                openNewPage(GridItemFragment.class, GridItemFragment.KEY_TITLE_NAME, item.getTitle());
                        }

                    });
                }
            }
        };

        //资讯的标题
        SingleDelegateAdapter titleAdapter = new SingleDelegateAdapter(R.layout.adapter_title_item) {
            @Override
            public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position) {
                holder.text(R.id.tv_title, "资讯");
                holder.text(R.id.tv_action, "更多");
                holder.click(R.id.tv_action, v -> XToastUtils.toast("更多"));
            }
        };

        //资讯
        mNewsAdapter = new BroccoliSimpleDelegateAdapter<NewInfo>(R.layout.adapter_news_card_view_list_item, new LinearLayoutHelper(), DemoDataProvider.getEmptyNewInfo()) {
            @Override
            protected void onBindData(RecyclerViewHolder holder, NewInfo model, int position) {
                if (model != null) {
                    holder.text(R.id.tv_user_name, model.getUserName());
                    holder.text(R.id.tv_tag, model.getTag());
                    holder.text(R.id.tv_title, model.getTitle());
                    holder.text(R.id.tv_summary, model.getSummary());
                    holder.text(R.id.tv_praise, model.getPraise() == 0 ? "点赞" : String.valueOf(model.getPraise()));
                    holder.text(R.id.tv_comment, model.getComment() == 0 ? "评论" : String.valueOf(model.getComment()));
                    holder.text(R.id.tv_read, "阅读量 " + model.getRead());
                    holder.image(R.id.iv_image, model.getImageUrl());

                    holder.click(R.id.card_view, v -> Utils.goWeb(getContext(), model.getDetailUrl()));
                }
            }

            @Override
            protected void onBindBroccoli(RecyclerViewHolder holder, Broccoli broccoli) {
                broccoli.addPlaceholders(
                        holder.findView(R.id.tv_user_name),
                        holder.findView(R.id.tv_tag),
                        holder.findView(R.id.tv_title),
                        holder.findView(R.id.tv_summary),
                        holder.findView(R.id.tv_praise),
                        holder.findView(R.id.tv_comment),
                        holder.findView(R.id.tv_read),
                        holder.findView(R.id.iv_image)
                );
            }
        };

        DelegateAdapter delegateAdapter = new DelegateAdapter(virtualLayoutManager);
        delegateAdapter.addAdapter(bannerAdapter);
        delegateAdapter.addAdapter(commonAdapter);
        delegateAdapter.addAdapter(titleAdapter);
        delegateAdapter.addAdapter(mNewsAdapter);

        binding.recyclerView.setAdapter(delegateAdapter);
    }

    @Override
    protected void initListeners() {
        //下拉刷新
        binding.refreshLayout.setOnRefreshListener(refreshLayout -> {
            // TODO: 2020-02-25 这里只是模拟了网络请求
            refreshLayout.getLayout().postDelayed(() -> {
                mNewsAdapter.refresh(DemoDataProvider.getDemoNewInfos());
                refreshLayout.finishRefresh();
            }, 1000);
        });
        //上拉加载
        binding.refreshLayout.setOnLoadMoreListener(refreshLayout -> {
            // TODO: 2020-02-25 这里只是模拟了网络请求
            refreshLayout.getLayout().postDelayed(() -> {
                mNewsAdapter.loadMore(DemoDataProvider.getDemoNewInfos());
                refreshLayout.finishLoadMore();
            }, 1000);
        });
        binding.refreshLayout.autoRefresh();//第一次进入触发自动刷新，演示效果
    }
}
