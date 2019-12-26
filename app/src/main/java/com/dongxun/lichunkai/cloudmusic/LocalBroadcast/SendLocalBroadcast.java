package com.dongxun.lichunkai.cloudmusic.LocalBroadcast;

import android.content.Context;
import android.content.Intent;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

/**
 * 发送本地广播
 */
public class SendLocalBroadcast {

    private static Intent intent;
    private static LocalBroadcastManager localBroadcastManager;

    /**
     * 播放新的音频
     */
    public static void playNew(Context context) {
        intent = new Intent("com.dongxun.lichunkai.cloudmusic.MUSIC_BROADCAST");
        intent.putExtra("ACTION","PLAYNEW");
        localBroadcastManager = LocalBroadcastManager.getInstance(context);
        localBroadcastManager.sendBroadcast(intent);
    }

    /**
     * 播放/暂停音频
     */
    public static void playOrPause(Context context) {
        intent = new Intent("com.dongxun.lichunkai.cloudmusic.MUSIC_BROADCAST");
        intent.putExtra("ACTION","PLAY_PAUSE");
        localBroadcastManager = LocalBroadcastManager.getInstance(context);
        localBroadcastManager.sendBroadcast(intent);
    }

    /**
     * 上一曲
     */
    public static void last(Context context) {
        intent = new Intent("com.dongxun.lichunkai.cloudmusic.MUSIC_BROADCAST");
        intent.putExtra("ACTION","LAST");
        localBroadcastManager = LocalBroadcastManager.getInstance(context);
        localBroadcastManager.sendBroadcast(intent);
    }

    /**
     * 下一曲
     */
    public static void next(Context context) {
        intent = new Intent("com.dongxun.lichunkai.cloudmusic.MUSIC_BROADCAST");
        intent.putExtra("ACTION","NEXT");
        localBroadcastManager = LocalBroadcastManager.getInstance(context);
        localBroadcastManager.sendBroadcast(intent);
    }

    /**
     * 更改进度
     */
    public static void changeProgress(Context context) {
        intent = new Intent("com.dongxun.lichunkai.cloudmusic.MUSIC_BROADCAST");
        intent.putExtra("ACTION","CHANGEPROGRESS");
        localBroadcastManager = LocalBroadcastManager.getInstance(context);
        localBroadcastManager.sendBroadcast(intent);
    }

    /**
     * 播放完成(音频控制)
     */
    public static void completeMusic(Context context) {
        intent = new Intent("com.dongxun.lichunkai.cloudmusic.MUSIC_BROADCAST");
        intent.putExtra("ACTION","COMPLETE");
        localBroadcastManager = LocalBroadcastManager.getInstance(context);
        localBroadcastManager.sendBroadcast(intent);
    }

    /**
     * 刷新时间及进度条
     */
    public static void refreshTime(Context context) {
        intent = new Intent("com.dongxun.lichunkai.cloudmusic.TIME_BROADCAST");
        intent.putExtra("ACTION","REFRESH");
        localBroadcastManager = LocalBroadcastManager.getInstance(context);
        localBroadcastManager.sendBroadcast(intent);
    }

    /**
     * 刷新歌词
     */
    public static void refreshLyric(Context context) {
        intent = new Intent("com.dongxun.lichunkai.cloudmusic.TIME_BROADCAST");
        intent.putExtra("ACTION","LYRIC");
        localBroadcastManager = LocalBroadcastManager.getInstance(context);
        localBroadcastManager.sendBroadcast(intent);
    }

    /**
     * 播放完成（时间控制）
     */
    public static void completeTime(Context context) {
        intent = new Intent("com.dongxun.lichunkai.cloudmusic.TIME_BROADCAST");
        intent.putExtra("ACTION","COMPLETE");
        localBroadcastManager = LocalBroadcastManager.getInstance(context);
        localBroadcastManager.sendBroadcast(intent);
    }

    /**
     * 刷新循环方式（时间控制）
     */
    public static void refreshLoop(Context context) {
        intent = new Intent("com.dongxun.lichunkai.cloudmusic.TIME_BROADCAST");
        intent.putExtra("ACTION","LOOP");
        localBroadcastManager = LocalBroadcastManager.getInstance(context);
        localBroadcastManager.sendBroadcast(intent);
    }
}
