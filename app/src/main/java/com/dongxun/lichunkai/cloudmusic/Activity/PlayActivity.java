package com.dongxun.lichunkai.cloudmusic.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.dongxun.lichunkai.cloudmusic.Bean.Lyric;
import com.dongxun.lichunkai.cloudmusic.Bean.Song;
import com.dongxun.lichunkai.cloudmusic.Common.Common;
import com.dongxun.lichunkai.cloudmusic.LocalBroadcast.SendLocalBroadcast;
import com.dongxun.lichunkai.cloudmusic.PopWindow.ListWindow;
import com.dongxun.lichunkai.cloudmusic.PopWindow.SongDetailsWindow;
import com.dongxun.lichunkai.cloudmusic.R;
import com.dongxun.lichunkai.cloudmusic.Util.ToolHelper;
import com.gyf.immersionbar.ImmersionBar;
import com.martinrgb.animer.Animer;
import com.martinrgb.animer.core.interpolator.AndroidNative.AccelerateDecelerateInterpolator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.dongxun.lichunkai.cloudmusic.Common.Common.songList;

public class PlayActivity extends AppCompatActivity implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {

    private static String TAG = "PlayActivity";

    private ImageView imageView_close;
    private TextView textView_name;
    private TextView textView_author;

    private ImageView imageView_coverImg;

    private TextView textView_firstLyric;
    private TextView textView_secondLyric;
    private TextView textView_thirdLyric;

    private SeekBar seekBar;
    private TextView textView_nowTime;
    private TextView textView_sumTime;

    private ImageView imageView_lastSong;
    private ImageView imageView_playOrPause;
    private ImageView imageView_nextSong;

    private ImageView imageView_like;
    private ImageView imageView_loop;
    private ImageView imageView_comments;
    private ImageView imageView_list;
    private int time1000 = 15000;
    Handler handler = new Handler();

    //广播/接收器
    private LocalBroadcastManager localBroadcastManager;
    private TimeReceiver timeReceiver;
    private IntentFilter intentFilter;

    //变量
    private Boolean updateSeekbar = true;//是否更新进度条，用户自行调整进度时使用

    Animer.AnimerSolver solver1 = Animer.interpolatorDroid(new AccelerateDecelerateInterpolator(), 15000);

    // 模仿 ObjectAnimator 的构造
    private Animer animer1;

    private File sd = Environment.getExternalStorageDirectory();

    private String path_cover=sd.getPath()+"/CloudMusic/cover";//封面文件夹

    private String path_lyric=sd.getPath()+"/CloudMusic/lyric";//歌词文件夹

    private String path_details=sd.getPath()+"/CloudMusic/details";//歌曲详情文件夹

