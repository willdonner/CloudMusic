package com.dongxun.lichunkai.cloudmusic.Activity;

import androidx.activity.ComponentActivity;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dongxun.lichunkai.cloudmusic.Adapter.CommentAdapter;
import com.dongxun.lichunkai.cloudmusic.Bean.Comment;
import com.dongxun.lichunkai.cloudmusic.Bean.Song;
import com.dongxun.lichunkai.cloudmusic.Bean.User;
import com.dongxun.lichunkai.cloudmusic.Class.BaseActivity;
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
public class CommentActivity extends BaseActivity implements View.OnClickListener {

    private String TAG = "CommentActivity";

    private RecyclerView recyclerView_comment;
    private ImageView imageView_back;
    private TextView textView_title;
    private TextView textView_total;
    private LinearLayout LinearLayout_loading;

    private LinearLayoutManager linearLayoutManager;
    private CommentAdapter commentAdapter;
    private ArrayList<Comment> commentList = new ArrayList<>();

    private Boolean HotModel = false;//精彩评论模式

    private String sourceID = "";//资源id
    private int scrollY = 0;//recycleView滑动的距离
    private int page = 1;//获取的评论页码
    private Boolean isRequest = false;
    private int limit = 20;//每次请求数据条数
    private Boolean isAll = false;

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
        LinearLayout_loading = findViewById(R.id.LinearLayout_loading);
    }

    private void setAdapter() {
        //热门评论Adapter
        linearLayoutManager = new LinearLayoutManager(this);
        commentAdapter = new CommentAdapter(commentList);
        recyclerView_comment.setLayoutManager(linearLayoutManager);
        recyclerView_comment.setAdapter(commentAdapter);
        //滑动监听
        recyclerView_comment.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                //onScrollStateChanged 方法
                RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
                //判断是当前layoutManager是否为LinearLayoutManager
                //只有LinearLayoutManager才有查找第一个和最后一个可见view位置的方法
                if (layoutManager instanceof LinearLayoutManager) {
                    LinearLayoutManager linearManager = (LinearLayoutManager) layoutManager;
                    //获取最后一个可见view的位置
                    int lastItemPosition = linearManager.findLastVisibleItemPosition();
                    //获取第一个可见view的位置
                    int firstItemPosition = linearManager.findFirstVisibleItemPosition();
                    Log.e(TAG, "最后一个可见view的位置: "+lastItemPosition);
                    Log.e(TAG, "第一个可见view的位置: "+firstItemPosition);
                    if (lastItemPosition == commentList.size()-1){
                        if (isRequest) return;
                        if (isAll) return;
                        //加载新数据
                        showToast(CommentActivity.this,"加载新数据");
                        page = commentList.size()-15/10+1;
                        if (HotModel){
                            //热评模式
                            getHotComment(sourceID,0,limit,page);
                        }else {
                            getMusicComment(sourceID,limit,page);
                        }
                    }
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                switch (recyclerView.getId()){
                    case R.id.recyclerView_comment:
                        //记录滑动的总距离
                        scrollY = scrollY + dy;
                        break;
                }
            }
        });
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
                intent.putExtra("id",Common.song_playing.getId());
                intent.putExtra("type",0);
                intent.putExtra("limit",20);
                intent.putExtra("hotModel",true);
                startActivity(intent);
            }
        });
    }

    private void getdata() {
        //获取参数（id,type,limit）
        sourceID = getIntent().getStringExtra("id");
        int type = getIntent().getIntExtra("type",0);
        HotModel = getIntent().getBooleanExtra("hotModel",false);
        Log.e(TAG, "getdata: "+sourceID);
        Log.e(TAG, "getdata: "+type);
        Log.e(TAG, "getdata: "+HotModel);

        //热评模式
        if (HotModel){
            textView_title.setText("精彩评论(");
            getHotComment(sourceID,type,limit,page);
        }
        //获取评论（根据资源类型使用对应方法）
        if (!HotModel){
            switch (type){
                case 0:
                    //歌曲评论
                    getMusicComment(sourceID,limit,page);
                    break;
            }
        }
    }

    /**
     * 获取歌曲评论(包括15条热门评论和规定条最新评论)
     * @param id 歌曲id
     * @param limit 取出评论数量 , 默认为 20
     * @param page 页码，offset:偏移数量 , 用于分页 , 如 :( 评论页数 -1)*20, 其中 20 为 limit 的值
     */
    private void getMusicComment(final String id, final int limit,final int page) {
        if (isRequest) return;
        isRequest = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    OkHttpClient client = new OkHttpClient();//新建一个OKHttp的对象
                    Request request = new Request.Builder()
                            .url("https://neteasecloudmusicapi.willdonner.top/comment/music?id="+ id +"&limit="+ limit +"&offset="+ (page-1)*limit +"")
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
                                    if (page==1){
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
                                    }
                                    //解析最新评论
                                    final JSONArray comments = newRes.getJSONArray("comments");
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
                                        if (i==0 && page==1){
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
                                            if (comments.length()==0) {
                                                commentList.get(commentList.size()-1).setAllData(true);
                                                isAll = true;
                                            }
                                            if (commentList.size()!=0) LinearLayout_loading.setVisibility(View.GONE);
                                            textView_total.setText(total);
                                            commentAdapter.notifyDataSetChanged();
                                            for (Comment comment:commentList)
                                                Log.e(TAG, "run: "+comment.getCommentId());
                                            isRequest = false;
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
    private void getHotComment(final String id, final int type, final int limit,final int page) {
        if (isRequest) return;
        isRequest = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    OkHttpClient client = new OkHttpClient();//新建一个OKHttp的对象
                    Request request = new Request.Builder()
                            .url("https://neteasecloudmusicapi.willdonner.top/comment/hot?id="+ id +"&type="+ type +"&limit="+ limit +"&offset="+ (page-1)*limit +"")
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
                                    //是否还有更多
                                    isAll = newRes.getBoolean("hasMore");
                                    //解析热门评论
                                    final JSONArray hotComments = newRes.getJSONArray("hotComments");
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
                                        comment.setShowNew(!isAll);
                                        comment.setShowHot(false);
                                        comment.setShowAllHot(false);
                                        commentList.add(comment);
                                    }
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (isAll) {
                                                commentList.get(commentList.size()-1).setAllData(true);
                                            }
                                            if (commentList.size()!=0) LinearLayout_loading.setVisibility(View.GONE);
                                            textView_total.setText(total);
                                            commentAdapter.notifyDataSetChanged();
                                            isRequest = false;
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