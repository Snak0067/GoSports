/*
 * Copyright (C) 2020 xuexiangjys(xuexiangjys@163.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.xiaofeng.GoSports.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.xiaofeng.GoSports.adapter.entity.NewInfo;
import com.xiaofeng.GoSports.R;
import com.xuexiang.xaop.annotation.MemoryCache;
import com.xuexiang.xui.adapter.simple.AdapterItem;
import com.xuexiang.xui.utils.ResUtils;
import com.xuexiang.xui.widget.banner.widget.banner.BannerItem;

import java.util.ArrayList;
import java.util.List;

/**
 * 演示数据
 *
 * @author xiaofeng
 * @since 2022/5/23 下午5:52
 */
public class DemoDataProvider {

    public static String[] titles = new String[]{
            "增肌还是减脂？我来教你吧",
            "AI 跳绳,记录你进步的每一下",
            "今天，健康目标达成了吗？",
            "健康数据,身体状态在线，事事都在线",
            "与家人一起，为健康添砖加瓦"
    };

    public static String[] urls = new String[]{
            "https://consumer.huawei.com/content/dam/huawei-cbg-site/cn/mkt/mobileservices/health/imgs/huawei-health-fitness-goal@2x.webp",
            "https://consumer.huawei.com/content/dam/huawei-cbg-site/cn/mkt/mobileservices/health/imgs/huawei-health-fitness-arrangement@2x.webp",
            "https://consumer.huawei.com/content/dam/huawei-cbg-site/cn/mkt/mobileservices/health/imgs/s30-bg@2x.webp",
            "https://consumer.huawei.com/content/dam/huawei-cbg-site/cn/mkt/mobileservices/health/imgs/huawei-health-activities@2x.webp",
            "https://wx3.sinaimg.cn/mw2000/001VUOSZly1h0v587czexj62yo1o0kjl02.jpg"

    };

    @MemoryCache
    public static List<BannerItem> getBannerList() {
        List<BannerItem> list = new ArrayList<>();
        for (int i = 0; i < urls.length; i++) {
            BannerItem item = new BannerItem();
            item.imgUrl = urls[i];
            item.title = titles[i];
            list.add(item);
        }
        return list;
    }

    /**
     * 用于占位的空信息
     *
     * @return
     */
    @MemoryCache
    public static List<NewInfo> getDemoNewInfos() {
        List<NewInfo> list = new ArrayList<>();
        list.add(new NewInfo("公众号", "近视眼戴眼镜的和不戴眼镜的哪个度数升得更快")
                .setSummary("患者宁可看不清东西，也不愿戴眼镜，其原因是担心戴上眼镜后近视度数会越来越深。那么近视镜的度数真的是越戴越深吗？这是一种认识上的误区。")
                .setDetailUrl("https://h5.baike.qq.com/mobile/article.html?docid=qa00002011txxxok&VNK=f8137c07")
                .setImageUrl("https://baike-med-1256891581.file.myqcloud.com/2021041/51ce2bd0-a58d-11eb-87f5-87f8260230cc_0.jpg"));

        list.add(new NewInfo("网页", "多组分锻炼计划能提高老年人的身体素质吗？")
                .setSummary("本研究的目的是评估参加5年多组分锻炼计划的老年人的身体健康状况。样本包括138名60-93岁的老年人（70.4±7.8岁），通过高级体能测试（肌力、柔韧性、平衡性和心肺健康）进行评估。")
                .setDetailUrl("http://www.sporthealth.cn/article-607-1.html")
                .setImageUrl("https://staticd.baike.qq.com/2022020/991afcf0-8757-11ec-a661-7567a8bed242_0.jpg?imageMogr2/thumbnail/x152"));

        list.add(new NewInfo("公众号", "治疗肾结石需要“开刀”吗？")
                .setSummary("外科手术治疗可取出介入手术无法取出的结石。近年来，随着介入手术技术的发展及应用，肾结石的治疗取得了突破性的进展，开放性手术在治疗中的运用显著减少，肾结石患者中采用外科开刀手术仅占 1%～5.4%")
                .setDetailUrl("https://h5.baike.qq.com/mobile/article.html?docid=tx00071001bvwl4h&VNK=4b729079")
                .setImageUrl("https://static4.baike.qq.com/2021120/3b98bef0-6312-11ec-9a9e-fb422aaf03c8_0.jpg?imageMogr2/thumbnail/x152"));

        list.add(new NewInfo("公众号", "患了糖尿病，还能喝酒吗？")
                .setSummary("第一，酒精一开始可以升高血糖，随后导致血糖水平降低。因为醉酒后的表现与低血糖症状（嗜睡和定向障碍））很相似，所以患者可能并不知道自己已经出现了低血糖。")
                .setDetailUrl("https://h5.baike.qq.com/mobile/article.html?docid=qa00003011tgmzwg&VNK=fee360c5")
                .setImageUrl("https://baike-med-1256891581.file.myqcloud.com/2018031/fd739290-1deb-11e8-ab39-b5cd33858281_0.png"));

        list.add(new NewInfo("公众号", "咽喉不适，咽炎还是食管癌？")
                .setSummary("食管癌的早期症状比较轻微，与慢性咽炎十分相似，都表现为咽部不适、有异物感和吞咽困难。让我们来重点看一下，食管癌早期症状和慢性咽炎的区别：")
                .setDetailUrl("https://h5.baike.qq.com/mobile/article.html?docid=qa10658011ff5zvp&VNK=121c078a")
                .setImageUrl("https://staticd.baike.qq.com/2022020/991afcf0-8757-11ec-a661-7567a8bed242_0.jpg?imageMogr2/thumbnail/x152"));
        return list;
    }

    public static List<AdapterItem> getGridItems(Context context) {
        return getGridItems(context, R.array.grid_titles_entry, R.array.grid_icons_entry);
    }


    private static List<AdapterItem> getGridItems(Context context, int titleArrayId, int iconArrayId) {
        List<AdapterItem> list = new ArrayList<>();
        String[] titles = ResUtils.getStringArray(titleArrayId);
        Drawable[] icons = ResUtils.getDrawableArray(context, iconArrayId);
        for (int i = 0; i < titles.length; i++) {
            list.add(new AdapterItem(titles[i], icons[i]));
        }
        return list;
    }

    /**
     * 用于占位的空信息
     *
     * @return
     */
    @MemoryCache
    public static List<NewInfo> getEmptyNewInfo() {
        List<NewInfo> list = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            list.add(new NewInfo());
        }
        return list;
    }

}
