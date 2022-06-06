package com.xiaofeng.GoSports.Model;

/**
 * @author xiaofeng
 * @description 跑步运动记录对象
 * @date 2022/6/6.
 */
public class RunningInfo {
    /**
     * 标签
     */
    private String Tag;
    /**
     * 标题
     */
    private String style;

    /**
     * 日期
     */
    private String date;
    /**
     * 距离
     */
    private String distance;
    /**
     * 跑步时长
     */
    private String duration;
    /**
     * 新闻的详情地址
     */
    private String recordSize;
    /**
     * 步数
     */
    private String steps;

    public RunningInfo() {
    }

    public RunningInfo(String tag, String style, String date, String distance, String duration, String recordSize, String steps) {
        Tag = tag;
        this.style = style;
        this.date = date;
        this.distance = distance;
        this.duration = duration;
        this.recordSize = recordSize;
        this.steps = steps;
    }

    public String getSteps() {
        return steps;
    }

    public void setSteps(String steps) {
        this.steps = steps;
    }

    public String getTag() {
        return Tag;
    }

    public void setTag(String tag) {
        Tag = tag;
    }

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getRecordSize() {
        return recordSize;
    }

    public void setRecordSize(String recordSize) {
        this.recordSize = recordSize;
    }

    @Override
    public String toString() {
        return "RunningInfo{" +
                "Tag='" + Tag + '\'' +
                ", style='" + style + '\'' +
                ", date='" + date + '\'' +
                ", distance='" + distance + '\'' +
                ", duration='" + duration + '\'' +
                ", steps='" + steps + '\'' +
                ", recordSize='" + recordSize + '\'' +
                '}';
    }
}
