package com.dongxun.lichunkai.cloudmusic.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dongxun.lichunkai.cloudmusic.Adapter.DailyRecommendAdapter;
import com.dongxun.lichunkai.cloudmusic.Bean.Song;
import com.dongxun.lichunkai.cloudmusic.Class.BaseActivity;
import com.dongxun.lichunkai.cloudmusic.Common.Common;
import com.dongxun.lichunkai.cloudmusic.LocalBroadcast.SendLocalBroadcast;
import com.dongxun.lichunkai.cloudmusic.R;
import com.dongxun.lichunkai.cloudmusic.Util.BitMap;
import com.dongxun.lichunkai.cloudmusic.Util.ToolHelper;
import com.gyf.immersionbar.ImmersionBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Date;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.internal.Util;

import static com.dongxun.lichunkai.cloudmusic.Util.ToolHelper.openImage;
import static com.dongxun.lichunkai.cloudmusic.Util.ToolHelper.saveAccount;
import static com.dongxun.lichunkai.cloudmusic.Util.ToolHelper.showToast;

/**
 * 每日推荐页
 */
public class DailyRecommendActivity extends BaseActivity implements View.OnClickListener {

    private String TAG = "DailyRecommendActivity";

    private ImageView imageView_back;
    private TextView textView_day;
    private TextView textView_month;
    private LinearLayout LinearLayout_playAll;
    private LinearLayout LinearLayout_selects;
    private RecyclerView recyclerView;
    private ImageView imageView_loading;

    private ArrayList<Song> songs = new ArrayList<>();//每日推荐歌曲列表
    private LinearLayoutManager linearLayoutManager;
    private DailyRecommendAdapter dailyRecommendAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_recommend);

        initStateBar();
        initView();
        setAdapter();
        getData();
    }


    private void setAdapter() {
        linearLayoutManager = new LinearLayoutManager(this);
        dailyRecommendAdapter = new DailyRecommendAdapter(songs);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(dailyRecommendAdapter);
        dailyRecommendAdapter.setOnItemClickListener(new DailyRecommendAdapter.OnItemClickListener() {
            @Override
            public void onClickPlay(int position) {
                //播放
                showToast(DailyRecommendActivity.this,"播放");
                //更改公共变量
                Common.song_playing = songs.get(position);
                //发送本地广播播放
                SendLocalBroadcast.playNew(DailyRecommendActivity.this);
                //跳转(带参数，说明是播放新歌曲)
                Intent intent = new Intent(DailyRecommendActivity.this,PlayActivity.class);
                startActivity(intent);
            }

            @Override
            public void onClickMenu(int position) {
                //菜单
                showToast(DailyRecommendActivity.this,"菜单");
            }
        });
    }

    /**
     * 获取页面数据
     */
    private void getData() {
        //获取日期
        Date date = new Date();
        String month = new SimpleDateFormat("MM").format(date);
        String day = new SimpleDateFormat("dd").format(date);
        textView_day.setText(day);
        textView_month.setText(month);
        getRecommend();
    }

    /**
     * 获取recommend
     */
    private void getRecommend() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    final Request request = new Request.Builder()
                            .url("http://www.willdonner.top:3000/recommend/songs")
                            .build();
                    Call call = Common.mOkHttpClient.newCall(request);
                    call.enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {

                        }
                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            final String responseData = response.body().string();//处理返回的数据
                            Log.e(TAG, "onResponse: "+responseData);
                            //处理JSON
                            try {
                                JSONObject newResponse = new JSONObject(responseData);
                                String code = newResponse.getString("code");
                                if (code.equals("200")){
                                    Log.e(TAG, "onResponse: 每日推荐歌曲获取成功");
                                    JSONArray recommend = newResponse.getJSONArray("recommend");
                                    for (int i=0;i<recommend.length();i++){
                                        JSONObject song = recommend.getJSONObject(i);
                                        String name = song.getString("name");//歌曲名
                                        String id = song.getString("id");//歌曲id
                                        String reason = song.getString("reason");//推荐原因

                                        final Song song1 = new Song();
                                        song1.setName(name);
                                        song1.setId(id);

                                        JSONArray artists = song.getJSONArray("artists");
                                        String str_artists = "";
                                        for (int j=0;j<artists.length();j++){
                                            JSONObject artist = artists.getJSONObject(j);
                                            String artist_name = artist.getString("name");//歌手名
                                            String artist_id = artist.getString("id");//歌手id
                                            str_artists = str_artists.length()==0?artist_name:str_artists+"/"+artist_name;
                                        }
                                        song1.setArtist(str_artists);

                                        JSONObject album = song.getJSONObject("album");
                                        String album_name = album.getString("name");//专辑名称
                                        String album_id = album.getString("id");//专辑id
                                        final String album_blurPicUrl = album.getString("blurPicUrl");//专辑封面
                                        String album_publishTime = album.getString("publishTime");//出版时间

                                        song1.setAlbumId(album_id);
                                        song1.setAlbumName(album_name);
                                        song1.setCoverURL(album_blurPicUrl);

                                        songs.add(song1);
                                    }
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            dailyRecommendAdapter.notifyDataSetChanged();
                                            imageView_loading.setVisibility(View.GONE);
                                        }
                                    });
                                }else {
                                    Log.e(TAG, "onResponse: 每日推荐歌曲获取失败");
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
     * 初始化组件
     */
    private void initView() {
        imageView_back = findViewById(R.id.imageView_back);
        imageView_back.setOnClickListener(this);
        textView_day = findViewById(R.id.textView_day);
        textView_month = findViewById(R.id.textView_month);
        LinearLayout_playAll = findViewById(R.id.LinearLayout_playAll);
        LinearLayout_playAll.setOnClickListener(this);
        LinearLayout_selects = findViewById(R.id.LinearLayout_selects);
        LinearLayout_selects.setOnClickListener(this);
        recyclerView = findViewById(R.id.recyclerView);
        imageView_loading = findViewById(R.id.imageView_loading);
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
     * 点击事件
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.imageView_back:
                finish();
                break;
            case R.id.LinearLayout_playAll:
                showToast(this,"播放全部");
                break;
            case R.id.LinearLayout_selects:
                showToast(this,"多选");
                break;
        }
    }
}
