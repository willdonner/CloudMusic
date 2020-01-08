package com.dongxun.lichunkai.cloudmusic.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.dongxun.lichunkai.cloudmusic.Adapter.SongSheetAdapter;
import com.dongxun.lichunkai.cloudmusic.Bean.Song;
import com.dongxun.lichunkai.cloudmusic.Bean.SongSheet;
import com.dongxun.lichunkai.cloudmusic.Common.Common;
import com.dongxun.lichunkai.cloudmusic.LocalBroadcast.SendLocalBroadcast;
import com.dongxun.lichunkai.cloudmusic.R;
import com.dongxun.lichunkai.cloudmusic.Util.ToolHelper;
import com.gyf.immersionbar.ImmersionBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import pl.droidsonroids.gif.GifImageView;

public class SongSheetActivity extends AppCompatActivity {

    private String TAG = "SongSheetActivity";

    private TextView textView_copywriter;
    private TextView textView_name;
    private TextView textView_playCount;
    private RecyclerView recyclerView;
    private GifImageView gifImageView_loading;

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
        SongSheet songSheet = (SongSheet)getIntent().getSerializableExtra("songsheet");
        textView_copywriter.setText(songSheet.getCopywriter());
        textView_name.setText(songSheet.getName());
        textView_playCount.setText(songSheet.getPlayCount());

        getDetails(songSheet.getId());
    }

    private void initView() {
         gifImageView_loading = findViewById(R.id.gifImageView_loading);
        textView_copywriter = findViewById(R.id.textView_copywriter);
        textView_name = findViewById(R.id.textView_name);
        textView_playCount = findViewById(R.id.textView_playCount);
        recyclerView = findViewById(R.id.recyclerView);
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
                            //处理JSON
                            try {
                                JSONObject newRes = new JSONObject(responseData);
                                String code = newRes.getString("code");
                                if (code.equals("200")){
                                    Log.e(TAG, "歌单详情获取成功");
                                    JSONArray tracks = newRes.getJSONObject("playlist").getJSONArray("tracks");
                                    for (int i=0;i<tracks.length();i++){
                                        String name = tracks.getJSONObject(i).getString("name");//歌曲名称
                                        String id = tracks.getJSONObject(i).getString("id");//歌曲id
                                        String artist = "";//艺术家
                                        JSONArray ars = tracks.getJSONObject(i).getJSONArray("ar");
                                        for(int j=0;j<ars.length();j++){
                                            String ar = ars.getJSONObject(j).getString("name");
                                            artist = ar.equals("")?ar:artist + "/" + ar;
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
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
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
}
