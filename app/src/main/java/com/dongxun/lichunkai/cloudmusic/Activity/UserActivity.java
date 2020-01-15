package com.dongxun.lichunkai.cloudmusic.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dongxun.lichunkai.cloudmusic.Adapter.MainPagerAdapter;
import com.dongxun.lichunkai.cloudmusic.Adapter.PlayListAdapter;
import com.dongxun.lichunkai.cloudmusic.Bean.Creator;
import com.dongxun.lichunkai.cloudmusic.Bean.Song;
import com.dongxun.lichunkai.cloudmusic.Bean.SongSheet;
import com.dongxun.lichunkai.cloudmusic.Class.CircleImageView;
import com.dongxun.lichunkai.cloudmusic.Class.ResizableImageView;
import com.dongxun.lichunkai.cloudmusic.Common.Common;
import com.dongxun.lichunkai.cloudmusic.LocalBroadcast.SendLocalBroadcast;
import com.dongxun.lichunkai.cloudmusic.R;
import com.dongxun.lichunkai.cloudmusic.Util.CityAndCodeUtil;
import com.dongxun.lichunkai.cloudmusic.Util.ProvinceAndCodeUtil;
import com.dongxun.lichunkai.cloudmusic.Util.ToolHelper;
import com.dongxun.lichunkai.cloudmusic.Util.Year;
import com.gyf.immersionbar.ImmersionBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.dongxun.lichunkai.cloudmusic.Util.ToolHelper.betweenDate;
import static com.dongxun.lichunkai.cloudmusic.Util.ToolHelper.millisecondToDate;
import static com.dongxun.lichunkai.cloudmusic.Util.ToolHelper.showToast;

/**
 * 用户主页（自己和其他用户）
 */
public class UserActivity extends AppCompatActivity implements View.OnClickListener {

    private String TAG = "UserActivity";

    private ResizableImageView ResizableImageView_background;
    private CircleImageView CircleImageView_head;
    private TextView textView_nickname;
    private TextView textView_follow;
    private TextView textView_fans;
    private TextView textView_years;
    private TextView textView_level;
    private TextView textView_momentCount;
    private TextView textView_edit;
    private TextView textView_changeback;

    //viewpager相关
    private View view_index, view_moments;
    private ViewPager viewPager;  //对应的viewPager
    private List<View> viewList;//view数组
    private LinearLayout LinearLayout_index;
    private LinearLayout LinearLayout_moment;
    private TextView textView_index;
    private TextView textView_moment;

    //view_index组件
    private TextView textView_listenCount;
    private TextView textView_province;
    private TextView textView_city;
    private TextView textView_year;
    private TextView textView_constellation;
    private TextView textView_createTime;
    private TextView textView_likePlayList;
    private TextView textView_createPlayListCount;
    private TextView textView_likeCount;
    private TextView textView_likePlayCount;
    private TextView textView_createPlayListBeLike;
    private LinearLayout LinearLayout_moreInfo;

    private SongSheet songSheet_like = new SongSheet();//我喜欢的音乐

    private ArrayList<SongSheet> songSheetList_myCreate = new ArrayList<>();//我创建的歌单列表
    private RecyclerView recyclerView_createPlayList;
    private LinearLayoutManager linearLayoutManager_myCreate;//我创建的歌单列表相关
    private PlayListAdapter playListAdapter_myCreate;

