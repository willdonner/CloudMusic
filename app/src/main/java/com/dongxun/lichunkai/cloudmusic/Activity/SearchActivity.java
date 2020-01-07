package com.dongxun.lichunkai.cloudmusic.Activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dongxun.lichunkai.cloudmusic.Adapter.SearcHistoryAdapter;
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
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 歌曲搜索页
 */
public class SearchActivity extends AppCompatActivity implements View.OnClickListener {


    private static final String TAG = "SearchActivity";
    private ArrayList<Song> songList = new ArrayList<>();//搜索结果
    private ArrayList<String> searchHistory = new ArrayList<>();//搜索历史

    private EditText editText_search;
    private TextView textView_cancel;
    private RecyclerView recyclerView_result;
    private ImageView imageView_deleteHistory;
    private RecyclerView recyclerView_history;
    private LinearLayout LinearLayout_history;
    private ImageView imageView_waiting;

    private SearchAdapter searchAdapter;
    private LinearLayoutManager layoutManager;

    private SearcHistoryAdapter searcHistoryAdapter;
    private StaggeredGridLayoutManager layoutManager_history;

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
        /**
         * 搜索历史Adapter
         */
        searchHistory = getSearchHistory();
        layoutManager_history = new StaggeredGridLayoutManager(1,StaggeredGridLayoutManager.HORIZONTAL);//瀑布流
        recyclerView_history.setLayoutManager(layoutManager_history);
        searcHistoryAdapter = new SearcHistoryAdapter(searchHistory);
        recyclerView_history.setAdapter(searcHistoryAdapter);
        //点击事件(实现自定义的点击事件接口)
        searcHistoryAdapter.setOnItemClickListener(new SearcHistoryAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position) {
                //搜索选取历史
                editText_search.setText(searchHistory.get(position));
                editText_search.clearFocus();
                //搜索
                search(editText_search.getText().toString());
                //关闭输入法
                InputMethodManager m=(InputMethodManager) getSystemService(getBaseContext().INPUT_METHOD_SERVICE);
                m.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                //存储搜索历史
                saveSearchHistory(editText_search.getText().toString());
                //隐藏历史布局
                LinearLayout_history.setVisibility(View.GONE);
            }
        });
        //没有搜索历史隐藏布局
        if (searchHistory.size() == 0) LinearLayout_history.setVisibility(View.GONE);

        /**
         * 搜索结果Adapter
         */
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
     * 读取搜索历史
     */
    private ArrayList<String> getSearchHistory() {
        ArrayList<String> history = new ArrayList<>();//搜索历史
        SharedPreferences preferences = getSharedPreferences("data",MODE_PRIVATE);
        String str_searchHistory = preferences.getString("searchHistory","");
        if (str_searchHistory.length() > 0){
            if (str_searchHistory.trim().charAt(0) == ',')  str_searchHistory = str_searchHistory.substring(1).trim();
            for (String str:str_searchHistory.split(",")){
                history.add(str);
            }
        }
        //倒叙
        Collections.reverse(history);
        return history;
    }

    /**
     * 保存搜索历史
     * @param keyword
     */
    private void saveSearchHistory(String keyword) {
        String str_searchHistory = "";
        Collections.reverse(searchHistory);
        if (searchHistory.contains(keyword)) {
            //搜索历史包含该关键词，放至最前
            int index = searchHistory.indexOf(keyword);
            searchHistory.remove(keyword);
        }
        for (String his:searchHistory){
            if (!his.trim().equals("")) str_searchHistory = str_searchHistory + "," + his;
        }
        str_searchHistory = str_searchHistory + "," + keyword;

        SharedPreferences.Editor editor = getSharedPreferences("data",MODE_PRIVATE).edit();
        editor.putString("searchHistory",str_searchHistory);
        editor.apply();
    }

    /**
     * 删除搜索历史
     */
    private void deleteSearchHistory() {
        if (getSearchHistory().size() == 0) return;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("提示");
        builder.setMessage("确认删除搜索历史？");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                searchHistory.remove(searchHistory);
                SharedPreferences.Editor editor = getSharedPreferences("data",MODE_PRIVATE).edit();
                editor.putString("searchHistory","");
                editor.apply();
                //刷新UI
                LinearLayout_history.setVisibility(View.GONE);
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        builder.show();
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
        editText_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                //文本为空显示搜索历史
                if (editText_search.getText().toString().length() == 0){
                    recyclerView_result.setVisibility(View.GONE);
                    LinearLayout_history.setVisibility(View.VISIBLE);
                    searchHistory.removeAll(searchHistory);
                    searchHistory.addAll(getSearchHistory());
                    searcHistoryAdapter.notifyDataSetChanged();
                }
            }
        });
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
                        //存储搜索历史
                        saveSearchHistory(keyword);
                        //隐藏历史布局
                        LinearLayout_history.setVisibility(View.GONE);
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
        imageView_deleteHistory = findViewById(R.id.imageView_deleteHistory);
        imageView_deleteHistory.setOnClickListener(this);
        recyclerView_history = findViewById(R.id.recyclerView_history);
        LinearLayout_history = findViewById(R.id.LinearLayout_history);
        imageView_waiting = findViewById(R.id.imageView_waiting);
        imageView_waiting.setVisibility(View.GONE);
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
        imageView_waiting.setVisibility(View.VISIBLE);
        recyclerView_result.setVisibility(View.GONE);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    OkHttpClient client = new OkHttpClient();//新建一个OKHttp的对象
                    //和风请求方式
                    Request request = new Request.Builder()
                            .url("https://neteasecloudmusicapi.willdonner.top/search?keywords="+ keyWord +"")
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
                String albumid = songInfo.getJSONObject("album").getString("id");
                String artist = songInfo.getJSONArray("artists").getJSONObject(0).getString("name");
                String coverURL = songInfo.getJSONArray("artists").getJSONObject(0).getString("img1v1Url");

                Song song = new Song();
                song.setId(id);
                song.setalbumId(albumid);
                song.setName(name);
                song.setArtist(artist);
                song.setCoverURL(coverURL);
                songList.add(song);
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    imageView_waiting.setVisibility(View.GONE);
                    recyclerView_result.setVisibility(View.VISIBLE);
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
            case R.id.imageView_deleteHistory:
                //删除搜索历史
                deleteSearchHistory();
                break;

        }
    }

}
