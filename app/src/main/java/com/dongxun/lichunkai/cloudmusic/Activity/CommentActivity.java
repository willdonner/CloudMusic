package com.dongxun.lichunkai.cloudmusic.Activity;

import androidx.activity.ComponentActivity;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.dongxun.lichunkai.cloudmusic.Adapter.CommentAdapter;
import com.dongxun.lichunkai.cloudmusic.Bean.Comment;
import com.dongxun.lichunkai.cloudmusic.Bean.Song;
import com.dongxun.lichunkai.cloudmusic.Bean.User;
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
public class CommentActivity extends AppCompatActivity {

    private String TAG = "CommentActivity";

    private RecyclerView recyclerView_hotComment;

    private LinearLayoutManager linearLayoutManager;
    private CommentAdapter commentAdapter;
    private ArrayList<Comment> comments = new ArrayList<>();

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
        recyclerView_hotComment = findViewById(R.id.recyclerView_hotComment);
    }

    private void setAdapter() {
        linearLayoutManager = new LinearLayoutManager(this);
        commentAdapter = new CommentAdapter(comments);
        recyclerView_hotComment.setLayoutManager(linearLayoutManager);
        recyclerView_hotComment.setAdapter(commentAdapter);
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
                //c
                showToast(CommentActivity.this,"查看回复");
            }
        });
    }

    private void getdata() {
        //获取参数（id,type,limit）
        String id = getIntent().getStringExtra("id");
        int type = getIntent().getIntExtra("type",0);
        int limit = getIntent().getIntExtra("limit",10);
        Log.e(TAG, "getdata: "+id);
        Log.e(TAG, "getdata: "+type);
        Log.e(TAG, "getdata: "+limit);

        getHotComment(id,type,limit);
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
                                    Log.e(TAG, "banner获取成功");
                                    JSONArray hotComments = newRes.getJSONArray("hotComments");
                                    for (int i=0;i<hotComments.length();i++){
                                        JSONObject user = hotComments.getJSONObject(i).getJSONObject("user");
                                        User user1 = new User();
                                        user1.setUserId(user.getString("userId"));
                                        user1.setNickname(user.getString("nickname"));
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
                                        comments.add(comment);
                                    }
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            commentAdapter.notifyDataSetChanged();
                                        }
                                    });
                                }else {
                                    Log.e(TAG, "banner获取失败");
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
}
