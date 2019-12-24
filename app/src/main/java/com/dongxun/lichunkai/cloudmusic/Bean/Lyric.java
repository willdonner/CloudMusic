package com.dongxun.lichunkai.cloudmusic.Bean;

/**
 * 歌词类
 */
public class Lyric {
    private int time;//开始时间
    private String text;//歌词内容

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
