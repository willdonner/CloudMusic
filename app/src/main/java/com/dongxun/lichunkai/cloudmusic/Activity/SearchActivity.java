package com.dongxun.lichunkai.cloudmusic.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.dongxun.lichunkai.cloudmusic.Adapter.SearchAdapter;
import com.dongxun.lichunkai.cloudmusic.Bean.Song;
import com.dongxun.lichunkai.cloudmusic.Common.Common;
import com.dongxun.lichunkai.cloudmusic.LocalBroadcast.SendLocalBroadcast;
import com.dongxun.lichunkai.cloudmusic.R;
import com.gyf.immersionbar.ImmersionBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SearchActivity extends AppCompatActivity implements View.OnClickListener {


    private static final String TAG = "SearchActivity";
    private List<Song> songList = new ArrayList<>();//搜索结果

    private EditText editText_search;
    private TextView textView_cancel;
    private RecyclerView recyclerView_result;

    private SearchAdapter searchAdapter;
    private LinearLayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        initStateBar();
        initView();
        showInput(editText_search);
        initAdapter();
    }

    /**
     * 初始化适配器
     */
    private void initAdapter() {
        layoutManager = new LinearLayoutManager(this);
        recyclerView_result.setLayoutManager(layoutManager);
        searchAdapter = new SearchAdapter(songList);
        recyclerView_result.setAdapter(searchAdapter);
        searchAdapter.setOnItemClickListener(new SearchAdapter.OnItemClickListener() {
            @Override
            public void onClickPlay(int position) {
                //更改公共变量
                Common.song_playing = songList.get(position);
                //发送本地广播播放
                SendLocalBroadcast.playNew(SearchActivity.this);
                //跳转(带参数，说明是播放新歌曲)
                Intent intent = new Intent(SearchActivity.this,PlayActivity.class);
                startActivity(intent);
            }

            @Override
            public void onClickMenu(int position) {
                //显示菜单
                Toast.makeText(SearchActivity.this,"菜单",Toast.LENGTH_SHORT).show();
            }
        });
    }


    /**
     * 显示键盘
     * @param et 输入焦点
     */
    public void showInput(final EditText et) {
        et.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.showSoftInput(et, InputMethodManager.SHOW_IMPLICIT);
    }

    /**
     * 初始化组件
     */
    private void initView() {
        //监听输入法点击搜索键，搜索歌曲
        editText_search = findViewById(R.id.editText_search);
        editText_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    String keyword = v.getText().toString().trim();
                    if (keyword.length() != 0){
                        //搜索
                        search(keyword);
                        //关闭输入法
                        InputMethodManager m=(InputMethodManager) getSystemService(getBaseContext().INPUT_METHOD_SERVICE);
                        m.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                    }else {
                        //显示其余组件
                    }
                    return true;
                }
                return false;
            }
        });
        textView_cancel = findViewById(R.id.textView_cancel);
        textView_cancel.setOnClickListener(this);
        recyclerView_result = findViewById(R.id.recyclerView_result);
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
     * 搜索歌曲
     * @param keyWord 关键词
     */
    private void search(final String keyWord) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    OkHttpClient client = new OkHttpClient();//新建一个OKHttp的对象
                    //和风请求方式
                    Request request = new Request.Builder()
                            .url("https://api.imjad.cn/cloudmusic/?type=search&s="+ keyWord +"")
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
            JSONArray songs = response.getJSONObject("result").getJSONArray("songs");
            songList.removeAll(songList);
            for (int i=0;i<songs.length();i++){
                JSONObject songInfo = songs.getJSONObject(i);
                String name = songInfo.getString("name");
                String id = songInfo.getString("id");
                String artist = songInfo.getJSONArray("ar").getJSONObject(0).getString("name");
                String coverURL = songInfo.getJSONObject("al").getString("picUrl");

                Song song = new Song();
                song.setId(id);
                song.setName(name);
                song.setArtist(artist);
                song.setCoverURL(coverURL);
                songList.add(song);
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //回到顶部并应用更改
                    layoutManager.scrollToPosition(0);
                    layoutManager.setStackFromEnd(true);
                    searchAdapter.notifyDataSetChanged();
                }
            });
        }else {
            //显示错误信息
        }
    }

    /**
     * 点击事件
     * @param view
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.textView_cancel:
                finish();
                break;

        }
    }
}
