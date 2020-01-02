package com.dongxun.lichunkai.cloudmusic.Bean;

import android.graphics.Bitmap;

/**
 * 用户信息类
 */
public class User {
    private String userId;//用户ID
    private String gender;//性别
    private String birthday;//生日
    private String nickname;//昵称
    private String city;//城市
    private String province;//省份
    private String avatarUrl;//头像Url
    private String backgroundUrl;//背景图Url
    private Bitmap avatarUrl_bitmap;//头像
    private Bitmap backgroundUrl_bitmap;//背景图

    public Bitmap getAvatarUrl_bitmap() {
        return avatarUrl_bitmap;
    }

    public void setAvatarUrl_bitmap(Bitmap avatarUrl_bitmap) {
        this.avatarUrl_bitmap = avatarUrl_bitmap;
    }

    public Bitmap getBackgroundUrl_bitmap() {
        return backgroundUrl_bitmap;
    }

    public void setBackgroundUrl_bitmap(Bitmap backgroundUrl_bitmap) {
        this.backgroundUrl_bitmap = backgroundUrl_bitmap;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getBackgroundUrl() {
        return backgroundUrl;
    }

    public void setBackgroundUrl(String backgroundUrl) {
        this.backgroundUrl = backgroundUrl;
    }
}