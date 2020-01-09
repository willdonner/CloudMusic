package com.dongxun.lichunkai.cloudmusic.Activity;

import androidx.activity.ComponentActivity;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.dongxun.lichunkai.cloudmusic.Adapter.CommentAdapter;
import com.dongxun.lichunkai.cloudmusic.Bean.Comment;
import com.dongxun.lichunkai.cloudmusic.Bean.Song;
import com.dongxun.lichunkai.cloudmusic.Bean.User;
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

import static com.dongxun.lichunkai.cloudmusic.Util.ToolHelper.showToast;

/**
 * 评论页
 */
public class CommentActivity extends AppCompatActivity implements View.OnClickListener {

    private String TAG = "CommentActivity";

    private RecyclerView recyclerView_comment;
    private RecyclerView recyclerView_newComment;
    private TextView textView_allHotComment;
    private ImageView imageView_back;
    private TextView textView_title;
    private TextView textView_title_hot;
    private TextView textView_title_new;
    private TextView textView_total;

    private LinearLayoutManager linearLayoutManager;
    private CommentAdapter commentAdapter_hot;
    private LinearLayoutManager linearLayoutManager_new;
    private CommentAdapter commentAdapter;
    private ArrayList<Comment> commentList = new ArrayList<>();

    private Boolean HotModel = false;//精彩评论模式

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        initStateBar();
        initView();
        setAdapter();
        getdata();
    }

    private void initView() {
        textView_total = findViewById(R.id.textView_total);
        textView_title = findViewById(R.id.textView_title);
        recyclerView_comment = findViewById(R.id.recyclerView_comment);
        imageView_back = findViewById(R.id.imageView_back);
        imageView_back.setOnClickListener(this);
    }

    private void setAdapter() {
        //热门评论Adapter
        linearLayoutManager = new LinearLayoutManager(this);
        commentAdapter = new CommentAdapter(commentList);
        recyclerView_comment.setLayoutManager(linearLayoutManager);
        recyclerView_comment.setAdapter(commentAdapter);
        commentAdapter.setOnItemClickListener(new CommentAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position) {
                //点击评论内容，弹出操作
                showToast(CommentActivity.this,"弹出操作");
            }

            @Override
            public void onClickLike(int position) {
                //点赞
                showToast(CommentActivity.this,"点赞");

            }

            @Override
            public void onClickReturn(int position) {
                //查看回复
                showToast(CommentActivity.this,"查看回复");
            }

            @Override
            public void onClickAllHot(int position) {
                //所有热评
                showToast(CommentActivity.this,"所有热评");
                Intent intent = new Intent(CommentActivity.this,CommentActivity.class);
                intent.putExtra("id","186016");
                intent.putExtra("type",0);
                intent.putExtra("limit",20);
                intent.putExtra("hotModel",true);
                startActivity(intent);
            }
        });
    }

    private void getdata() {
        //获取参数（id,type,limit）
        String id = getIntent().getStringExtra("id");
        int type = getIntent().getIntExtra("type",0);
        int limit = getIntent().getIntExtra("limit",10);
        HotModel = getIntent().getBooleanExtra("hotModel",false);
        Log.e(TAG, "getdata: "+id);
        Log.e(TAG, "getdata: "+type);
        Log.e(TAG, "getdata: "+limit);
        Log.e(TAG, "getdata: "+HotModel);

        //热评模式
        if (HotModel){
            textView_title.setText("精彩评论(");
            getHotComment(id,type,limit);
        }
        //获取评论（根据资源类型使用对应方法）
        if (!HotModel){
            switch (type){
                case 0:
                    //歌曲评论
                    getMusicComment(id,limit);
                    break;
            }
        }
    }

    /**
     * 获取歌曲评论(包括15条热门评论和规定条最新评论)
     * @param id
     * @param limit
     */
    private void getMusicComment(final String id, final int limit) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    OkHttpClient client = new OkHttpClient();//新建一个OKHttp的对象
                    Request request = new Request.Builder()
                            .url("https://neteasecloudmusicapi.willdonner.top/comment/music?id="+ id +"&limit="+ limit +"")
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
                                    Log.e(TAG, "歌曲评论获取成功");
                                    final String total = newRes.getString("total");
                                    //解析热门评论
                                    JSONArray hotComments = newRes.getJSONArray("hotComments");
                                    for (int i=0;i<hotComments.length();i++){
                                        JSONObject user = hotComments.getJSONObject(i).getJSONObject("user");

                                        User user1 = new User();
                                        user1.setUserId(user.getString("userId"));
                                        user1.setNickname(user.getString("nickname"));
                                        user1.setAvatarUrl(user.getString("avatarUrl"));

                                        String commentId = hotComments.getJSONObject(i).getString("commentId");
                                        String content = hotComments.getJSONObject(i).getString("content");
                                        String time = hotComments.getJSONObject(i).getString("time");
                                        String likedCount = hotComments.getJSONObject(i).getString("likedCount");

                                        Comment comment = new Comment();
                                        comment.setUser(user1);
                                        comment.setCommentId(commentId);
                                        comment.setContent(content);
                                        comment.setLikedCount(likedCount);
                                        comment.setTime(time);
                                        comment.setShowNew(false);
                                        if (i==hotComments.length()-1) {
                                            //最后一条热评
                                            comment.setShowHot(false);
                                            comment.setShowAllHot(true);
                                        }else if (i==0){
                                            //第一条热评
                                            comment.setShowHot(true);
                                            comment.setShowAllHot(false);
                                        }else {
                                            //中间热评
                                            comment.setShowHot(false);
                                            comment.setShowAllHot(false);
                                        }
                                        commentList.add(comment);
                                    }
                                    //解析最新评论
                                    JSONArray comments = newRes.getJSONArray("comments");
                                    for (int i=0;i<comments.length();i++){
                                        JSONObject user = comments.getJSONObject(i).getJSONObject("user");

                                        User user1 = new User();
                                        user1.setUserId(user.getString("userId"));
                                        user1.setNickname(user.getString("nickname"));
                                        user1.setAvatarUrl(user.getString("avatarUrl"));

                                        String commentId = comments.getJSONObject(i).getString("commentId");
                                        String content = comments.getJSONObject(i).getString("content");
                                        String time = comments.getJSONObject(i).getString("time");
                                        String likedCount = comments.getJSONObject(i).getString("likedCount");

                                        Comment comment = new Comment();
                                        comment.setUser(user1);
                                        comment.setCommentId(commentId);
                                        comment.setContent(content);
                                        comment.setLikedCount(likedCount);
                                        comment.setTime(time);
                                        comment.setShowHot(false);
                                        comment.setShowAllHot(false);
                                        if (i==0){
                                            //第一条评论
                                            comment.setShowNew(true);
                                        }else {
                                            comment.setShowNew(false);
                                        }
                                        commentList.add(comment);
                                    }
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            textView_total.setText(total);
                                            commentAdapter.notifyDataSetChanged();
                                        }
                                    });

                                }else {
                                    Log.e(TAG, "歌曲评论获取失败");
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
     * 获取热门评论
     * @param id 资源id
     * @param type 数字 , 资源类型 , 对应歌曲(0) , mv(1), 歌单(2) , 专辑(3) , 电台(4), 视频(5)
     * @param limit 取出评论数量 , 默认为 20
     */
    private void getHotComment(final String id, final int type, final int limit) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    OkHttpClient client = new OkHttpClient();//新建一个OKHttp的对象
                    Request request = new Request.Builder()
                            .url("https://neteasecloudmusicapi.willdonner.top/comment/hot?id="+ id +"&type="+ type +"&limit="+ limit +"")
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
                                    Log.e(TAG, "热门评论获取成功");
                                    final String total = newRes.getString("total");
                                    //解析热门评论
                                    JSONArray hotComments = newRes.getJSONArray("hotComments");
                                    for (int i=0;i<hotComments.length();i++){
                                        JSONObject user = hotComments.getJSONObject(i).getJSONObject("user");

                                        User user1 = new User();
                                        user1.setUserId(user.getString("userId"));
                                        user1.setNickname(user.getString("nickname"));
                                        user1.setAvatarUrl(user.getString("avatarUrl"));

                                        String commentId = hotComments.getJSONObject(i).getString("commentId");
                                        String content = hotComments.getJSONObject(i).getString("content");
                                        String time = hotComments.getJSONObject(i).getString("time");
                                        String likedCount = hotComments.getJSONObject(i).getString("likedCount");

                                        Comment comment = new Comment();
                                        comment.setUser(user1);
                                        comment.setCommentId(commentId);
                                        comment.setContent(content);
                                        comment.setLikedCount(likedCount);
                                        comment.setTime(time);
                                        comment.setShowNew(false);
                                        comment.setShowHot(false);
                                        comment.setShowAllHot(false);
                                        commentList.add(comment);
                                    }
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            textView_total.setText(total);
                                            commentAdapter.notifyDataSetChanged();
                                        }
                                    });
                                }else {
                                    Log.e(TAG, "热门评论获取失败");
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
     * 初始化状态栏
     */
    private void initStateBar() {
        ImmersionBar.with(this).init();
        getSupportActionBar().hide();
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.imageView_back:
                finish();
                break;
        }
    }
}