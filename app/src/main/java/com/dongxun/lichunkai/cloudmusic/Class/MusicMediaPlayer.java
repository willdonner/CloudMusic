package com.dongxun.lichunkai.cloudmusic.Class;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Environment;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.dongxun.lichunkai.cloudmusic.Activity.MainActivity;
import com.dongxun.lichunkai.cloudmusic.Activity.PlayActivity;
import com.dongxun.lichunkai.cloudmusic.Common.Common;

import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;


public class MusicMediaPlayer extends MediaPlayer {

    private static String TAG = "MusicMediaPlayer";
    private static MediaPlayer mediaPlayer;
    private static  Timer timer;

    public MusicMediaPlayer(Context context) {
        initMediaPlayer(context);
    }

    /**
     * 初始化
     */
    public static void initMediaPlayer(final Context context) {
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
        mediaPlayer.setOnCompletionListener(new OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                //发送播放完广播
                Intent intent_broadcast = new Intent("com.dongxun.lichunkai.cloudmusic.MUSIC_BROADCAST");
                intent_broadcast.putExtra("ACTION","COMPLETE");
                LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(context);
                localBroadcastManager.sendBroadcast(intent_broadcast);
            }
        });
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (Common.state_playing){
                    Common.song_playing.setNowTime(mediaPlayer.getCurrentPosition());
                    Log.e("TAG","没隔1秒执行一次操作" + Common.song_playing.getNowTime());
                    //发送刷新时间广播
                    Intent intent_broadcast = new Intent("com.dongxun.lichunkai.cloudmusic.TIME_BROADCAST");
                    intent_broadcast.putExtra("ACTION","REFRESH");
                    LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(context);
                    localBroadcastManager.sendBroadcast(intent_broadcast);
                }
            }
        },0,1000);
    }


    /**
     * 更改进度
     */
    public void seekToOption(){
        mediaPlayer.seekTo(Common.changeProgress);
    }

    /**
     * 返回是否在播放
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
            timer.cancel();
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
            timer.cancel();
            Common.state_playing = false;
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