    private ArrayList<SongSheet> songSheetList_collection = new ArrayList<>();//收藏的歌单列表
    private RecyclerView recyclerView_collection;
    private LinearLayoutManager linearLayoutManager_collection;//我创建的歌单列表相关
    private PlayListAdapter playListAdapter_collection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        initStateBar();
        initView();
        setViewpager();
        setAdapter();
        updateUI();
        getData();
    }

    /**
     * 设置Adapter
     */
    private void setAdapter() {
        //我创建的歌单
        linearLayoutManager_myCreate = new LinearLayoutManager(this);
        playListAdapter_myCreate = new PlayListAdapter(songSheetList_myCreate);
        recyclerView_createPlayList.setLayoutManager(linearLayoutManager_myCreate);
        recyclerView_createPlayList.setAdapter(playListAdapter_myCreate);
        playListAdapter_myCreate.setOnItemClickListener(new PlayListAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position) {
                //点击
                showToast(UserActivity.this,songSheetList_myCreate.get(position).getName());
            }
        });

        //我收藏的歌单
        linearLayoutManager_collection = new LinearLayoutManager(this);
        playListAdapter_collection = new PlayListAdapter(songSheetList_collection);
        recyclerView_collection.setLayoutManager(linearLayoutManager_collection);
        recyclerView_collection.setAdapter(playListAdapter_collection);
        playListAdapter_collection.setOnItemClickListener(new PlayListAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position) {
                //点击
                showToast(UserActivity.this,songSheetList_collection.get(position).getName());
            }
        });
    }

    /**
     * 获取页面数据
     */
    private void getData() {
        getUserInfo();
        getPlaylist();
//        getEvent();
    }

    /**
     * 获取用户动态
     */
    private void getEvent() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    final Request request = new Request.Builder()
                            .url("https://neteasecloudmusicapi.willdonner.top/user/event?uid="+ Common.user.getUserId() +"&limit=10&lasttime="+ System.currentTimeMillis() +"")
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
                                    Log.e(TAG, "onResponse: 用户动态获取成功");
                                    JSONArray events = newResponse.getJSONArray("events");
                                    for (int i=0;i<events.length();i++) {
                                        JSONObject event = events.getJSONObject(i);

                                    }
                                }else {
                                    Log.e(TAG, "onResponse: 用户动态获取失败");
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
     * 获取用户歌单
     */
    private void getPlaylist() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    final Request request = new Request.Builder()
                            .url("https://neteasecloudmusicapi.willdonner.top/user/playlist?uid="+ Common.user.getUserId() +"")
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
                                    Log.e(TAG, "onResponse: 歌单信息获取成功");
                                    JSONArray playlists = newResponse.getJSONArray("playlist");

                                    for (int i=0;i<playlists.length();i++){
                                        JSONObject playlist = playlists.getJSONObject(i);
                                        //用户id，判断歌单是属于用户创建还是用户收藏
                                        String userId = playlist.getString("userId");
                                        if (i==0){
                                            //用户喜欢的音乐
                                            String id = playlist.getString("id");//歌单id
                                            String trackCount = playlist.getString("trackCount");//歌曲数量
                                            String playCount = playlist.getString("playCount");//播放次数
                                            String description = playlist.getString("description");//歌单描述
                                            String coverImgUrl = playlist.getString("coverImgUrl");//封面图

                                            songSheet_like.setPlayCount(playCount);
                                            songSheet_like.setId(id);
                                            songSheet_like.setTrackCount(trackCount);
                                            songSheet_like.setDescription(description);
                                            songSheet_like.setPicUrl(coverImgUrl);
                                        }else if (userId.equals(Common.user.getUserId())){
                                            //用户创建的歌单
                                            String id = playlist.getString("id");//歌单id
                                            String trackCount = playlist.getString("trackCount");//歌曲数量
                                            String playCount = playlist.getString("playCount");//播放次数
                                            String description = playlist.getString("description");//歌单描述
                                            String coverImgUrl = playlist.getString("coverImgUrl");//封面图
                                            String name = playlist.getString("name");//歌单名
                                            String subscribedCount = playlist.getString("subscribedCount");//订阅数/收藏数

                                            SongSheet songSheet = new SongSheet();
                                            songSheet.setName(name);
                                            songSheet.setSubscribedCount(subscribedCount);
                                            songSheet.setPlayCount(playCount);
                                            songSheet.setId(id);
                                            songSheet.setTrackCount(trackCount);
                                            songSheet.setDescription(description);
                                            songSheet.setPicUrl(coverImgUrl);
                                            songSheetList_myCreate.add(songSheet);

                                        }else if (!userId.equals(Common.user.getUserId())) {
                                            //用户收藏的歌单
                                            String id = playlist.getString("id");//歌单id
                                            String trackCount = playlist.getString("trackCount");//歌曲数量
                                            String playCount = playlist.getString("playCount");//播放次数
                                            String description = playlist.getString("description");//歌单描述
                                            String coverImgUrl = playlist.getString("coverImgUrl");//封面图
                                            String name = playlist.getString("name");//歌单名
                                            String subscribedCount = playlist.getString("subscribedCount");//订阅数/收藏数

                                            SongSheet songSheet = new SongSheet();
                                            songSheet.setName(name);
                                            songSheet.setSubscribedCount(subscribedCount);
                                            songSheet.setPlayCount(playCount);
                                            songSheet.setId(id);
                                            songSheet.setTrackCount(trackCount);
                                            songSheet.setDescription(description);
                                            songSheet.setPicUrl(coverImgUrl);

                                            //创建人
                                            JSONObject ison_creator = playlist.getJSONObject("creator");
                                            String creator_province = ison_creator.getString("province");
                                            String creator_avatarUrl = ison_creator.getString("avatarUrl");
                                            String creator_gender = ison_creator.getString("gender");
                                            String creator_city = ison_creator.getString("city");
                                            String creator_birthday = ison_creator.getString("birthday");
                                            String creator_userId = ison_creator.getString("userId");
                                            String creator_nickname = ison_creator.getString("nickname");
                                            String creator_signature = ison_creator.getString("signature");
                                            String creator_description = ison_creator.getString("description");
                                            String creator_backgroundUrl = ison_creator.getString("backgroundUrl");

                                            Creator creator = new Creator();
                                            creator.setProvince(creator_province);
                                            creator.setAvatarUrl(creator_avatarUrl);
                                            creator.setGender(creator_gender);
                                            creator.setCity(creator_city);
                                            creator.setBirthday(creator_birthday);
                                            creator.setUserId(creator_userId);
                                            creator.setNickname(creator_nickname);
                                            creator.setSignature(creator_signature);
                                            creator.setDescription(creator_description);
                                            creator.setBackgroundUrl(creator_backgroundUrl);
                                            songSheet.setCreator(creator);
                                            songSheetList_collection.add(songSheet);
                                        }
                                    }
                                    //刷新UI
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            //喜欢的音乐
                                            textView_likeCount.setText(songSheet_like.getTrackCount());
                                            textView_likePlayCount.setText(songSheet_like.getPlayCount());
                                            //我创建的歌单
                                            playListAdapter_myCreate.notifyDataSetChanged();
                                            textView_createPlayListCount.setText(songSheetList_myCreate.size()+"");
                                            //计算我创建的歌单被收藏次数
                                            int x = 0;
                                            for (SongSheet songSheet:songSheetList_myCreate){
                                                x = x + Integer.parseInt(songSheet.getSubscribedCount());
                                            }
                                            textView_createPlayListBeLike.setText(x+"");
                                            //我收藏的歌单
                                            playListAdapter_collection.notifyDataSetChanged();
                                            textView_likePlayList.setText(songSheetList_collection.size()+"");
                                        }
                                    });
                                }else {
                                    Log.e(TAG, "onResponse: 歌单信息获取失败");
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

    private void updateUI() {
        textView_nickname.setText(Common.user.getNickname());
        CircleImageView_head.setImageBitmap(Common.user.getAvatarUrl_bitmap());
        ResizableImageView_background.setImageBitmap(Common.user.getBackgroundUrl_bitmap());
    }

    /**
     * 获取用户信息
     */
    private void getUserInfo() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    OkHttpClient client = new OkHttpClient();//新建一个OKHttp的对象
                    Request request = new Request.Builder()
                            .url("https://neteasecloudmusicapi.willdonner.top/user/detail?uid="+ Common.user.getUserId() +"")
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
                                    Log.e(TAG, "用户详情获取成功"+Common.user.getUserId());
                                    final String level = newRes.getString("level");//等级
                                    final String listenSongs = newRes.getString("listenSongs");//累计听歌
                                    JSONObject profile = newRes.getJSONObject("profile");
                                    final String nickname = profile.getString("nickname");//昵称
                                    final String province = profile.getString("province");//省份
                                    String vipType = profile.getString("vipType");//vip类型
                                    final String birthday = profile.getString("birthday");//生日
                                    String gender = profile.getString("gender");//性别
                                    final String avatarUrl = profile.getString("avatarUrl");//头像URL
                                    String userType = profile.getString("userType");//用户类型
                                    final String city = profile.getString("city");//城市
                                    final String backgroundUrl = profile.getString("backgroundUrl");//背景URl
                                    String signature = profile.getString("signature");//个性签名
                                    final String follows = profile.getString("follows");//关注
                                    final String followeds = profile.getString("followeds");//粉丝
                                    final String eventCount = profile.getString("eventCount");//动态数量
                                    final String createTime = profile.getString("createTime");//注册时间

                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            textView_momentCount.setText(eventCount);
                                            textView_nickname.setText(nickname);
                                            textView_follow.setText(follows);
                                            textView_fans.setText(followeds);
                                            textView_level.setText("Lv."+level);
                                            textView_years.setText(millisecondToDate(new Long(birthday)).substring(2,3)+"0后");//年代
                                            //view_index组件UI
                                            textView_listenCount.setText(listenSongs);
                                            textView_province.setText(ProvinceAndCodeUtil.getCityByCode(province.substring(0,2)));
                                            textView_city.setText(CityAndCodeUtil.getCityByCode(city.substring(0,4)));
                                            textView_year.setText(millisecondToDate(new Long(birthday)).substring(2,3)+"0后");
                                            int month = Integer.parseInt(millisecondToDate(new Long(birthday)).substring(5,7));
                                            int day = Integer.parseInt(millisecondToDate(new Long(birthday)).substring(8,10));
                                            textView_constellation.setText(Year.getConstellation(month,day));
                                            textView_createTime.setText(betweenDate(createTime).getYear()+"年"+"（"+millisecondToDate(new Long(createTime))+"注册）");
                                        }
                                    });

                                    //头像
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            URL imageurl = null;
                                            try {
                                                imageurl = new URL(avatarUrl);
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
                                                CircleImageView_head.post(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        CircleImageView_head.setImageBitmap(bitmap);
                                                        Common.user.setAvatarUrl_bitmap(bitmap);
                                                        Common.user.setAvatarUrl(avatarUrl);
                                                    }
                                                });
                                                is.close();
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }).start();

                                    //背景
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            URL imageurl = null;
                                            try {
                                                imageurl = new URL(backgroundUrl);
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
                                                ResizableImageView_background.post(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        ResizableImageView_background.setImageBitmap(bitmap);
                                                        Common.user.setBackgroundUrl_bitmap(bitmap);
                                                        Common.user.setBackgroundUrl(backgroundUrl);
                                                    }
                                                });
                                                is.close();
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }).start();


                                }else {
                                    Log.e(TAG, "用户详情获取失败");
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

    private void initView() {
        ResizableImageView_background = findViewById(R.id.ResizableImageView_background);
        ResizableImageView_background.setOnClickListener(this);
        CircleImageView_head = findViewById(R.id.CircleImageView_head);
        CircleImageView_head.setOnClickListener(this);
        textView_nickname = findViewById(R.id.textView_nickname);
        textView_follow = findViewById(R.id.textView_follow);
        textView_fans = findViewById(R.id.textView_fans);
        textView_years = findViewById(R.id.textView_years);
        textView_level = findViewById(R.id.textView_level);
        textView_momentCount = findViewById(R.id.textView_momentCount);
        textView_edit = findViewById(R.id.textView_edit);
        textView_edit.setOnClickListener(this);
        textView_changeback = findViewById(R.id.textView_changeback);
        textView_changeback.setOnClickListener(this);

        //实例化view
        viewPager = findViewById(R.id.viewpager);
        LayoutInflater inflater=getLayoutInflater();
        view_index = inflater.inflate(R.layout.viewpager_user_index, null);
        view_moments = inflater.inflate(R.layout.viewpager_user_moments,null);

        //组件
        textView_index = findViewById(R.id.textView_index);
        textView_moment = findViewById(R.id.textView_moment);
        LinearLayout_index = findViewById(R.id.LinearLayout_index);
        LinearLayout_index.setOnClickListener(this);
        LinearLayout_moment = findViewById(R.id.LinearLayout_moment);
        LinearLayout_moment.setOnClickListener(this);

        //view_index组件
        textView_listenCount = view_index.findViewById(R.id.textView_listenCount);
        textView_province = view_index.findViewById(R.id.textView_province);
        textView_city = view_index.findViewById(R.id.textView_city);
        textView_year = view_index.findViewById(R.id.textView_year);
        textView_constellation = view_index.findViewById(R.id.textView_constellation);
        textView_createTime = view_index.findViewById(R.id.textView_createTime);
        textView_likePlayList = view_index.findViewById(R.id.textView_likePlayList);
        textView_createPlayListCount = view_index.findViewById(R.id.textView_createPlayListCount);
        textView_likeCount = view_index.findViewById(R.id.textView_likeCount);
        textView_likePlayCount = view_index.findViewById(R.id.textView_likePlayCount);
        recyclerView_createPlayList = view_index.findViewById(R.id.recyclerView_createPlayList);
        recyclerView_collection = view_index.findViewById(R.id.recyclerView_collection);
        textView_createPlayListBeLike = view_index.findViewById(R.id.textView_createPlayListBeLike);
        LinearLayout_moreInfo = view_index.findViewById(R.id.LinearLayout_moreInfo);
        LinearLayout_moreInfo.setOnClickListener(this);
    }

    /**
     * 设置Viewpager
     */
    private void setViewpager() {
        viewList = new ArrayList<View>();// 将要分页显示的View装入数组中
        viewList.add(view_index);
        viewList.add(view_moments);

        //设置PagerAdapter
        MainPagerAdapter mainPagerAdapter = new MainPagerAdapter(viewList,this);
        viewPager.setAdapter(mainPagerAdapter);
        //监听
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            /**
             * 页面滑动状态停止前一直调用
             *
             * @param position 当前点击滑动页面的位置
             * @param positionOffset 当前页面偏移的百分比
             * @param positionOffsetPixels 当前页面偏移的像素位置
             */
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//                Log.e("vp", "滑动中=====position:" + position + "   positionOffset:" + positionOffset + "   positionOffsetPixels:" + positionOffsetPixels);
            }

            /**
             * 滑动后显示的页面和滑动前不同，调用
             *
             * @param position 选中显示页面的位置
             */
            @Override
            public void onPageSelected(int position) {
                Log.e(TAG, "显示页改变=====postion:" + position);
                //设置标题样式
                switch (position){
                    case 0:
                        textView_index.setTextColor(Color.parseColor("#fe3a3b"));
                        textView_moment.setTextColor(Color.parseColor("#000000"));
                        break;
                    case 1:
                        textView_index.setTextColor(Color.parseColor("#000000"));
                        textView_moment.setTextColor(Color.parseColor("#fe3a3b"));
                        break;
                }
            }

            /**
             * 页面状态改变时调用
             * @param state 当前页面的状态
             *              SCROLL_STATE_IDLE：空闲状态
             *              SCROLL_STATE_DRAGGING：滑动状态
             *              SCROLL_STATE_SETTLING：滑动后滑翔的状态
             */
            @Override
            public void onPageScrollStateChanged(int state) {
//                switch (state) {
//                    case ViewPager.SCROLL_STATE_IDLE:
//                        Log.e("vp", "状态改变=====SCROLL_STATE_IDLE====静止状态");
//                        break;
//                    case ViewPager.SCROLL_STATE_DRAGGING:
//                        Log.e("vp", "状态改变=====SCROLL_STATE_DRAGGING==滑动状态");
//                        break;
//                    case ViewPager.SCROLL_STATE_SETTLING:
//                        Log.e("vp", "状态改变=====SCROLL_STATE_SETTLING==滑翔状态");
//                        break;
//                }
            }
        });
    }

    /**
     * 初始化状态栏
     */
    private void initStateBar() {
        ImmersionBar.with(this).init();
        getSupportActionBar().hide();
//        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);//黑色状态栏字体
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);//白色状态栏字体
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.LinearLayout_index:
                viewPager.setCurrentItem(0);
                break;
            case R.id.LinearLayout_moment:
                viewPager.setCurrentItem(1);
                break;
            case R.id.ResizableImageView_background:
                //查看背景

                break;
            case R.id.CircleImageView_head:
                //查看头像

                break;
            case R.id.LinearLayout_moreInfo:
                //更多个人信息
                showToast(this,"更多个人信息");
                break;
            case R.id.textView_edit:
                //编辑资料
                showToast(this,"编辑资料");
                Intent intent = new Intent(this,EditMyInfoActivity.class);
                startActivity(intent);
                break;
            case R.id.textView_changeback:
                //更换背景
                showToast(this,"更换背景");
                break;
        }
    }
}
