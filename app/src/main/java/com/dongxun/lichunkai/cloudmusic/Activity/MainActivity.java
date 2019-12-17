package com.dongxun.lichunkai.cloudmusic.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dongxun.lichunkai.cloudmusic.Class.MusicMediaPlayer;
import com.dongxun.lichunkai.cloudmusic.Common.Common;
import com.dongxun.lichunkai.cloudmusic.R;
import com.gyf.immersionbar.ImmersionBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView imageView_search;

    //其他
    private LocalBroadcastManager localBroadcastManager;
    private MusicReceiver musicReceiver;
    private IntentFilter intentFilter;
    //自定义播放器
    private MusicMediaPlayer musicMediaPlayer;

    //底部布局组件
    private TextView textView_name;
    private ImageView imageView_playOrPause;
    private ImageView imageView_list;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initStateBar();
        initView();
        initReceiver();
        getPermission();

    }

    /**
     * 创建文件夹（根目录下新建文件夹CloudMusic及其子文件夹mp3及jpg）
     */
    private void createDirectory() {
        File sd=Environment.getExternalStorageDirectory();
        //创建总文件夹
        String path_sum=sd.getPath()+"/CloudMusic";
        File file_sum=new File(path_sum);
        if(!file_sum.exists())
            file_sum.mkdir();
        //创建歌曲文件夹
        String path_song=sd.getPath()+"/CloudMusic/mp3";
        File file_song=new File(path_song);
        if(!file_song.exists())
            file_song.mkdir();
        //创建封面文件夹
        String path_cover=sd.getPath()+"/CloudMusic/jpg";
        File file_cover=new File(path_cover);
        if(!file_cover.exists())
            file_cover.mkdir();
    }


    /**
     * 获取读写权限
     */
    private void getPermission() {
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.WRITE_APN_SETTINGS},502);
        }else {
            createDirectory();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 502:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    createDirectory();
                }else {
                    Toast.makeText(this,"用户拒绝了权限申请",Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    /**
     * 初始化组件
     */
    private void initView() {
        musicMediaPlayer = new MusicMediaPlayer(this);
        imageView_search = findViewById(R.id.imageView_search);
        imageView_search.setOnClickListener(this);

        //底部组件
        textView_name = findViewById(R.id.textView_name);
        textView_name.setOnClickListener(this);
        imageView_playOrPause = findViewById(R.id.imageView_playOrPause);
        imageView_playOrPause.setOnClickListener(this);
        imageView_list = findViewById(R.id.imageView_list);
        imageView_list.setOnClickListener(this);
    }

    /**
     * 初始化本地广播接收器
     */
    private void initReceiver() {
        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        intentFilter = new IntentFilter();
        intentFilter.addAction("com.dongxun.lichunkai.cloudmusic.MUSIC_BROADCAST");
        musicReceiver = new MusicReceiver();
        localBroadcastManager.registerReceiver(musicReceiver,intentFilter);
    }

    /**
     * 初始化状态栏
     */
    private void initStateBar() {
        ImmersionBar.with(this).init();
        getSupportActionBar().hide();
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.imageView_search:
                Intent intent_search = new Intent(MainActivity.this,SearchActivity.class);
                startActivity(intent_search);
                break;
            case R.id.textView_name:
                Intent intent_play = new Intent(MainActivity.this,PlayActivity.class);
                startActivity(intent_play);
                break;
            case R.id.imageView_playOrPause:
                Toast.makeText(this,"暂停/播放",Toast.LENGTH_SHORT).show();
                break;
            case R.id.imageView_list:
                Toast.makeText(this,"歌单",Toast.LENGTH_SHORT).show();
                break;
        }

    }

    /**
     * 获取歌曲URL
     */
    public void getSongUrl(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    OkHttpClient client = new OkHttpClient();//新建一个OKHttp的对象
                    //和风请求方式
                    Request request = new Request.Builder()
                            .url("https://api.imjad.cn/cloudmusic/?type=song&id="+ Common.song_playing.getId() +"")
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
                                //解析JSON
                                JSONObject object = new JSONObject(responseData);
                                String code = object.getString("code");
                                if (code.equals("200")){
                                    JSONArray data = object.getJSONArray("data");
                                    String downloadUrl = data.getJSONObject(0).getString("url");
                                    //下载歌曲
                                    if (downloadUrl.equals("")){
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(MainActivity.this,"这首歌需要会员，暂时无法收听...",Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }else {
                                        download(Common.song_playing.getId(),downloadUrl);
                                    }
                                }else {
                                    //错误
                                }
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
     * 下载具体操作
     * @param downloadUrl   下载的文件地址
     * @return
     */
    private void download(String songID, final String downloadUrl) {
        try {
            //创建文件夹
            String dirName = "";
            //下载后的文件名
            //设置下载位置和名称
            dirName = Environment.getExternalStorageDirectory() + "/CloudMusic/mp3/";
            final String fileName = dirName + songID + ".mp3";

            File file1 = new File(fileName);

            if (file1.exists()) {
                //文件存在
                Log.e("DOWLOAD", "文件已存在！");
            }else {
                URL url = new URL(downloadUrl);
                //打开连接
                URLConnection conn = url.openConnection();
                //打开输入流
                InputStream is = conn.getInputStream();
                //获得长度
                int contentLength = conn.getContentLength();
                Log.e("DOWLOAD", "文件长度 = " + contentLength);
                //创建字节流
                byte[] bs = new byte[1024];
                int len;
                OutputStream os = new FileOutputStream(fileName);
                //写数据
                while ((len = is.read(bs)) != -1) {
                    os.write(bs, 0, len);
                }
                //完成后关闭流
                Log.e("DOWLOAD", "文件不存在,下载成功！");
                os.close();
                is.close();
            }
            //播放
            musicMediaPlayer.initMediaPlayer(this);
            musicMediaPlayer.startOption();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    class MusicReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getStringExtra("ACTION");
            Toast.makeText(context,"收到广播：" + action,Toast.LENGTH_SHORT).show();
            switch (action){
                case "PLAYNEW":
                    //停止当前播放
                    musicMediaPlayer.stopOption();
                    //获取歌曲信息并下载播放
                    getSongUrl();
                    break;
                case "PLAY_PAUSE":
                    //暂停/播放
                    break;
                case "LAST":
                    //上一曲
                    break;
                case "NEXT":
                    //下一曲
                    break;
                case "LIKE":
                    //收藏
                    break;
                case "LOOP":
                    //循环
                    break;
                case "COMMENTS":
                    //评论
                    break;
                case "LIST":
                    //歌曲列表
                    break;
            }
        }
    }

    /**
     * 退出应用停止播放
     */
    @Override
    protected void onDestroy() {
        //退出播放器
        musicMediaPlayer.exitOption();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}
