package com.dongxun.lichunkai.cloudmusic.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dongxun.lichunkai.cloudmusic.Adapter.SongSheetAdapter;
import com.dongxun.lichunkai.cloudmusic.Bean.Song;
import com.dongxun.lichunkai.cloudmusic.Bean.SongSheet;
import com.dongxun.lichunkai.cloudmusic.Class.CircleImageView;
import com.dongxun.lichunkai.cloudmusic.Class.RoundImageView;
import com.dongxun.lichunkai.cloudmusic.Common.Common;
import com.dongxun.lichunkai.cloudmusic.LocalBroadcast.SendLocalBroadcast;
import com.dongxun.lichunkai.cloudmusic.R;
import com.dongxun.lichunkai.cloudmusic.Util.ToolHelper;
import com.gyf.immersionbar.ImmersionBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import pl.droidsonroids.gif.GifImageView;

public class SongSheetActivity extends AppCompatActivity implements View.OnClickListener {

    private String TAG = "SongSheetActivity";

    private TextView textView_copywriter;
    private TextView textView_name;
    private TextView textView_playCount;
    private RecyclerView recyclerView;
    private GifImageView gifImageView_loading;
    private RoundImageView roundImageView_cover;
    private ImageView imageView_back;
    private ImageView ResizableImageView_background;
    private TextView textView_creatorNickName;
    private TextView textView_description;
    private TextView textview_commentsCount;
    private TextView textview_shareCount;
    private CircleImageView circleImageView_head;
    private RelativeLayout RelativeLayout_songSheet;
    private LinearLayout LinearLayout_creator;

