package com.dongxun.lichunkai.cloudmusic.Class;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Environment;
import android.util.Log;

import com.dongxun.lichunkai.cloudmusic.Common.Common;

import java.io.File;
import java.io.IOException;


public class MusicMediaPlayer extends MediaPlayer {

    private static String TAG = "MusicMediaPlayer";
    private static MediaPlayer mediaPlayer;

    public MusicMediaPlayer(Context context) {
        initMediaPlayer(context);
    }

    /**
     * 初始化
     */
    public static void initMediaPlayer(Context context) {
        Log.d(TAG, "initMediaPlayer: 初始化");
        try {
            mediaPlayer = new MediaPlayer();
            File file = new File(Environment.getExternalStorageDirectory() + "/CloudMusic/mp3/", Common.song_playing.getId() + ".mp3");
            if (file.exists()){
                mediaPlayer.setDataSource(file.getPath());
                mediaPlayer.prepare();
                Log.d(TAG, "initMediaPlayer: 初始化完成");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 开始播放
     */
    public void startOption() {
        if (!mediaPlayer.isPlaying()){
            mediaPlayer.start();
            Log.d(TAG, "initMediaPlayer: 开始播放");
        }
    }

    /**
     * 暂停播放
     */
    public void pauseOption() {
        if (mediaPlayer.isPlaying()){
            mediaPlayer.pause();
            Log.d(TAG, "initMediaPlayer: 暂停播放");
        }
    }

    /**
     * 停止播放
     */
    public void stopOption() {
        if (mediaPlayer.isPlaying()){
            mediaPlayer.reset();
            Log.d(TAG, "initMediaPlayer: 停止播放");
        }
    }

    /**
     * 退出播放器
     */
    public void exitOption() {
        if (mediaPlayer != null){
            mediaPlayer.stop();
            mediaPlayer.release();
            Log.d(TAG, "initMediaPlayer: 退出播放器");
        }
    }

}
