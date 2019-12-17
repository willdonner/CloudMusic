package com.dongxun.lichunkai.cloudmusic.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.dongxun.lichunkai.cloudmusic.Common.Common;
import com.dongxun.lichunkai.cloudmusic.R;
import com.gyf.immersionbar.ImmersionBar;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class PlayActivity extends AppCompatActivity implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {

    private TextView textView_name;
    private TextView textView_author;
    private ImageView imageView_close;
    private ImageView imageView_coverImg;
    private SeekBar seekBar;
    private ImageView imageView_lastSong;
    private ImageView imageView_playOrPause;
    private ImageView imageView_nextSong;
    private ImageView imageView_like;
    private ImageView imageView_loop;
    private ImageView imageView_comments;
    private ImageView imageView_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        initStateBar();
        initView();
        refreshUI();
    }

    /**
     * 初始化状态栏
     */
    private void initStateBar() {
        ImmersionBar.with(this).init();
        getSupportActionBar().hide();
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
    }

    /**
     * 更新UI
     */
    private void refreshUI() {
        textView_name.setText(Common.song_playing.getName());
        textView_author.setText(Common.song_playing.getArtist());
        if (Common.song_playing.getCover() != null){
            imageView_coverImg.setImageBitmap(Common.song_playing.getCover());
        }else {
            getCoverImage(Common.song_playing.getCoverURL());
        }
    }

    /**
     * 加载网络图片，获取网络图片的bitmap
     * @param url：网络图片的地址
     * @return
     */
    public void getCoverImage(final String url) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Bitmap bitmap = null;
                    URL myurl = new URL(url);
                    // 获得连接
                    HttpURLConnection conn = (HttpURLConnection) myurl.openConnection();
                    conn.setConnectTimeout(6000);//设置超时
                    conn.setDoInput(true);
                    conn.setUseCaches(false);//不缓存
                    conn.connect();
                    InputStream is = conn.getInputStream();//获得图片的数据流
                    bitmap = BitmapFactory.decodeStream(is);
                    //修改公共变量和更新UI
                    Common.song_playing.setCover(bitmap);
                    //切换主线程更新UI
                    imageView_coverImg.post(new Runnable() {
                        @Override
                        public void run() {
                            imageView_coverImg.setImageBitmap(Common.song_playing.getCover());
                        }
                    });
                    is.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 初始化组件
     */
    private void initView() {
        textView_name = findViewById(R.id.textView_name);
        textView_author = findViewById(R.id.textView_author);
        imageView_close = findViewById(R.id.imageView_close);
        imageView_close.setOnClickListener(this);
        imageView_coverImg = findViewById(R.id.imageView_coverImg);
        imageView_coverImg.setOnClickListener(this);
        seekBar = findViewById(R.id.seekBar);
        seekBar.setOnSeekBarChangeListener(this);
        imageView_lastSong = findViewById(R.id.imageView_lastSong);
        imageView_lastSong.setOnClickListener(this);
        imageView_playOrPause = findViewById(R.id.imageView_playOrPause);
        imageView_playOrPause.setOnClickListener(this);
        imageView_nextSong = findViewById(R.id.imageView_nextSong);
        imageView_nextSong.setOnClickListener(this);
        imageView_like = findViewById(R.id.imageView_like);
        imageView_like.setOnClickListener(this);
        imageView_loop = findViewById(R.id.imageView_loop);
        imageView_loop.setOnClickListener(this);
        imageView_comments = findViewById(R.id.imageView_comments);
        imageView_comments.setOnClickListener(this);
        imageView_list = findViewById(R.id.imageView_list);
        imageView_list.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.imageView_close:
                finish();
                break;
            case R.id.imageView_coverImg:
                Toast.makeText(this,"封面",Toast.LENGTH_SHORT).show();
                break;
            case R.id.imageView_lastSong:
                Toast.makeText(this,"上一曲",Toast.LENGTH_SHORT).show();
                break;
            case R.id.imageView_playOrPause:
                Toast.makeText(this,"播放/暂停",Toast.LENGTH_SHORT).show();
                break;
            case R.id.imageView_nextSong:
                Toast.makeText(this,"下一曲",Toast.LENGTH_SHORT).show();
                break;
            case R.id.imageView_like:
                Toast.makeText(this,"收藏",Toast.LENGTH_SHORT).show();
                break;
            case R.id.imageView_loop:
                Toast.makeText(this,"循环",Toast.LENGTH_SHORT).show();
                break;
            case R.id.imageView_comments:
                Toast.makeText(this,"评论",Toast.LENGTH_SHORT).show();
                break;
            case R.id.imageView_list:
                Toast.makeText(this,"歌单",Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        Toast.makeText(this,"当前进度：" + seekBar.getProgress(),Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}