    private ArrayList<Song> songs = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private SongSheetAdapter songSheetAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_sheet);

        initStateBar();
        initView();
        setAdapter();
        getSongSheet();
    }

    private void setAdapter() {
        linearLayoutManager = new LinearLayoutManager(this);
        songSheetAdapter = new SongSheetAdapter(songs);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(songSheetAdapter);
        songSheetAdapter.setOnItemClickListener(new SongSheetAdapter.OnItemClickListener() {
            @Override
            public void onClickPlay(int position) {
                //播放
                ToolHelper.showToast(SongSheetActivity.this,"播放"+songs.get(position).getId());
                //更改公共变量
                Common.song_playing = songs.get(position);
                //发送本地广播播放
                SendLocalBroadcast.playNew(SongSheetActivity.this);
                //跳转(带参数，说明是播放新歌曲)
                Intent intent = new Intent(SongSheetActivity.this,PlayActivity.class);
                startActivity(intent);
            }

            @Override
            public void onClickMenu(int position) {
                //菜单
                ToolHelper.showToast(SongSheetActivity.this,"菜单");
            }
        });
    }

    private void getSongSheet() {
        final SongSheet songSheet = (SongSheet)getIntent().getSerializableExtra("songsheet");
        textView_copywriter.setText(songSheet.getCopywriter());
        textView_name.setText(songSheet.getName());
        textView_playCount.setText((songSheet.getPlayCount().length()>5)?(int)Integer.parseInt(songSheet.getPlayCount())/10000+"万":songSheet.getPlayCount());//超过万改变单位
        textView_playCount.setText((songSheet.getPlayCount().length()>8)?(int)Integer.parseInt(songSheet.getPlayCount())/100000000+"亿":(int)Integer.parseInt(songSheet.getPlayCount())/10000+"万");//超过亿改变单位
        //获取封面
        new Thread(new Runnable() {
            @Override
            public void run() {
                URL imageurl = null;
                try {
                    imageurl = new URL(songSheet.getPicUrl());
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                try {
                    HttpURLConnection conn = (HttpURLConnection)imageurl.openConnection();
                    conn.setDoInput(true);
                    conn.connect();
                    InputStream is = conn.getInputStream();
                    final Bitmap bitmap = BitmapFactory.decodeStream(is);
                    //切换主线程更新UI
                    roundImageView_cover.post(new Runnable() {
                        @Override
                        public void run() {
                            roundImageView_cover.setImageBitmap(bitmap);
                            ResizableImageView_background.setImageBitmap(bitmap);
                        }
                    });
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        getDetails(songSheet.getId());
    }

    private void initView() {
         gifImageView_loading = findViewById(R.id.gifImageView_loading);
        textView_copywriter = findViewById(R.id.textView_copywriter);
        textView_name = findViewById(R.id.textView_name);
        textView_playCount = findViewById(R.id.textView_playCount);
        recyclerView = findViewById(R.id.recyclerView);
        roundImageView_cover = findViewById(R.id.roundImageView_cover);
        imageView_back = findViewById(R.id.imageView_back);
        imageView_back.setOnClickListener(this);
        ResizableImageView_background = findViewById(R.id.ResizableImageView_background);
        textView_creatorNickName = findViewById(R.id.textView_creatorNickName);
        textView_description = findViewById(R.id.textView_description);
        textView_description.setOnClickListener(this);
        textview_commentsCount = findViewById(R.id.textview_commentsCount);
        textview_shareCount = findViewById(R.id.textview_shareCount);
        circleImageView_head = findViewById(R.id.circleImageView_head);
        RelativeLayout_songSheet = findViewById(R.id.RelativeLayout_songSheet);
        RelativeLayout_songSheet.setOnClickListener(this);
        LinearLayout_creator = findViewById(R.id.LinearLayout_creator);
        LinearLayout_creator.setOnClickListener(this);
    }

    /**
     * 初始化状态栏
     */
    private void initStateBar() {
        ImmersionBar.with(this).init();
        getSupportActionBar().hide();
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
    }

    /**
     * 获取歌单详情
     */
    private void getDetails(final String id) {
        Log.e(TAG, "getDetails: "+id);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    OkHttpClient client = new OkHttpClient();//新建一个OKHttp的对象
                    Request request = new Request.Builder()
                            .url("http://www.willdonner.top:3000/playlist/detail?id="+ id +"")
                            .build();
                    Call call = client.newCall(request);
                    call.enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                        }
                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            final String responseData = response.body().string();//处理返回的数据
                            Log.e(TAG, "onResponse: "+responseData);
                            //处理JSON（私人定制情况需要单独处理）
                            try {
                                JSONObject newRes = new JSONObject(responseData);
                                String code = newRes.getString("code");
                                if (code.equals("200")){
                                    Log.e(TAG, "歌单详情获取成功");
                                    //解析歌曲
                                    JSONArray tracks = newRes.getJSONObject("playlist").getJSONArray("tracks");
                                    for (int i=0;i<tracks.length();i++){
                                        String name = tracks.getJSONObject(i).getString("name");//歌曲名称
                                        String id = tracks.getJSONObject(i).getString("id");//歌曲id
                                        String artist = "";//艺术家
                                        JSONArray ars = tracks.getJSONObject(i).getJSONArray("ar");
                                        for(int j=0;j<ars.length();j++){
                                            String ar = ars.getJSONObject(j).getString("name");
                                            artist = artist.equals("")?ar:artist + "/" + ar;
                                        }
                                        String al_name = tracks.getJSONObject(i).getJSONObject("al").getString("name");//专辑
                                        String al_id = tracks.getJSONObject(i).getJSONObject("al").getString("id");//专辑
                                        Log.e(TAG, "onResponse: "+name+artist+al_name);

                                        Song song = new Song();
                                        song.setId(id);
                                        song.setName(name);
                                        song.setArtist(artist);
                                        song.setAlbumName(al_name);
                                        song.setAlbumId(al_id);
                                        songs.add(song);
                                    }
                                    //解析歌单基本信息
                                    final String commentCount = newRes.getJSONObject("playlist").getString("commentCount");//评论数量
                                    final String shareCount = newRes.getJSONObject("playlist").getString("shareCount");//分享数量
                                    final String description = newRes.getJSONObject("playlist").getString("description");//歌单描述
                                    String userId = newRes.getJSONObject("playlist").getString("userId");//作者id
                                    String commentThreadId = newRes.getJSONObject("playlist").getString("commentThreadId");//评论随机码
                                    String trackCount = newRes.getJSONObject("playlist").getString("trackCount");//歌曲总数量
                                    String coverImgUrl = newRes.getJSONObject("playlist").getString("coverImgUrl");//封面图
                                    final String creator_nickname = newRes.getJSONObject("playlist").getJSONObject("creator").getString("nickname");//作者昵称
                                    final String creator_avatarUrl = newRes.getJSONObject("playlist").getJSONObject("creator").getString("avatarUrl");//作者头像
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            //作者名
                                            textView_creatorNickName.setText(creator_nickname);
                                            //介绍
                                            textView_description.setText(description);
                                            //评论数
                                            textview_commentsCount.setText(commentCount);
                                            //分享数
                                            textview_shareCount.setText(shareCount);
                                            //头像
                                            new Thread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    URL imageurl = null;
                                                    try {
                                                        imageurl = new URL(creator_avatarUrl);
                                                    } catch (MalformedURLException e) {
                                                        e.printStackTrace();
                                                    }
                                                    try {
                                                        HttpURLConnection conn = (HttpURLConnection)imageurl.openConnection();
                                                        conn.setDoInput(true);
                                                        conn.connect();
                                                        InputStream is = conn.getInputStream();
                                                        final Bitmap bitmap = BitmapFactory.decodeStream(is);
                                                        //切换主线程更新UI
                                                        roundImageView_cover.post(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                circleImageView_head.setImageBitmap(bitmap);
                                                            }
                                                        });
                                                        is.close();
                                                    } catch (IOException e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                            }).start();
                                            songSheetAdapter.notifyDataSetChanged();
                                            gifImageView_loading.setVisibility(View.GONE);
                                        }
                                    });
                                }else {
                                    Log.e(TAG, "歌单详情获取失败");
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

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.imageView_back:
                finish();
                break;
            case R.id.RelativeLayout_songSheet: case R.id.textView_description:
                //显示歌单介绍
                ToolHelper.showToast(this,"歌单介绍");
                break;
            case R.id.LinearLayout_creator:
                //作者
                ToolHelper.showToast(this,"作者主页");
                break;
        }
    }
}
