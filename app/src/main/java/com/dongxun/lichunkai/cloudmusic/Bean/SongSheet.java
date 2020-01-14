package com.dongxun.lichunkai.cloudmusic.Bean;

import android.graphics.Bitmap;

import java.io.Serializable;

/**
 * 歌单类
 */
public class SongSheet implements Serializable {
    private String id;//歌单id
    private String name;//歌单名
    private String copywriter;//文案
    private String picUrl;//封面图URL
    private Bitmap pic;//封面图
    private String playCount;//播放量
    private Creator creator;//创建人
    private String trackCount;//歌曲数量
    private String description;//歌单描述
    private String subscribedCount;//订阅数

    public String getSubscribedCount() {
        return subscribedCount;
    }

    public void setSubscribedCount(String subscribedCount) {
        this.subscribedCount = subscribedCount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTrackCount() {
        return trackCount;
    }

    public void setTrackCount(String trackCount) {
        this.trackCount = trackCount;
    }

    public Creator getCreator() {
        return creator;
    }

    public void setCreator(Creator creator) {
        this.creator = creator;
    }

    public Bitmap getPic() {
        return pic;
    }

    public void setPic(Bitmap pic) {
        this.pic = pic;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCopywriter() {
        return copywriter;
    }

    public void setCopywriter(String copywriter) {
        this.copywriter = copywriter;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public String getPlayCount() {
        return playCount;
    }

    public void setPlayCount(String playCount) {
        this.playCount = playCount;
    }
}
