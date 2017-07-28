package com.zwhd.appbase.download;

import java.io.Serializable;

/**
 * 广告模型
 * Created by YI on 2015/12/10.
 */
public class AdvModel implements Serializable{
    /**
     * 广告的标题
     */
    String title;
    /**
     * 广告的描述
     */
    String desc;
    /**
     * 广告的下载地址
     */
    String url;
    /**
     * 广告的图片地址
     */
    String resourceUrl;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getResourceUrl() {
        return resourceUrl;
    }

    public void setResourceUrl(String resourceUrl) {
        this.resourceUrl = resourceUrl;
    }

    @Override
    public String toString() {
        return "AdvModel{" +
                "title='" + title + '\'' +
                ", desc='" + desc + '\'' +
                ", url='" + url + '\'' +
                ", resourceUrl='" + resourceUrl + '\'' +
                '}';
    }
}