    private float windowAlpha = 1f;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        initStateBar();
        initView();
        getalbum(Common.song_playing.getalbumId());
        animer1 = new Animer(imageView_coverImg, solver1, Animer.ROTATION, 0, 360);
        initReceiver();
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            try {

                // 创建一个 Animer 解算器对象，采用了原生的插值动画类
                Animer.AnimerSolver solver1 = Animer.interpolatorDroid(new AccelerateDecelerateInterpolator(), 15000);

// 模仿 ObjectAnimator 的构造
                Animer animer1 = new Animer(imageView_coverImg, solver1, Animer.ROTATION, 0, 360);

                animer1.start();

                Log.i("1", "1");
                handler.postDelayed(this, time1000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    /**
     * 初始化状态栏
     */
    private void initStateBar() {
        ImmersionBar.with(PlayActivity.this).statusBarDarkFont(true).init();
        getSupportActionBar().hide();
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
    }

    /**
     * 初始化本地广播接收器
     */
    private void initReceiver() {
        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        intentFilter = new IntentFilter();
        intentFilter.addAction("com.dongxun.lichunkai.cloudmusic.TIME_BROADCAST");
        timeReceiver = new TimeReceiver();
        localBroadcastManager.registerReceiver(timeReceiver, intentFilter);
    }

    /**
     * 更新UI
     */
    private void refreshUI() {
        animer1.start();
        handler.postDelayed(runnable, time1000);

        textView_name.setText(Common.song_playing.getName());
        textView_author.setText(Common.song_playing.getArtist());
        if (Common.song_playing.getCover() != null) {
            //显示封面
            imageView_coverImg.setImageBitmap(Common.song_playing.getCover());
        } else {
            //加载封面
            if (Common.song_playing.getCoverURL() != null){
                //查看本地是否有封面图
                String file = path_cover + "/"+ Common.song_playing.getId() +".jpg";
                File coverFile = new File(file);
                if (coverFile.exists()){
                    Log.e(TAG, "refreshUI: 本地有歌曲封面");
                    //读取文件内容
                    Common.song_playing.setCover( ToolHelper.getLoacalBitmap(file));
                    //更新UI
                    imageView_coverImg.setImageBitmap(Common.song_playing.getCover());
                }else {
                    Log.e(TAG, "refreshUI: 本地没有歌曲封面");
                    //获取封面图
                    getCoverImage(Common.song_playing.getCoverURL());
                }
            }
        }
        //加载歌词
        if (Common.song_playing.getId() != null) {
            if (Common.song_playing.getLyricList() == null){
                //查看本地是否有歌词
                String file = path_lyric + "/"+ Common.song_playing.getId() +".txt";
                File lyricFile = new File(file);
                if (lyricFile.exists()){
                    Log.e(TAG, "refreshUI: 本地有歌词文本");
                    //读取文件内容
                    String localLyric = null;
                    try {
                        localLyric = ToolHelper.readTxtFile(file);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    //解析歌词
                    final List<Lyric> lyricList = new ArrayList<>();
                    String[] we = localLyric.split("\n");
                    for (String x : we) {
                        if (isInteger(x.substring(1, 3))) {
                            String time = x.substring(x.indexOf("[") + 1, x.indexOf("]"));
                            String text = x.substring(x.indexOf("]") + 1);
                            Lyric lyric = new Lyric();
                            lyric.setTime(toMillisecond(time));
                            lyric.setText(text);
                            if (text.trim().length() != 0) lyricList.add(lyric);
                        }
                    }
                    Common.song_playing.setLyricList(lyricList);
                }else {
                    Log.e(TAG, "refreshUI: 本地没有歌词文本");
                    getLyric(Common.song_playing.getId());
                }
            }
        }
        if (Common.state_playing) {
            imageView_playOrPause.setImageResource(R.drawable.logo_pause);

        } else imageView_playOrPause.setImageResource(R.drawable.logo_play);
        textView_sumTime.setText(generateTime(Common.song_playing.getSunTime()));
        textView_nowTime.setText(generateTime(Common.song_playing.getNowTime()));

        //更新循环
        switch (Common.loopType_playing){
            case 0: imageView_loop.setImageResource(R.drawable.logo_loop_random);break;
            case 1: imageView_loop.setImageResource(R.drawable.logo_loop_single);break;
            case 2: imageView_loop.setImageResource(R.drawable.logo_loop_list);break;
        }
    }


    /**
     * 将毫秒转时分秒
     *
     * @param time
     * @return
     */
    public static String generateTime(long time) {
        int totalSeconds = (int) (time / 1000);
        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    @Override
    protected void onResume() {
        refreshUI();
        super.onResume();
    }

    /**
     * 通过获取专辑💽得到封面图
     */
    private void getalbum(final String albumid) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    OkHttpClient client = new OkHttpClient();//新建一个OKHttp的对象
                    //和风请求方式
                    Request request = new Request.Builder()
                            .url("http://www.willdonner.top:3000/album?id="+ albumid +"")
                            .build();//创建一个Request对象
                    //第三步构建Call对象
                    Call call = client.newCall(request);
                    //第四步:异步get请求
                    call.enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                        }
                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            final String responseData = response.body().string();//处理返回的数据
                            try {
                                parseJSON(responseData);//解析JSON
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 解析JSON
     * @param responseData JSON字符串
     */
    private void parseJSON(String responseData) throws JSONException {
        JSONObject response = new JSONObject(responseData);
        String code = response.getString("code");
        if (code.equals("200")){
            JSONArray songs = response.getJSONArray("songs");
            for (int i=0;i<1;i++){
                JSONObject songInfo = songs.getJSONObject(i);
                String coverURL = songInfo.getJSONObject("al").getString("picUrl");
                getCoverImage(coverURL);
                Song song = new Song();
                song.setCoverURL(coverURL);
                songList.add(song);
            }
        }else {
            //显示错误信息
        }
    }

    /**
     * 加载网络图片，获取网络图片的bitmap
     *
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
                    //存储封面图
                    ToolHelper.saveBitmap(bitmap,path_cover+"/"+ Common.song_playing.getId() +".jpg");
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
     * 加载歌词
     *
     * @param id：歌曲ID
     */
    public void getLyric(final String id) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();//新建一个OKHttp的对象
                    //和风请求方式
                    Request request = new Request.Builder()
                            .url("http://www.willdonner.top:3000/lyric?id=" + id + "")
                            .build();//创建一个Request对象
                    //第三步构建Call对象http://www.willdonner.top:3000/lyric?id=" + id + "
                    Call call = client.newCall(request);
                    //第四步:异步get请求
                    call.enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            final String responseData = response.body().string();//处理返回的数据
                            //解析歌词
                            try {
                                String lycirs = new JSONObject(responseData).getJSONObject("lrc").getString("lyric");
                                Log.d(TAG, "onResponse: " + lycirs.trim());

                                final List<Lyric> lyricList = new ArrayList<>();
                                String[] we = lycirs.split("\n");
                                for (String x : we) {
                                    if (isInteger(x.substring(1, 3))) {
                                        String time = x.substring(x.indexOf("[") + 1, x.indexOf("]"));
                                        String text = x.substring(x.indexOf("]") + 1);
                                        Lyric lyric = new Lyric();
                                        lyric.setTime(toMillisecond(time));
                                        lyric.setText(text);
                                        if (text.trim().length() != 0) lyricList.add(lyric);
                                    }
                                }
                                Common.song_playing.setLyricList(lyricList);

                                //显示歌词
                                Log.d(TAG, "歌词: ————————————————————————————————————");
                                for (Lyric lyric : lyricList) {
                                    Log.d(TAG, "onResponse: " + lyric.getTime() + "：" + lyric.getText());
                                }
                                //存储歌词
                                Log.d(TAG, "存储歌词: ————————————————————————————————————");

                                ToolHelper.creatTxtFile(path_lyric,id);
                                ToolHelper.writeTxtFile(path_lyric,id,lycirs.trim());

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 将歌词时间转换为毫秒
     *
     * @param str
     * @return
     */
    public static int toMillisecond(String str) {
        int minute = Integer.valueOf(str.substring(0, 2));
        int second = Integer.valueOf(str.substring(3, 5));
        int millisecond = Integer.valueOf(str.substring(6));
        return minute * 60 * 1000 + second * 1000 + millisecond;
    }

    /**
     * 判断是否为数字
     *
     * @param str
     * @return
     */
    public static boolean isInteger(String str) {
        Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
        return pattern.matcher(str).matches();
    }

    /**
     * 初始化组件
     */
    private void initView() {
        imageView_close = findViewById(R.id.imageView_close);
        imageView_close.setOnClickListener(this);
        textView_name = findViewById(R.id.textView_name);
        textView_author = findViewById(R.id.textView_author);

        imageView_coverImg = findViewById(R.id.imageView_coverImg);
        imageView_coverImg.setOnClickListener(this);

        textView_firstLyric = findViewById(R.id.textView_firstLyric);
        textView_secondLyric = findViewById(R.id.textView_secondLyric);
        textView_thirdLyric = findViewById(R.id.textView_thirdLyric);

        seekBar = findViewById(R.id.seekBar);
        seekBar.setMax(Common.song_playing.getSunTime());
        seekBar.setProgress(Common.song_playing.getNowTime());
        seekBar.setOnSeekBarChangeListener(this);
        textView_nowTime = findViewById(R.id.textView_nowTime);
        textView_sumTime = findViewById(R.id.textView_sumTime);

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

    /**
     * 点击事件
     *
     * @param view
     */
    @SuppressLint("ResourceAsColor")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imageView_close:
                finish();
                break;
            case R.id.imageView_coverImg:
                //弹出歌曲详情窗口
                SongDetailsWindow songDetailsWindow = new SongDetailsWindow(this);
                songDetailsWindow.show();
                break;
            case R.id.imageView_lastSong:
                if (songList.size() != 0) {
                    SendLocalBroadcast.last(this);
                } else {
                    Toast.makeText(this, "歌单空空如也", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.imageView_playOrPause:
                Toast.makeText(this,"播放/暂停",Toast.LENGTH_SHORT).show();
                if (Common.song_playing.getId() != null) {
                    //更新UI
                    if (Common.state_playing){
                        //暂停
                        imageView_playOrPause.setImageResource(R.drawable.logo_play);
                        handler.removeCallbacks(runnable);
                        animer1.cancel();
                    }else {
                        //播放
                        imageView_playOrPause.setImageResource(R.drawable.logo_pause);
                        animer1.start();
                        handler.postDelayed(runnable, time1000);
                    }
                    //发送本地广播播放
                    SendLocalBroadcast.playOrPause(this);
                } else {
                    Toast.makeText(this, "当前暂无歌曲，快去选一首吧", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.imageView_nextSong:
                if (songList.size() != 0) {
                    SendLocalBroadcast.next(this);
                } else {
                    Toast.makeText(this, "歌单空空如也", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.imageView_like:
                if (Common.song_playing.getId() != null) {
                    Toast.makeText(this, "收藏", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "当前暂无歌曲，快去选一首吧", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.imageView_loop:
                Toast.makeText(this, "循环", Toast.LENGTH_SHORT).show();
                break;
            case R.id.imageView_comments:
                if (Common.song_playing.getId() != null) {
                    Toast.makeText(this, "评论", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "当前暂无歌曲，快去选一首吧", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.imageView_list:
                Toast.makeText(this, "歌单", Toast.LENGTH_SHORT).show();
                ListWindow listWindow = new ListWindow(this);
                listWindow.show();
                break;
        }
    }


    /**
     * 进度条监听
     *
     * @param seekBar
     * @param i
     * @param b
     */
    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        if (Common.song_playing.getId() != null) {
            switch (seekBar.getId()) {
                case R.id.seekBar:
                    if (!updateSeekbar)
                        textView_nowTime.setText(generateTime(Long.valueOf(seekBar.getProgress())));
                    break;
            }
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        if (Common.song_playing.getId() != null) {
            switch (seekBar.getId()) {
                case R.id.seekBar:
                    Log.d(TAG, "onStartTrackingTouch: 开始");
                    updateSeekbar = false;
                    break;
            }
        }
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        if (Common.song_playing.getId() != null) {
            switch (seekBar.getId()) {
                case R.id.seekBar:
                    Log.d(TAG, "onStartTrackingTouch: 结束");
                    updateSeekbar = true;
                    //调整歌曲进度
                    Common.changeProgress = Integer.valueOf(seekBar.getProgress());
                    Common.lyricPosition_playing = 0;
                    //发送广播
                    SendLocalBroadcast.changeProgress(this);
                    break;
            }
        }

    }

    /**
     * 本地广播接收器（刷新时间）
     */
    class TimeReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getStringExtra("ACTION");
            switch (action) {
                case "REFRESH":
                    //更新UI
                    textView_sumTime.setText(generateTime(Common.song_playing.getSunTime()));
                    if (updateSeekbar)
                        textView_nowTime.setText(generateTime(Common.song_playing.getNowTime()));

                    if (Common.state_playing) {
                        imageView_playOrPause.setImageResource(R.drawable.logo_pause);
                    } else imageView_playOrPause.setImageResource(R.drawable.logo_play);
                    seekBar.setMax(Common.song_playing.getSunTime());
                    if (updateSeekbar) seekBar.setProgress(Common.song_playing.getNowTime());
                    break;
                case "COMPLETE":
                    imageView_playOrPause.setImageResource(R.drawable.logo_play);
                    handler.removeCallbacks(runnable);
                    animer1.cancel();
                    if (updateSeekbar)
                        textView_nowTime.setText(generateTime(Common.song_playing.getNowTime()));
                    if (updateSeekbar) seekBar.setProgress(Common.song_playing.getNowTime());
                    //歌词回到最开始
                    Common.lyricPosition_playing = 0;
                    if (Common.song_playing.getLyricList() != null){
                        textView_firstLyric.setText(Common.song_playing.getLyricList().get(0).getText());
                        textView_secondLyric.setText(Common.song_playing.getLyricList().get(1).getText());
                        textView_thirdLyric.setText(Common.song_playing.getLyricList().get(2).getText());
                    }

                    break;
                case "LYRIC":
                    //歌词滚动
                    //获取当前歌词
                    if (Common.song_playing.getLyricList() != null){
                        List<Lyric> lyrics = Common.song_playing.getLyricList();
                        if (lyrics.size() - Common.lyricPosition_playing >= 3) {
                            lyric_loop: for (int i=Common.lyricPosition_playing;i<lyrics.size();i++){
                                if (lyrics.get(i).getTime() > Common.song_playing.getNowTime()){
                                    if (i-1 >= 0 && i-1 < lyrics.size()) textView_firstLyric.setText(lyrics.get(i-1).getText());
                                    textView_secondLyric.setText(lyrics.get(i).getText());
                                    if (i+1 >= 0 && i+1 < lyrics.size()) textView_thirdLyric.setText(lyrics.get(i+1).getText());
                                    Common.lyricPosition_playing = i;
                                    break lyric_loop;
                                }
                            }
                        }
                    }

                    break;
                case "LOOP":
                    switch (Common.loopType_playing){
                        case 0: imageView_loop.setImageResource(R.drawable.logo_loop_random);break;
                        case 1: imageView_loop.setImageResource(R.drawable.logo_loop_single);break;
                        case 2: imageView_loop.setImageResource(R.drawable.logo_loop_list);break;
                    }
                    break;
                case "COVER":
                    refreshUI();
                    break;
            }
        }
    }
}
