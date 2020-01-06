package com.dongxun.lichunkai.cloudmusic.Bean;

import android.graphics.Bitmap;

/**
 * 歌单类
 */
public class SongSheet {
    private String id;//歌单id
    private String name;//歌单名
    private String copywriter;//文案
    private String picUrl;//封面图URL
    private Bitmap pic;//封面图
    private String playCount;//播放量

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
