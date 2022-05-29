package com.xiaofeng.GoSports.core.http.entity;

import androidx.annotation.Keep;

/**
 * @author xiaofeng
 * @since 2022-08-28 15:35
 */
@Keep
public class TipInfo {


    private String title;
    private String content;

    public TipInfo() {
    }

    public TipInfo(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "TipInfo{" +
                "title='" + title + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}
