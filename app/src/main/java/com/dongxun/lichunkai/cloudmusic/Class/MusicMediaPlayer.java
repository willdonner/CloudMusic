package com.dongxun.lichunkai.cloudmusic.Class;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaTimestamp;
import android.os.Environment;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.dongxun.lichunkai.cloudmusic.Activity.MainActivity;
import com.dongxun.lichunkai.cloudmusic.Activity.PlayActivity;
import com.dongxun.lichunkai.cloudmusic.Common.Common;
import com.dongxun.lichunkai.cloudmusic.LocalBroadcast.SendLocalBroadcast;

import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 音频多媒体类
 */
public class MusicMediaPlayer extends MediaPlayer {

    private static String TAG = "MusicMediaPlayer";
    private static MediaPlayer mediaPlayer;
    private static  Timer timer_seekBar;
    private static  Timer timer_Lyric;
    private static Context mcontext;

    public MusicMediaPlayer(Context context) {
        initMediaPlayer(context);
    }

    /**
     * 初始化
     */
    public static void initMediaPlayer(final Context context) {
        Log.d(TAG, "initMediaPlayer: 初始化播放器");
        mcontext = context;
        try {
            mediaPlayer = new MediaPlayer();
            File file = new File(Environment.getExternalStorageDirectory() + "/CloudMusic/mp3/", Common.song_playing.getId() + ".mp3");
            if (file.exists()){
                mediaPlayer.setDataSource(file.getPath());
                mediaPlayer.prepare();
                Log.d(TAG, "initMediaPlayer: 播放器初始化完成");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        /**
         * 音频播放完成
         */
        mediaPlayer.setOnCompletionListener(new OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                Common.song_playing.setNowTime(0);
                //发送播放完成广播（音乐控制）
                SendLocalBroadcast.completeMusic(context);
                //发送播放完成广播（时间控制）
                SendLocalBroadcast.completeTime(context);
            }
        });

        /**
         * 歌曲时间子线程（1000ms刷新一次)
         */
        timer_seekBar = new Timer();
        timer_seekBar.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (Common.state_playing){
                    Common.song_playing.setNowTime(mediaPlayer.getCurrentPosition());
//                    Log.e("TAG","时间：" + Common.song_playing.getNowTime());
                    //发送刷新时间广播
                    SendLocalBroadcast.refreshTime(context);
                }
            }
        },0,1000);

        /**
         * 歌词子线程(100ms刷新一次)
         */
        timer_Lyric = new Timer();
        timer_seekBar.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (Common.state_playing){
//                    Log.e("TAG","歌词：" + Common.song_playing.getNowTime());
                    //发送刷新歌词广播
                    SendLocalBroadcast.refreshLyric(context);
                }
            }
        },0,500);
    }


    /**
     * 更改进度
     */
    public void seekToOption(){
        mediaPlayer.seekTo(Common.changeProgress);
    }

    /**
     * 是否正在播放
     * @return
     */
    public boolean isPlayingOption(){
        return mediaPlayer.isPlaying();
    }

    /**
     * 开始播放
     */
    public void startOption() {
        if (!mediaPlayer.isPlaying()){
            mediaPlayer.start();
            Common.state_playing = true;
            Log.d(TAG, "initMediaPlayer: 开始播放");
        }
    }

    /**
     * 暂停播放
     */
    public void pauseOption() {
        if (mediaPlayer.isPlaying()){
            mediaPlayer.pause();
            timer_seekBar.cancel();
            timer_Lyric.cancel();
            Common.state_playing = false;
            Log.d(TAG, "initMediaPlayer: 暂停播放");
        }
    }

    /**
     * 停止播放
     */
    public void stopOption() {
        if (mediaPlayer.isPlaying()){
            mediaPlayer.reset();
            timer_seekBar.cancel();
            timer_Lyric.cancel();
            Common.state_playing = false;
            initMediaPlayer(mcontext);
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
